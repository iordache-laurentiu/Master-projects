var express = require("express");
var request = require("sync-request");
var url = require("url");
var qs = require("qs");
var querystring = require('querystring');
var cons = require('consolidate');
var randomstring = require("randomstring");
var __ = require('underscore');
__.string = require('underscore.string');

var app = express();

app.engine('html', cons.underscore);
app.set('view engine', 'html');
app.set('views', 'files/client');

// authorization server information
var authServer = {
    authorizationEndpoint: 'http://localhost:9001/authorize',
    tokenEndpoint: 'http://localhost:9001/token'
};

// client information
var client = {
    "client_id": "oauth-client-1",
    "client_secret": "oauth-client-secret-1",
    "redirect_uris": ["http://localhost:9000/callback"]
};

var protectedResource = 'http://localhost:9002/resource';

var state = null;

var access_token = null;
var scope = null;

app.get('/', function (req, res) {
    res.render('index', {access_token: access_token, scope: scope});
});

app.get('/authorize', function (req, res) {
	console.log('INFO: Redirect the user to the authorization server.');

	// Generate the state code
    state = randomstring.generate();

    // Build the redirect URL
    var authorizeUrl = buildUrl(authServer.authorizationEndpoint, {
        response_type: 'code',
        client_id: client.client_id,
        redirect_uri: client.redirect_uris[0],
        state: state
    });

    console.log("INFO: Redirecting the user to the authorization server.")
    res.redirect(authorizeUrl);
});

app.get('/callback', function (req, res) {
	console.log('INFO: Received data from the authorization served.')

    if (req.query.state != state) {
        console.log('ERROR: Received wrong state code. Received: %s, expected: %s', req.query.state, state);
        res.render('error', {error: 'State value did not match'});
        return;
    }

    var code = req.query.code;
	if(!code){
        console.log('ERROR: Received unexpected message');
        res.render('error', {error: 'Received unexpected message'});
        return;
	}
	console.log("INFO: Received the authorization code: %s", code);

	// Process the received authorization code
    var form_data = qs.stringify({
        grant_type: 'authorization_code',
        code: code,
        redirect_uri: client.redirect_uris[0]
    });

    // Headers to specify that this is an HTTP form-encoded and the encoded credentials.
    var headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'Authorization': 'Basic ' + encodeClientCredentials(client.client_id, client.client_secret)
    };


    console.log('INFO: Requesting the access token.');
    var tokRes = request('POST', authServer.tokenEndpoint,
        {
            body: form_data,
            headers: headers
        }
    );

    var body = JSON.parse(tokRes.getBody());
    access_token = body.access_token;
    console.log('INFO: Received OAuth access token: %s', access_token);

    res.render('index', {access_token: body.access_token, scope: scope});
});

app.get('/fetch_resource', function (req, res) {
	console.log('INFO: Accessing the protected resource');

    if (!access_token) {
        console.log('ERROR: Missing access token');
        res.render('error', {error: 'Missing Access Token'});
    }
    console.log('INFO: Making request with access token: %s', access_token);

    var headers = {
        'Authorization': 'Bearer ' + access_token
    };
    var resource = request('POST', protectedResource,
        {headers: headers}
    );

    if (resource.statusCode >= 200 && resource.statusCode < 300) {
    	console.log('INFO: Received data from protected resource.');
        var body = JSON.parse(resource.getBody());
        res.render('data', {resource: body});
        return;
    } else {
    	console.log('ERROR: Protected resource error code received: %s', resource.statusCode);
        access_token = null;
        res.render('error', {error: resource.statusCode});
        return;
    }

});

// This function builds the URL in a proper form
var buildUrl = function (base, options, hash) {
    var newUrl = url.parse(base, true);
    delete newUrl.search;
    if (!newUrl.query) {
        newUrl.query = {};
    }
    __.each(options, function (value, key, list) {
        newUrl.query[key] = value;
    });
    if (hash) {
        newUrl.hash = hash;
    }

    return url.format(newUrl);
};

var encodeClientCredentials = function (clientId, clientSecret) {
    return new Buffer(querystring.escape(clientId) + ':' + querystring.escape(clientSecret)).toString('base64');
};

app.use('/', express.static('files/client'));

var server = app.listen(9000, 'localhost', function () {
    var host = server.address().address;
    var port = server.address().port;
    console.log('OAuth Client is listening at http://%s:%s', host, port);
});
 
