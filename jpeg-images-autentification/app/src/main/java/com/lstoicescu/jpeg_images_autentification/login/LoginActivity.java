package com.lstoicescu.jpeg_images_autentification.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lstoicescu.jpeg_images_autentification.R;
import com.lstoicescu.jpeg_images_autentification.login.utils.UserAuthentication;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";
    private final  UserAuthentication userAuthentication = new UserAuthentication();
    private static EditText username;
    private static EditText password;
    private static Button loginButton;
    private int attempts = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        onButtonClicked();
    }

    public void onButtonClicked(){
        username = (EditText) findViewById(R.id.editTextUser);
        password = (EditText) findViewById(R.id.editTextPassword);
        loginButton = (Button) findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean passwordAuthenticity = userAuthentication.checkString(password.getText().toString());
                boolean userAuthenticity = userAuthentication.checkString(username.getText().toString());

                if(passwordAuthenticity&userAuthenticity){
                    Intent intent = new Intent("com.lstoicescu.jpeg_images_autentification.MainActivity");
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong user or password.", Toast.LENGTH_SHORT).show();
                    attempts --;
                    if(attempts == 0){
                        loginButton.setEnabled(false);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                    loginButton.setEnabled(true);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        attempts = 5;
                    }
                }
            }
        });
    }
}
