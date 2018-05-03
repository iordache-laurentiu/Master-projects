package com.envision.lstoicescu.sms_reader.controller;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.envision.lstoicescu.sms_reader.MainActivity;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class PermissionController {
    private static PermissionController singleton;

    private PermissionController() {

    }

    public static PermissionController getInstance() {
        if (singleton == null) {
            singleton = new PermissionController();
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
