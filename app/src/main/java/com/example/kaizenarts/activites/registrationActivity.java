package com.example.kaizenarts.activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kaizenarts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class registrationActivity extends AppCompatActivity {

    private EditText name, email, password;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Redirect to MainActivity if user is already signed in
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(registrationActivity.this, MainActivity.class));
            finish(); // Close current activity
        }

        // Initialize UI elements
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);

        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        // If it's the first time, show the onboarding screen
        if (isFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);  // Update the flag to false after onboarding
            editor.apply();

            Intent intent = new Intent(registrationActivity.this, onBoardingActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        }
    }

    // Sign-up method triggered by the button
    public void signup(View view) {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(userName)) {
            showToast("Please enter your name.");
            return;
        }

        if (TextUtils.isEmpty(userEmail)) {
            showToast("Please enter your email.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            showToast("Please enter a valid email address.");
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            showToast("Please enter your password.");
            return;
        }

        if (userPassword.length() < 6) {
            showToast("Password must be at least 6 characters long.");
            return;
        }

        // Register user with Firebase Authentication
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast("Registration successful. Welcome, " + userName + "!");
                            startActivity(new Intent(registrationActivity.this, MainActivity.class));
                            finish(); // Close current activity
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred.";

                            // Handling specific Firebase errors
                            if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                errorMessage = "Password is too weak";
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                errorMessage = "Invalid email format";
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                errorMessage = "Email already in use";
                            }

                            showToast("Registration failed: " + errorMessage);
                        }
                    }
                });
    }

    // Sign-in method triggered by the button
    public void signin(View view) {
        Intent intent = new Intent(registrationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Close current activity
    }

    // Helper method to show Toast messages
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Prevent users from navigating back to registration screen after successful registration
    @Override
    public void onBackPressed() {
        // Do nothing or navigate to a specific screen (like MainActivity)
        super.onBackPressed();
    }
}
