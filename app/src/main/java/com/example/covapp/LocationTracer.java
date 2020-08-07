package com.example.covapp;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.covapp.models.User;
import com.example.covapp.models.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationTracer extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private String TAG = "LocationTracer";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseFirestore firebaseFirestore_db;
    private FirebaseAuth mAuth;
    private UserLocation userLocation;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();

    private String userid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_tracer);



 /*       if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        Log.d(TAG, "onCreate: end of oncreate");
  */
        bottomNavigation();
        UserType(); // checking if the user is a member
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: inside onMapReady ");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        Log.d(TAG, "onMapReady: finished onMapReady");
    }
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient: inside buildGoogleApiClient ");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        Log.d(TAG, "buildGoogleApiClient: finished buildGoogleApiClient ");
    }
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: inside onConnected ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }

        Log.d(TAG, "onConnected: finished onConnected ");
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: inside and finished onConnectionSuspended  ");
    }
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: inside onLocationChanged ");
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        String state = null, country = null, subLocality = null;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Log.d(TAG, "onLocationChanged: inside longitude = "+longitude +"   latitude= " + latitude);
            Geocoder geocoder = new Geocoder(getApplicationContext(),
                    Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude,
                        longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {
                    state = listAddresses.get(0).getAdminArea();
                    country = listAddresses.get(0).getCountryName();
                    subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state
                            + "," + country);
                    Log.d(TAG, "onLocationChanged: inside try catch,  listAddresses => " +listAddresses);
                    //saveCurrentlocation(geoPoint, subLocality, state, country, listAddresses );
                    //getUserDeatils();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        saveCurrentlocation(geoPoint, subLocality, state, country ); // saving current location
        patientList(); // getting all location of patients

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.d(TAG, "onLocationChanged: inside latLng =  " + latLng);

        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
        Log.d(TAG, "onLocationChanged: finished onLocationChanged ");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            }
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /***********************************************************************************
     *  Helper Functions
     ************************************************************************************/

    public void saveCurrentlocation(final GeoPoint geoPoint, final String subLocality, final String state, final String country)
    {
        final DocumentReference documentReference_current = firebaseFirestore_db.collection("patients").document(userid)
                .collection("current_location").document(userid);
        final DocumentReference documentReference_history = firebaseFirestore_db.collection("patients").document(userid)
                .collection("location_history").document();

        final Map<String, Object> patient_location = new HashMap<>();
        patient_location.put("userId", userid);
        patient_location.put("geo_point",geoPoint );
        patient_location.put("timestamp", FieldValue.serverTimestamp());
        patient_location.put("subLocality", subLocality);
        patient_location.put("state", state);
        patient_location.put("country", country);
        //patient_location.put("list:", listAddresses.getClass());
        documentReference_current.set(patient_location).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "onSuccess: saveCurrentlocation => userId: "+ userid + " geo_point: " + geoPoint + " timestamp: " + FieldValue.serverTimestamp()
                        + " subLocality: " + subLocality + "state: " + state + " country: "+country);

                documentReference_history.set(patient_location).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Log.d(TAG, "onSuccess: Patients location history saved");

                    }
                });
            }
        });
    }

    public void patientList()
    {
        Log.d(TAG, "Current patients: patientList function is called: ");

        CollectionReference userRef = firebaseFirestore_db.collection("patients");
        userRef.whereEqualTo("patient", "Yes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the list and add all the users again
                            mUserList.clear();
                            mUserList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                mUserList.add(user);
                                Log.d(TAG, "patientList: adding patient user to the list: " + user);


                                getPatientLocation(user);

                            }
                            Log.d(TAG, "Current patients : " + mUserList);
                            Log.d(TAG, "Current patients size: " + mUserList.size());

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
    }

    private void getPatientLocation(final User user) {
        Log.d(TAG, "Current patients: getPatientLocation function is called: " + user);

        final CollectionReference collectionReference = firebaseFirestore_db.collection("patients").document(user.getuserId()).collection("current_location");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.get("geo_point"));
