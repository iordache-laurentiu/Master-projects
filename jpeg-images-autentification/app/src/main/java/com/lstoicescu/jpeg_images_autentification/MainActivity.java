package com.lstoicescu.jpeg_images_autentification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lstoicescu.jpeg_images_autentification.login.utils.Crypt;
import com.lstoicescu.jpeg_images_autentification.watermarking.controller.WatermarkController;
import com.lstoicescu.jpeg_images_autentification.watermarking.entity.Watermark;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static Button buttonNewPhoto;
    private static Button buttonValidate;
    private static EditText editTextWatermarkKey;
    private static byte[] watermark;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_WRITE_IMAGES = 1;
    private static final int PICK_IMAGE_REQUEST = 2;

    static {
        System.loadLibrary("opencv_java3");
    }

    static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("Did not load openCV");
        } else {
            System.out.println("Success!");
        }
    }

    public static byte[] getWatermark() {
        if (watermark == null) {
            watermark = Crypt.getHash("scm");
        }
        return watermark;
    }

    public byte[] getWatermarkHash() throws NullPointerException {
        editTextWatermarkKey = (EditText) findViewById(R.id.editTextWatermarkKey);

        if (editTextWatermarkKey.getText().toString() == null || editTextWatermarkKey.getText().toString().equals("")) {
            watermark = Crypt.getHash("scm");
            return watermark;
        }
        watermark = Crypt.getHash(editTextWatermarkKey.getText().toString());
        return watermark;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        onInsertWatermarkButtonClicked();
        onValidateWatermarkButton();
    }

    /**
     * The event trigger of "TAKE PHOTO" button
     */
    public void onInsertWatermarkButtonClicked() {
        buttonNewPhoto = findViewById(R.id.buttonMakePhoto);
        buttonNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                dispatchTakePictureIntent();
            }
        });
    }

    /**
     * The event trigger of "VALIDATE PHOTO" button
     */
    public void onValidateWatermarkButton() {
        buttonValidate = (Button) findViewById(R.id.buttonValidateWatermark);
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.lstoicescu.jpeg_images_autentification.watermarking.ValidationActivity");
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

    Uri photoURI;

    /**
     * Launches the prebuilt camera application of the phone that will take the picture.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.lstoicescu.jpeg_images_autentification.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inPurgeable = true;

            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            System.out.println("TEST IMG:" + imageBitmap.getWidth() + " " + imageBitmap.getHeight());
            Mat img = new Mat();
            Utils.bitmapToMat(imageBitmap, img);
            WatermarkController wm = new WatermarkController();
            img = wm.insertWatermarkIntoImage(img, new Watermark(getWatermarkHash()), true);
            Utils.matToBitmap(img, imageBitmap);

            saveImage(imageBitmap);
        }else {
            try {
                throw new Exception("MUIE");
            } catch (Exception e) {
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