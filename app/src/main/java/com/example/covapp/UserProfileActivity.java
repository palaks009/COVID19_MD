package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private FirebaseFirestore firebaseFirestore_db;
    private FirebaseAuth mAuth;
    private String userid, name, email, patient, age, phNum ;
    private String update_patient, familyMember;

    private TextView tv_fname, tv_email, tv_id;
    private RadioButton rb_patientsYes, rb_patientNo, rb_familyMemberYes, getRb_familyMemberNo;
    Button btn_update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firebaseFirestore_db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        tv_fname = (TextView) findViewById(R.id.settings_fullnname);
        tv_email = (TextView) findViewById(R.id.settings_email);
        tv_id = (TextView) findViewById(R.id.settings_user_id);

        rb_patientsYes = (RadioButton) findViewById(R.id.settings_texYes); // initiate a yes radio button
        rb_patientNo = (RadioButton) findViewById(R.id.settings_texNo); // initiate a no radio button

        rb_familyMemberYes = (RadioButton) findViewById(R.id.settingsFamily_yes); // initiate a yes radio button
        getRb_familyMemberNo = (RadioButton) findViewById(R.id.settingsFamily_no); // initiate a no radio button

        btn_update = findViewById(R.id.settings_updates);

        //userid= mAuth.getCurrentUser().getUid();
        //collectData(userid);
        UserType();


    } //onCreate ends

    public void UserType() {
        Log.d(TAG, " inside UserType() function ");

        if ((MainpageActivity.GuestUser== true) || (LoginActivity.guest_user==true) ||
                ((MainpageActivity.GuestUser== true) && (LoginActivity.guest_user==true)))
        {
            Log.d(TAG, " user is signed in as guest ");
            tv_fname.setText("Guest");
            tv_email.setText("Guest@gmail.com");
            tv_id.setText("No User Id");
            Toast.makeText
                    (getApplicationContext(), "Register account for saving data", Toast.LENGTH_SHORT)
                    .show();

        } else
            {
            Log.d(TAG, " user is signed in as member ");


            userid= mAuth.getCurrentUser().getUid();
            collectData(userid);
            SubmitUpdateBtn();

            // code to upload the data to db
        }
    }

    public void collectData(final String userid)
    {
        Log.d(TAG, "collectData function is called: ");
        DocumentReference documentReference = firebaseFirestore_db.collection("users_ID").document(userid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name = document.getString("fname");
                        email = document.getString("email");
                        patient = document.getString("patient");
                        phNum = document.getString("phNum");
                        age = document.getString("age");




                        pageSetup(name, userid, email,patient);
                        Log.d(TAG, "collectData => name: " + name + " email: "+ email + " patient "+ patient);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void pageSetup(String name, String id, String email, String patient)
    {
        tv_fname.setText(name);
        tv_email.setText(email);
        tv_id.setText(userid);

        if(patient.equals("Yes"))
        {
            rb_patientsYes.setChecked(true);
        }
        else
        {
            rb_patientNo.setChecked(true);
        }
    }

    private void SubmitUpdateBtn() { ;
        btn_update.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v )
            {
                Log.d(TAG, "calling updateProfile() function ");
                updateProfile();
                Toast.makeText(getApplicationContext(), "Update Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserProfileActivity.this, DashboardActivity.class));
            }
        });
    }

    public void updateProfile()
    {
        if (rb_patientsYes.isChecked()){
            update_patient = "Yes";
        }

        if (rb_patientNo.isChecked()){
            update_patient = "No";
        }

        if (rb_familyMemberYes.isChecked()){
            familyMember = "Yes";
        }

        if (getRb_familyMemberNo.isChecked()){
            familyMember = "No";
        }

        DocumentReference documentReference = firebaseFirestore_db.collection("users_ID").document(userid);
        Map<String, Object> user = new HashMap<>();

        user.put("fname", name);
        user.put("age", age);
        user.put("email", email);
        user.put("phNum", phNum);
        user.put("patient", update_patient);
        user.put("userId", userid);
        user.put("familyMember", familyMember);
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.d(TAG, "onSuccess: User profile is created with patient: "+update_patient +" familyMember: "+familyMember);

            }
        });

        /****************************/


        if(update_patient.equals("Yes"))
        {
            DocumentReference docRef = firebaseFirestore_db.collection("patients").document(userid);
            Map<String, Object> user_patient = new HashMap<>();
            user_patient.put("fname", name);
            user_patient.put("age", age);
            user_patient.put("patient", patient);
            user_patient.put("email", email);
            user_patient.put("userId", userid);
            user_patient.put("familyMember", familyMember);
            docRef.set(user_patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Log.d(TAG, "onSuccess: Created db for patients");
                }
            });
        }
        if(update_patient.equals("No"))
        {
            Task<Void> docRef = firebaseFirestore_db.collection("patients").document(userid).update("patient","No");
        }

    }





} //class ends