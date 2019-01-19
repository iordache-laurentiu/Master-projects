var express = require("express");
var url = require("url");
var bodyParser = require('body-parser');
var randomstring = require("randomstring");
var cons = require('consolidate');
var nosql = require('nosql').load('database.nosql');
var querystring = require('querystring');
var __ = require('underscore');
__.string = require('underscore.string');

var app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true })); // support form-encoded bodies (for the token endpoint)

app.engine('html', cons.underscore);
app.set('view engine', 'html');
app.set('views', 'files/authorizationServer');
app.set('json spaces', 4);

// authorization server information
var authServer = {
	authorizationEndpoint: 'http://localhost:9001/authorize',
	tokenEndpoint: 'http://localhost:9001/token'
};

/////////////////////////////////////// Client registration ///////////////////////////////////////

/** client information
	This will act as a data store for all client information. This type of data is usually stored in a database.
 */
var clients = [
	{
        "client_id": "oauth-client-1",
        "client_secret": "oauth-client-secret-1",
        "redirect_uris": ["http://localhost:9000/callback"],
        "scope": ""
	}
];

var codes = {};

var requests = {};

// doing a simple linear search across the list for the client with the given client ID
var getClient = function(clientId) {
	return __.find(clients, function(client) { return client.client_id == clientId; });
};

app.get('/', function(req, res) {
	res.render('index', {clients: clients, authServer: authServer});
});


/////////////////////////////////////// Authorize a client ///////////////////////////////////////
app.get("/authorize", function(req, res){
	console.log('INFO: Authorize endpoint');

    // Figuring out which client is making the request.
    var client = getClient(req.query.client_id);

    // Sanity checks
    if(!client){
        console.log('ERROR: Unknown client: %s', req.query.client_id);
        res.render('error', {error: 'Unknown client'});
    } else if (!__.contains(client.redirect_uris, req.query.redirect_uri)) {
        console.log('Mismatched redirect URI, expected: %s, got: %s', client.redirect_uris, req.query.redirect_uri);
        res.render('error', {error: 'Invalid redirect URI'});
        return;
    } else {

        // Save the request for further usage (after the user ends it's interaction)
        var reqid = randomstring.generate(8);
        requests[reqid] = req.query;

        // Show the client information in authorization page.
        res.render('approve', {client: client, reqid: reqid});
        return;
    }
	
});

app.post('/approve', function(req, res) {
    console.log('INFO: User approval.');

    var reqid = req.body.reqid;
    var query = requests[reqid];
    delete requests[reqid];

    // Sanity check for the request id
    if (!query) {
    	console.log('ERROR: The request id is not found.');
        res.render('error', {error: 'No matching authorization request'});
        return;
    }

    if(req.body.approve){
        console.log('INFO: The user approved authorization for client: %s.', query.client_id);

        if(query.response_type == 'code') {
            console.log('INFO: Generate authorization code for client: %s.', query.client_id);

            // Generate authorization code
            var code = randomstring.generate(8);

            codes[code] = { request: query };

            var urlParsed = buildUrl(query.redirect_uri, {
                code: code,
                state: query.state
            });
            res.redirect(urlParsed);
            return;

		} else {
            console.log('INFO: The required grants by the client: %s are unknown.', query.client_id);

            // Send back an error message if the grants are unknown
            var urlParsed = buildUrl(query.redirect_uri, {
                error: 'unsupported_response_type'
            });
            res.redirect(urlParsed);
            return;

		}
	} else {
        console.log('INFO: The user denied authorization for client: %s.', query.client_id);

        // Send back an error message
        var urlParsed = buildUrl(query.redirect_uri, {
            error: 'access_denied'
        });
        res.redirect(urlParsed);
        return;
	}

});

/////////////////////////////////////// Issuing a token ///////////////////////////////////////
app.post("/token", function(req, res){
    console.log('INFO: Token endpoint');

    // Check the authorization header
    var auth = req.headers['authorization'];
    if (auth) {
        var clientCredentials = decodeClientCredentials(auth);
        var clientId = clientCredentials.id;
        var clientSecret = clientCredentials.secret;
		console.log('INFO: Received client credentials through authorization header: id: %s, secret: %s', clientId, clientSecret);
    }

    // Check the form body
    if (req.body.client_id) {
        if (clientId) {
            console.log('ERROR: Received client credentials through both methods.');
            res.status(401).json({error: 'invalid_client'});
            return;
        }
        var clientId = req.body.client_id;
        var clientSecret = req.body.client_secret;
        console.log('INFO: Received client credentials through form body: id=%s, secret=%s', clientId, clientSecret);
    }

    // Search the client in the clients database
    var client = getClient(clientId);
    if (!client) {
        console.log('ERROR: No client found in database with the id: %s', clientId);
        res.status(401).json({error: 'invalid_client'});
        return;
    }

    // Check the credential
    if (client.client_secret != clientSecret) {
        console.log('ERROR: Invalid client credentials for client: %s', clientId);
        res.status(401).json({error: 'invalid_client'});
        return;
    }

    // Check the grant type
	if(req.body.grant_type == 'authorization_code') {
    	var code = codes[req.body.code];

		// Check it exist the given authorization code
		if(code){
			delete codes[req.body.code];

			// Check it was issued to the right client
			if(code.request.client_id == clientId){
                console.log('INFO: Generating access token for client %s', clientId);
                var access_token = randomstring.generate();
                nosql.insert({ access_token: access_token, client_id: clientId });
			} else {
                console.log('ERROR: Invalid client id for the given authorisation code: %s. Received client_id: %s, expected: %s', req.body.code, clientId, code.request.client_id);
                res.status(400).json({error: 'invalid_grant'});
                return;
            }
        } else{
            console.log('ERROR: No client found for the given authorisation code: %s', req.body.code);
            res.status(400).json({error: 'invalid_grant'});
            return;
        }
	} else {
        console.log('ERROR: Unknown grant type');
		res.status(400).json({error: 'unsupported_grant_type'});
		return;
    }

    console.log('INFO: Forwarding the token: %s to client: %s', access_token, clientId);
    var token_response = { access_token: access_token, token_type: 'Bearer' };
    res.status(200).json(token_response);

});

var buildUrl = function(base, options, hash) {
	var newUrl = url.parse(base, true);
	delete newUrl.search;
	if (!newUrl.query) {
		newUrl.query = {};
	}
	__.each(options, function(value, key, list) {
		newUrl.query[key] = value;
	});
	if (hash) {
		newUrl.hash = hash;
	}
	
	return url.format(newUrl);
};

/**
 * The Authorization header in HTTP Basic is a base64 encoded string made by
 * concatenating the username and password together,
 * separated by a single colon (:) character.
 */
var decodeClientCredentials = function(auth) {
	var clientCredentials = new Buffer(auth.slice('basic '.length), 'base64').toString().split(':');
	var clientId = querystring.unescape(clientCredentials[0]);
	var clientSecret = querystring.unescape(clientCredentials[1]);	
	return { id: clientId, secret: clientSecret };
};

app.use('/', express.static('files/authorizationServer'));

// clear the database
nosql.clear();

var server = app.listen(9001, 'localhost', function () {
  var host = server.address().address;
  var port = server.address().port;

  console.log('OAuth Authorization Server is listening at http://%s:%s', host, port);
});
 
