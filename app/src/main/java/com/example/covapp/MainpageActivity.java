package com.example.covapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainpageActivity extends Activity {
        private static final String TAG = "MainpageActivity";
        public static Boolean GuestUser ;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_mainpage);

            Button UserLogin;
            Button Guest;

            //login as a user
            UserLogin = findViewById(R.id.User);
            UserLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuestUser = false;
                    Log.d(TAG, "user is signed in as a guest (should print out false) = "+ GuestUser);
                    Intent intToMain = new Intent(com.example.covapp.MainpageActivity.this, LoginActivity.class);
                    startActivity(intToMain);
                }
            });

            //login as a guest
            Guest= findViewById(R.id.Guest);
            Guest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GuestUser = true;
                    Log.d(TAG, "user is signed in as a guest (should print out true) = "+ GuestUser);
                    Intent intToMain = new Intent(com.example.covapp.MainpageActivity.this, DashboardActivity.class);
                    startActivity(intToMain);
                }
            });

        }

        public void onBackPressed() {
            moveTaskToBack(true); // exit out from the app
        }
    }