package com.envision.lstoicescu.sms_reader.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class SmsController {
    private static SmsController singleton;
    private List<SmsPOJO> smsList = new ArrayList<>();

    private SmsController() {

    }

    public static SmsController getInstance() {
        if (singleton == null) {
            singleton = new SmsController();
        }
        return singleton;
    }

    public void populate(Context context){


        // get messages from phone.
        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur != null && cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            sms.add("Number: " + address + " .Message: " + body);
            Toast.makeText(context.getApplicationContext(), body, Toast.LENGTH_LONG).show();

        }

        if (cur != null) {
            cur.close();
        }
//        return sms;
    }


}
