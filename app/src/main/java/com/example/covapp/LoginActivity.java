package com.example.covapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private FirebaseAuth mAuth;
    private String email, password;
    private EditText emailEditText, passwordEditText;
    private static final String TAG = "LoginActivity";
    public static Boolean guest_user ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_Password);
        loginButton = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();



        registerAccount();  // calling the page to register account



        //checking if user is logged in
        if (mAuth.getCurrentUser() != null) {
            updateUI(mAuth.getCurrentUser());
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                // email / password requirements
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Please enter a valid email");
                    emailEditText.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    passwordEditText.setError("Minimum length of password should be 6");
                    passwordEditText.requestFocus();
                    return;
                }

                // FIREBASE LOG IN Authentication FUNCTION
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d(TAG, " user is signed in as member ");
                                    guest_user = false;
                                    MainpageActivity.GuestUser = false;
                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Account Not Found.",
                                            Toast.LENGTH_SHORT).show();
                                    guest_user = true;
                                    MainpageActivity.GuestUser = true;
                                    Log.d(TAG, " user is signed in as guest ");
                                   // updateUI(null);

                                }
                                // ...
                            }
                        });
            }

        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "current user id = "+ currentUser);

        if (currentUser != null) {
            guest_user = false;
            MainpageActivity.GuestUser = false;
            updateUI(currentUser);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            guest_user = false;
            MainpageActivity.GuestUser = false;
            updateUI(currentUser);
        }
    }

    public void updateUI(FirebaseUser currentUser) {
        Intent profileIntent = new Intent(getApplicationContext(), DashboardActivity.class);
        profileIntent.putExtra("email", currentUser.getEmail());
        Log.v("DATA", currentUser.getUid());
        guest_user = false;
        MainpageActivity.GuestUser = false;
        startActivity(profileIntent);
    }


    /**
     * Function to open the page to register the account
     */
    public void registerAccount()
    {
        Button registerButton = findViewById(R.id.btn_registerAccount);
        registerButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v )
            {
                guest_user = false;
                MainpageActivity.GuestUser = false;
                startActivity(new Intent(LoginActivity.this, RegisterAccountActivity.class));
            }
        });

    } // resgiterAccount ends

    public void onBackPressed() {
        // moveTaskToBack(true); // exit out from the app
        MainpageActivity.GuestUser = null;
        LoginActivity.guest_user = null;
        startActivity(new Intent(LoginActivity.this, MainpageActivity.class));

    }

} // class ends