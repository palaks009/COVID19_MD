package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covapp.models.User;
import com.example.covapp.models.UserLocation;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactTracerActivity extends AppCompatActivity {

    private String TAG = "ContactTracerActivity";
    TextView textDisplay;
    private FirebaseDatabase database;
    private FirebaseFirestore firebaseFirestore_db;
    private DatabaseReference mDatabase;
    private String fullname, email;
    private String userid;
    private FirebaseAuth mAuth;
    private EditText fname1_Edittext, email1_Edittext, fname2_Edittext, email2_Edittext, fname3_Edittext, email3_Edittext, fname4_Edittext, email4_Edittext, fname5_Edittext, email5_Edittext;
    private String fname1, email1, fname2, email2, fname3, email3, fname4, email4, fname5, email5;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_tracer);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore_db = FirebaseFirestore.getInstance();

        UserType();
        //activatePage();


        bottomNavigation(); // calling the bottom navigation function

    } // onCreate ends

    public void UserType() {
        Log.d(TAG, " inside UserType() function ");

        if ((MainpageActivity.GuestUser== true) || (LoginActivity.guest_user==true) ||
                ((MainpageActivity.GuestUser== true) && (LoginActivity.guest_user==true)))
        {
            Log.d(TAG, " user is signed in as guest ");
            // show the user the message that you have to register the account/ login inorder to save the data
            // if user clocks yes -  takes to login page/ registeration
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This feature requires you to create account for saving your data and only you have the control to manage your data stored. Do you want to log in or create account ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            //MainpageActivity.GuestUser = false;
                            MainpageActivity.GuestUser = null;
                            LoginActivity.guest_user = null;
                            Log.d(TAG, " user is signed : MainpageActivity.GuestUser =  "+ MainpageActivity.GuestUser);
                            Log.d(TAG, " user is signed : LoginActivity.guest_user = "+ LoginActivity.guest_user);

                            startActivity(new Intent(ContactTracerActivity.this, LoginActivity.class));
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ContactTracerActivity.this, DashboardActivity.class));
                }
            });

            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            Log.d(TAG, " user is signed in as member ");

            activatePage();
            // code to upload the data to db
        }
    }

    public void activatePage()
    {
        submitButton();
        fetchButton();
    }



    // bottom navigation function
    private void bottomNavigation()
    {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_contacttracer);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_covidtracker:
                        startActivity(new Intent(ContactTracerActivity.this, LocationTracer.class));
                        break;
                    case R.id.navigation_contacttracer:
                        startActivity(new Intent(ContactTracerActivity.this, ContactTracerActivity.class));
                        break;
                    case R.id.navigation_covidupdates:
                        startActivity(new Intent(ContactTracerActivity.this, CovidUpdatesActivity.class));
                        break;
                    case R.id.navigation_services:
                        startActivity(new Intent(ContactTracerActivity.this, ServiceActivity.class));
                        break;
                }
                return true;
            }
        });
    } // bottom navigation function ends

    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        startActivity(new Intent(ContactTracerActivity.this, DashboardActivity.class));
    }


    public void submitButton() {
        Button submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getuserInput();
                startActivity(new Intent(ContactTracerActivity.this, DashboardActivity.class));

            }
        });
    }


    public void fetchButton() {
        Button fetch = findViewById(R.id.btn_fetch);
        fetch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //fetchUser();
                startActivity(new Intent(ContactTracerActivity.this, ContactList_Activity.class));
            }
        });
    }

    public void getuserInput() {

        fname1_Edittext= (EditText)findViewById(R.id.Fullname1);
        email1_Edittext = (EditText) findViewById(R.id.Email1);

        fname2_Edittext= (EditText) findViewById(R.id.Fullname2);
        email2_Edittext = (EditText) findViewById(R.id.Email2);

        email3_Edittext = (EditText) findViewById(R.id.Email3);
        fname3_Edittext = (EditText) findViewById(R.id.Fullname3);

        email4_Edittext = (EditText) findViewById(R.id.Email4);
        fname4_Edittext = (EditText) findViewById(R.id.Fullname4);

        email5_Edittext = (EditText) findViewById(R.id.Email5);
        fname5_Edittext = (EditText) findViewById(R.id.Fullname5);

        fname1 = fname1_Edittext.getText().toString();
        email1 = email1_Edittext.getText().toString();

        fname2 = fname2_Edittext.getText().toString();
        email2 = email2_Edittext.getText().toString();

        fname3 = fname3_Edittext.getText().toString();
        email3 = email3_Edittext.getText().toString();

        fname4 = fname4_Edittext.getText().toString();
        email4 = email4_Edittext.getText().toString();

        fname5 = fname5_Edittext.getText().toString();
        email5 = email5_Edittext.getText().toString();

        Log.d(TAG, "onSuccess: getuserInput => name: " + fname1 );

        /*******************
         * here we can validate the user entries
         */
        if( (!fname1.isEmpty()) && (!email1.isEmpty()) )
        {
            updateDatabase(fname1,email1);
        }
        if( (!fname2.isEmpty()) && (!email2.isEmpty()) )
        {
            updateDatabase(fname2,email2);
        }
        if( (!fname3.isEmpty()) && (!email3.isEmpty()) )
        {
            updateDatabase(fname3,email3);
        }
        if( (!fname4.isEmpty()) && (!email4.isEmpty()) )
        {
            updateDatabase(fname4,email4);
        }
        if( (!fname5.isEmpty()) && (!email5.isEmpty()) )
        {
            updateDatabase(fname5,email5);
        }

    }




    public void updateDatabase(String name, final String phoneNumber)
    {


        Log.d(TAG, "onSuccess: getuserInput => name: " + name );

 //       if(!fname1.isEmpty())
 //       {  if not empty write to db  }


        userid= mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore_db.collection("contacts").document(userid)
                .collection("name").document();
        final String specific_ID = documentReference.getId();

        final Map<String, Object> user_name = new HashMap<>();
        user_name.put("fname", name);
        user_name.put("id", specific_ID);
        user_name.put("timestamp", FieldValue.serverTimestamp());
        documentReference.set(user_name).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "onSuccess: updateDatabase => name: " + user_name );
                updateEmail(specific_ID, phoneNumber);
            }
        });

    }

    public void updateEmail(String specific_ID, String phoneNumber)
    {
        userid= mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore_db.collection("contacts").document(userid)
                .collection("phone_num").document(specific_ID);

        final Map<String, Object> user_email = new HashMap<>();
        user_email.put("phone", phoneNumber);
        user_email.put("id", specific_ID);
        user_email.put("timestamp", FieldValue.serverTimestamp());
        documentReference.set(user_email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "onSuccess: updateEmail => email: " + user_email );
            }
        });
    }

} //class ends

