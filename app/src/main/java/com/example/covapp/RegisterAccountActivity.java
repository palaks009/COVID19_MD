package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

public class RegisterAccountActivity extends AppCompatActivity  {

    private EditText firstname_EditText, age_EditText, phNumber_EditText, email_EditText, password_EditText;
    private FirebaseDatabase database;
    private FirebaseFirestore firebaseFirestore_db;
    private DatabaseReference mDatabase;
    private String TAG = "RegisterActivity";
    private String fname, email, password, userId, phNumber, patient, age;
    private String userid;
    private FirebaseAuth mAuth;
    public static Boolean guest_user ;
    RadioButton yesRB;
    RadioButton noRB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        yesRB = (RadioButton) findViewById(R.id.textYes); // initiate a yes radio button
        noRB = (RadioButton) findViewById(R.id.textNo); // initiate a no radio button


        firstname_EditText = (EditText)findViewById(R.id.editTextFirstName);
        age_EditText = (EditText) findViewById(R.id.editTextAge);
        phNumber_EditText = (EditText) findViewById(R.id.editPhNumber);
        email_EditText = findViewById(R.id.editTextEmailAddress);
        password_EditText = findViewById(R.id.editTextTextPassword);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore_db = FirebaseFirestore.getInstance();



        registerAccount();


    } // ending Oncreate

    private void registerAccount() {
        Button registerAccountButton = findViewById(R.id.button_register_account);
        registerAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v )
            {
                Log.d(TAG, "calling registerUser(); function ");
                registerUser();
            }
        });
    }

    public void registerUser() {
        email = email_EditText.getText().toString();
        password = password_EditText.getText().toString();
        age = age_EditText.getText().toString();
        fname = firstname_EditText.getText().toString();
        phNumber = phNumber_EditText.getText().toString();

        if (email.isEmpty()) {
            email_EditText.setError("Email is required");
            email_EditText.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_EditText.setError("Please enter a valid email");
            email_EditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            password_EditText.setError("Password is required");
            password_EditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            password_EditText.setError("Minimum length of password should be 6");
            password_EditText.requestFocus();
            return;
        }

        if (Integer.parseInt(age) < 15) {
            age_EditText.setError("You must be at least 15 years old");
            age_EditText.requestFocus();
            return;
        }

        if (Integer.parseInt(age) > 140) {
            age_EditText.setError("Please enter a valid age");
            age_EditText.requestFocus();
            return;
        }

        if((phNumber.length()) != 10){
            phNumber_EditText.setError("Invalid phone number");
            phNumber_EditText.requestFocus();
            return;
        }

        if (yesRB.isChecked()){
            patient = "Yes";
        }

        if (noRB.isChecked()){
            patient = "No";
        }

        // FIREBASE REGISTRATION Authentication FUNCTION
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //   progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Registeration Successful", Toast.LENGTH_SHORT).show();


                    userid= mAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firebaseFirestore_db.collection("users_ID").document(userid);
                    Map<String, Object> user = new HashMap<>();
                    user.put("fname", fname);
                    user.put("age", age);
                    user.put("phNum", phNumber);
                    user.put("patient", patient);
                    user.put("email", email);
                    user.put("password", password);
                    user.put("userId", userid);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Log.d(TAG, "onSuccess: User profile is created with fname: "+fname +" email: "+email);

                        }
                    });

                    /****************************/


                    if(patient.equals("Yes"))
                    {
                        DocumentReference docRef = firebaseFirestore_db.collection("patients").document(userid);
                        Map<String, Object> user_patient = new HashMap<>();
                        user_patient.put("fname", fname);
                        user_patient.put("age", age);
                        user_patient.put("patient", patient);
                        user_patient.put("email", email);
                        user_patient.put("userId", userid);
                        docRef.set(user_patient).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Log.d(TAG, "onSuccess: Created db for patients");
                            }
                        });
                    }


                    /****************************/
                    guest_user = false;
                    startActivity(new Intent(RegisterAccountActivity.this, DashboardActivity.class));
                } else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        guest_user =true;
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    guest_user = true;

                }
            }
        });

    }

    public boolean isValidPhone(CharSequence phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }
    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        startActivity(new Intent(RegisterAccountActivity.this, LoginActivity.class));
    }

} // class ends