package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bottomNavigation(); // calling the bottom navigation function
        activateButtons();

    } // onCreate ends

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
                startActivity(new Intent(ServiceActivity.this, UserProfileActivity.class));
                break;
            case R.id.id_aboutBtn:
                startActivity(new Intent(ServiceActivity.this, AboutAppActivity.class));
                break;
            case R.id.id_logoutBtn:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ServiceActivity.this, MainpageActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void activateButtons()
    {
        // Function to open the page to NearestHospitalActivity
        Button Button_NearestHospitalActivity = findViewById(R.id.btn_hospitals);
        Button_NearestHospitalActivity.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v )
            {
                startActivity(new Intent(ServiceActivity.this, NearestHospitalActivity.class));
            }
        });

        // Function to open the page to TestingCentersActivity
        Button Button_TestingCentersActivity = findViewById(R.id.btn_testingCenters);
        Button_TestingCentersActivity.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v )
            {
                startActivity(new Intent(ServiceActivity.this, TestingCentersActivity.class));
            }
        });

        // Function to open the page to ServiceActivity
        Button Button_SelfTestingActivity = findViewById(R.id.btn_selfTesting);
        Button_SelfTestingActivity.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v )
            {
                startActivity(new Intent(ServiceActivity.this, SelfTestingActivity.class));
            }
        });

        //Covid Tracker
        Button btnCovConnect = findViewById(R.id.btn_covConnect);
        btnCovConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intToMain = new Intent(ServiceActivity.this, CovidConnect.class);
                startActivity(intToMain);
            }
        });

//        //Logging out
//        Button btnLogout = findViewById(R.id.btn_logout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intToMain = new Intent(ServiceActivity.this, MainpageActivity.class);
//                startActivity(intToMain);
//            }
//        });
    }



    // bottom navigation function
    private void bottomNavigation()
    {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_services);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_covidtracker:
                        startActivity(new Intent(ServiceActivity.this, LocationTracer.class));
                        break;
                    case R.id.navigation_contacttracer:
                        startActivity(new Intent(ServiceActivity.this, ContactTracerActivity.class));
                        break;
                    case R.id.navigation_covidupdates:
                        startActivity(new Intent(ServiceActivity.this, CovidUpdatesActivity.class));
                        break;
                    case R.id.navigation_services:
                        startActivity(new Intent(ServiceActivity.this, ServiceActivity.class));
                        break;
                }
                return true;
            }
        });
    } // bottom navigation function ends

    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        startActivity(new Intent(ServiceActivity.this, DashboardActivity.class));
    }

} //class ends