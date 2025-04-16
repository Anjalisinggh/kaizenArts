package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kaizenarts.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText name, address, city, postalCode, phoneNumber;
    private Button addAddressBtn;
    private Toolbar toolbar;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Initialize Firebase and UI components
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        name = findViewById(R.id.ad_name);
        address = findViewById(R.id.ad_address);
        city = findViewById(R.id.ad_city);
        postalCode = findViewById(R.id.ad_code);
        phoneNumber = findViewById(R.id.ad_phone);
        addAddressBtn = findViewById(R.id.ad_add_address);

        // Add address button functionality
        addAddressBtn.setOnClickListener(v -> addAddress());
    }

    private void addAddress() {
        // Retrieve user input
        String userName = name.getText().toString().trim();
        String userCity = city.getText().toString().trim();
        String userAddress = address.getText().toString().trim();
        String userCode = postalCode.getText().toString().trim();
        String userNumber = phoneNumber.getText().toString().trim();

        // Validate input fields
        if (userName.isEmpty() || userCity.isEmpty() || userAddress.isEmpty() || userCode.isEmpty() || userNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all fields properly", Toast.LENGTH_LONG).show();
            return;
        }

        // Combine fields into a single address string
        String finalAddress = userName + ", " + userCity + ", " + userAddress + ", " + userCode + ", " + userNumber;

        // Prepare address data for Firestore
        Map<String, String> addressMap = new HashMap<>();
        addressMap.put("userAddress", finalAddress);

        // Get the current user's UID
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
            return;
        }

        // Save address to Firestore
        firestore.collection("CurrentUser")
                .document(userId)
                .collection("Address")
                .add(addressMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddAddressActivity.this, "Address added successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AddAddressActivity.this,DetailedActivity.class));
                        finish(); // Close the activity after successfully adding the address
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error occurred";
                        Toast.makeText(AddAddressActivity.this, "Failed to add address: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        }


}
