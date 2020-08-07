package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestingCentersActivity extends AppCompatActivity {

    private String userid= "LNS1TyV2vVQOaBMQsRuAlSDr6xs1";
    private String TAG = "TestingCentersActivity";
    private FirebaseFirestore firebaseFirestore_db;
    private ListView simpleList;
    private Spinner spinner;
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayList<String> locationList = new ArrayList<String>();
    private ArrayList<String> countyList = new ArrayList<String>();
    private ArrayList<String> infoList = new ArrayList<String>();
    // private ArrayList<String> city = new ArrayList<String>();
    String[] city  = {"Select City", "Aberdeen", "Annapolis", "Baltimore","Beltsville", "Bethesda", "Gambrills","Germantown","Glen Burnie", "Glenarden", "Greenbelt", "Hagerstown", "Hampstead", "Hanover", "Hyattsville", "Jessup", "Laurel", "Olney"," Owings Mills"};
    final List<String> cityList = new ArrayList<>(Arrays.asList(city));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_centers);

        firebaseFirestore_db = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.spinner);
        //city();

        collectData(null);
        spinnerSettings ();
        bottomNavigation();

    } // onCreate ends

    public void spinnerSettings () {

       // Log.d(TAG, "spinnerSettings => city: " + city );

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,cityList){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position%2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(Color.parseColor("#454545"));
                }
                else {
                    // Set the alternate item background color
                    tv.setBackgroundColor(Color.parseColor("#999999"));
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // Notify the selected item text
                //selectedItemText= seletectCity;
                nameList.clear();
                locationList.clear();
                countyList.clear();
                infoList.clear();
                collectData(selectedItemText);
                Toast.makeText
                        (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //collectData(null);
            }
        });
    }


    public void collectData(String seletectCity)
    {
        Log.d(TAG, "collectData => seletectCity: " + seletectCity );
        if((seletectCity != null) && (seletectCity != "Select City"))
        {
            firebaseFirestore_db.collection("testing_center").whereEqualTo("County",seletectCity)
                .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    if (document.exists()) {
                                        String name = document.getString("Name");
                                        String location = document.getString("Location");
                                        String county = document.getString("County");
                                        String info = document.getString("info");
                                        nameList.add(name);
                                        locationList.add(location);
                                        countyList.add(county);
                                        infoList.add(info);

                                        Log.d(TAG, "name ==> "+ name +" location ==> "+ location +" county ==> " + county + " info ==> "+infoList );

                                    } else
                                    {
                                        Log.d(TAG, "Error getting firestore testing_center details: ");
                                    }
                                    Log.d(TAG, "collectData => nameList: " + nameList );
                                    Log.d(TAG, "collectData => locationList: " + locationList );
                                    displayContact();

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else {
            firebaseFirestore_db.collection("testing_center")
            .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult())
                                {
                                    if (document.exists()) {
                                        String name = document.getString("Name");
                                        String location = document.getString("Location");
                                        String county = document.getString("County");
                                        String info = document.getString("info");
                                        nameList.add(name);
                                        locationList.add(location);
                                        countyList.add(county);
                                        infoList.add(info);

                                        Log.d(TAG, "name ==> "+ name +" location ==> "+ location +" county ==> " + county + " info ==> "+infoList );

                                    } else
                                    {
                                        Log.d(TAG, "Error getting firestore testing_center details: ");
                                    }
                                    Log.d(TAG, "collectData => nameList: " + nameList );
                                    Log.d(TAG, "collectData => locationList: " + locationList );
                                    displayContact();

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
        //firebaseFirestore_db.collection("testing_center").whereEqualTo("County","Baltimore")



    }

    public  void displayContact()
    {
        simpleList = (ListView) findViewById(R.id.lv_testingcenter);
        CustomAdapterTestingCenter CustomAdapterTestingCenter = new CustomAdapterTestingCenter(getApplicationContext(), nameList, locationList,countyList, infoList);
        simpleList.setAdapter(CustomAdapterTestingCenter);
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
                        startActivity(new Intent(TestingCentersActivity.this, LocationTracer.class));
                        break;
                    case R.id.navigation_contacttracer:
                        startActivity(new Intent(TestingCentersActivity.this, ContactTracerActivity.class));
                        break;
                    case R.id.navigation_covidupdates:
                        startActivity(new Intent(TestingCentersActivity.this, CovidUpdatesActivity.class));
                        break;
                    case R.id.navigation_services:
                        startActivity(new Intent(TestingCentersActivity.this, ServiceActivity.class));
                        break;
                }
                return true;
            }
        });
    } // bottom navigation function ends

}//class ends