//     mUserLocation = (ArrayList<UserLocation>) document.get("geo_point");

                        //Log.d(TAG, "Getting all user locations: " + mUserLocation);
                        if (document.exists()) {
                            if(!(user.getuserId().equals(userid)))
                            {
                                GeoPoint geo = document.getGeoPoint("geo_point");
                                String name = document.getString("fname");
                                double lat = geo.getLatitude();
                                double lng = geo.getLongitude();
                                LatLng latLng = new LatLng(lat, lng);
                                Log.d(TAG, " firestore geo_point: " + document.getId() + " => " + latLng);

                                DocumentReference documentReference = firebaseFirestore_db.collection("user_locations").document(FirebaseAuth.getInstance().getUid());
                                documentReference.collection("user").whereArrayContains("patient","Yes");
                                Log.d(TAG, "Getting all patients: " + documentReference.get());

                                MarkerOptions options = new MarkerOptions().position(latLng).title(name);
                                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                mMap.addMarker(options);
                            }

                        } else {
                            Log.d(TAG, "Error getting firestore geo point: ");
                        }

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


    }

    public void UserType() {
        Log.d(TAG, " inside UserType() function ");

        try {
            if ((MainpageActivity.GuestUser== true) || (LoginActivity.guest_user==true) ||
                    ((MainpageActivity.GuestUser== true) && (LoginActivity.guest_user==true)))

            {
                Log.d(TAG, " user is signed in as guest ");
                // show the user the message that you have to register the account/ login inorder to save the data
                // if user clocks yes -  takes to login page/ registeration
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This feature requires you to create account for saving your current location. You have full control of your data to manage and delete. Do you want to create account ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                //MainpageActivity.GuestUser = false;
                                MainpageActivity.GuestUser = null;
                                LoginActivity.guest_user = null;
                                Log.d(TAG, " user is signed : MainpageActivity.GuestUser =  "+ MainpageActivity.GuestUser);
                                Log.d(TAG, " user is signed : LoginActivity.guest_user = "+ LoginActivity.guest_user);

                                startActivity(new Intent(LocationTracer.this, LoginActivity.class));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, " user is signed : MainpageActivity.GuestUser =  "+ MainpageActivity.GuestUser);
                        Log.d(TAG, " user is signed : LoginActivity.guest_user = "+ LoginActivity.guest_user);
                        startActivity(new Intent(LocationTracer.this, DashboardActivity.class));
                    }
                });

                final AlertDialog alert = builder.create();
                alert.show();
            } else {
                Log.d(TAG, " user is signed in as member ");

                firebaseFirestore_db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                userid= mAuth.getCurrentUser().getUid();


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkLocationPermission();
                }
                SupportMapFragment mapFragment = (SupportMapFragment)
                        getSupportFragmentManager()
                                .findFragmentById(R.id.map);

                mapFragment.getMapAsync(this);
                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps();
                }
                Log.d(TAG, "onCreate: end of oncreate");


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This feature requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        isServicesOK();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(LocationTracer.this, DashboardActivity.class));
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LocationTracer.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LocationTracer.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    // bottom navigation function
    private void bottomNavigation()
    {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_covidtracker:
                        startActivity(new Intent(LocationTracer.this, LocationTracer.class));
                        break;
                    case R.id.navigation_contacttracer:
                        //offlinePersistence();
                        startActivity(new Intent(LocationTracer.this, ContactTracerActivity.class));
                        break;
                    case R.id.navigation_covidupdates:
                        //offlinePersistence();
                        startActivity(new Intent(LocationTracer.this, CovidUpdatesActivity.class));
                        break;
                    case R.id.navigation_services:
                        //offlinePersistence();
                        startActivity(new Intent(LocationTracer.this, ServiceActivity.class));
                        break;
                }
                return true;
            }
        });
    } // bottom navigation function ends




} // class ends