package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button btnLogout;
        Button btnCovTracker;
        Button btnContTracer;
        Button btnUpdates;
        Button btnServices;

        mAuth = FirebaseAuth.getInstance();

        //Covid Tracker
        btnCovTracker = findViewById(R.id.btn_CovidTracker);
        btnCovTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMain = new Intent(DashboardActivity.this, LocationTracer.class);
                startActivity(intToMain);
            }
        });

        //btn_ContactTracing
        btnContTracer = findViewById(R.id.btn_ContactTracing);
        btnContTracer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMain = new Intent(DashboardActivity.this, ContactTracerActivity.class);
                startActivity(intToMain);
            }
        });

        //Covid Tracker
        btnUpdates = findViewById(R.id.btn_CovidUpdates);
        btnUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMain = new Intent(DashboardActivity.this, CovidUpdatesActivity.class);
                startActivity(intToMain);
            }
        });
        //Services
        btnServices = findViewById(R.id.btn_Services);
        btnServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMain = new Intent(DashboardActivity.this, ServiceActivity.class);
                startActivity(intToMain);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.id_settingsBtn:
                startActivity(new Intent(DashboardActivity.this, UserProfileActivity.class));
                break;
            case R.id.id_aboutBtn:
                startActivity(new Intent(DashboardActivity.this, AboutAppActivity.class));
                break;
            case R.id.id_logoutBtn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(DashboardActivity.this, MainpageActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        startActivity(new Intent(DashboardActivity.this, DashboardActivity.class));
    }
}