package com.envision.lstoicescu.sms_reader.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.envision.lstoicescu.sms_reader.activities.MainActivity;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class PermissionHandler {
    private static PermissionHandler singleton;

    private PermissionHandler() {

    }

    public static PermissionHandler getInstance() {
        if (singleton == null) {
            singleton = new PermissionHandler();
        }
        return singleton;
    }

    public void readSmsPermission(MainActivity context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                        Manifest.permission.READ_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.READ_SMS},
                            1);
                }
            }
        }
    }
}
