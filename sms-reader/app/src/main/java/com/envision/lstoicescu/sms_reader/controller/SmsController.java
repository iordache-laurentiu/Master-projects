package com.envision.lstoicescu.sms_reader.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.envision.lstoicescu.sms_reader.dto.SmsPOJO;

import java.util.ArrayList;
import java.util.Date;
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

    public void populate(Context context) {
        // get messages from phone.
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur != null && cur.moveToNext()) {
            String sender = cur.getString(cur.getColumnIndex("address"));
            String message = cur.getString(cur.getColumnIndexOrThrow("body"));
            long millisecond = Long.parseLong(cur.getString(cur.getColumnIndex("date")));
            String date = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();

            smsList.add(new SmsPOJO(sender, message, date));
        }

        if (cur != null) {
            cur.close();
        }
    }

    public List<SmsPOJO> getSmsList() {
        return smsList;
    }
}
