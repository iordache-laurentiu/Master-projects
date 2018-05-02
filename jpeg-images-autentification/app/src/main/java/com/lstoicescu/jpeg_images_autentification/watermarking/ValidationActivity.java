package com.lstoicescu.jpeg_images_autentification.watermarking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lstoicescu.jpeg_images_autentification.MainActivity;
import com.lstoicescu.jpeg_images_autentification.R;
import com.lstoicescu.jpeg_images_autentification.watermarking.controller.WatermarkController;
import com.lstoicescu.jpeg_images_autentification.watermarking.entity.Watermark;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidationActivity extends AppCompatActivity {
    private static final String TAG = "VALIDATION_ACTIVITY";
    private static final int PERMISSION_REQUEST_WRITE_IMAGES = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    private static Context context;

    private static ImageView imageViewResult;
    private static Button buttonSaveResult;

    private Bitmap result = null;

    public static void showMessage(String message){
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        context =  getApplicationContext();
        init();
    }

    public void init() {
        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        buttonSaveResult = (Button) findViewById(R.id.buttonSaveResult);
        dispatchReadPictureIntent();
        onButtonSaveResultPressed();
    }

    public void onButtonSaveResultPressed(){
        buttonSaveResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                saveImage(result);
                Intent intent = new Intent("com.lstoicescu.jpeg_images_autentification.MainActivity");
                startActivity(intent);
            }
        });
    }

    /**
     * This method asks the user to provide permission for the application to write data on memory
     * photos, more precisely.
     */
    public void checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_WRITE_IMAGES);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_IMAGES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permission granted!");
                } else {
                    System.out.println("Permission denied!");
                }
                return;
            }
        }
    }


    public void dispatchReadPictureIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                result = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d(TAG, String.valueOf(result));

                Mat img = new Mat();
                Utils.bitmapToMat(result, img);
                WatermarkController wm = new WatermarkController();
                img = wm.insertWatermarkIntoImage(img, new Watermark(MainActivity.getWatermark()), false);
                Utils.matToBitmap(img, result);

                imageViewResult.setImageBitmap(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the marked image in memory using a non collision naming
     *
     * @param finalBitmap
     */
    private void saveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        String image_name = getDateTime();
        File myDir = new File(root);
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        Log.i("LOAD", root + fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a part of the photo's name.
     *
     * @return
     */
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
