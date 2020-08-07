package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContactList_Activity<priavte> extends AppCompatActivity {


    private FirebaseFirestore firebaseFirestore_db;
    //private String fullname, email;
    private String userid;
    private FirebaseAuth mAuth;
    private String TAG = "ContactList_Activity";

    private RecyclerView mFirestoreList;
    private FirestoreRecyclerAdapter adapter;
    private ListView simpleList;
    private SearchView searchView;
    private ArrayList<String> nameList = new ArrayList<String>();
    private ArrayList<String> phoneList = new ArrayList<String>();

    //TextView fullName, phoneNumber;
    RecyclerView recyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list_);

        firebaseFirestore_db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userid= mAuth.getCurrentUser().getUid();
        collectData();

        bottomNavigation(); // calling the bottom navigation function




    } // onCreate ends

    public void collectData()
    {
        Log.d(TAG, "collectData function is called: ");
        CollectionReference collectionReference = firebaseFirestore_db.collection("contacts").document(userid)
                .collection("name");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Log.d(TAG, document.getId() + " => " + document.get("id"));
                            i++;
                        if (document.exists()) {
                            String name = document.getString("fname");
                            String id = document.getString("id");
                            Log.d(TAG, "collectData => name: " + name + " id: "+id);
                            //nameList.add(name);
                            collectEmail(id, name);

                        }
                    }
                    Log.d(TAG, "collectData => nameList: " + nameList );
                }
            }
        });

    }


    public void collectEmail(final String id, final String name)
    {
        Log.d(TAG, "collectEmail function is called: ");
        CollectionReference collectionReference = firebaseFirestore_db.collection("contacts").document(userid)
                .collection("phone_num");
        collectionReference.whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Log.d(TAG, document.getId() + " => " + document.get("id"));

                        if (document.exists()) {
                            String phone = document.getString("phone");
                            String id = document.getString("id");
                            nameList.add(name);
                            phoneList.add(phone);
                            Log.d(TAG, "collectEmail => id: "+ id + " name: "+name +" phone/email: "+phone );

                        }
                    }
                    Log.d(TAG, "collectData => nameList: " + nameList );
                    Log.d(TAG, "collectData => phoneList: " + phoneList );
                    displayContact();
                }
            }
        });

    }

    public  void displayContact()
    {
 /*       simpleList = (ListView)findViewById(R.id.list_view);
        ArrayAdapter<String> nameListAdapter = new ArrayAdapter<String>(this, R.layout.list_items, R.id.list_name, nameList);
        simpleList.setAdapter(nameListAdapter);

        Lv_phonelist = (ListView)findViewById(R.id.list_view);
        ArrayAdapter<String> phoneListAdapter = new ArrayAdapter<String>(this, R.layout.list_items, R.id.list_phone_number, phoneList);
        Lv_phonelist.setAdapter(phoneListAdapter);
*/
        simpleList = (ListView) findViewById(R.id.list_view);
        CustomAdapter_ContactTracer customAdapterContactTracer = new CustomAdapter_ContactTracer(getApplicationContext(), nameList, phoneList);
        simpleList.setAdapter(customAdapterContactTracer);

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
                        startActivity(new Intent(ContactList_Activity.this, LocationTracer.class));
                        break;
                    case R.id.navigation_contacttracer:
                        startActivity(new Intent(ContactList_Activity.this, ContactTracerActivity.class));
                        break;
                    case R.id.navigation_covidupdates:
                        startActivity(new Intent(ContactList_Activity.this, CovidUpdatesActivity.class));
                        break;
                    case R.id.navigation_services:
                        startActivity(new Intent(ContactList_Activity.this, ServiceActivity.class));
                        break;
                }
                return true;
            }
        });
    } // bottom navigation function ends

    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        startActivity(new Intent(ContactList_Activity.this, DashboardActivity.class));
    }
} // class ends