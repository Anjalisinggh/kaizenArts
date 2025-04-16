package com.example.kaizenarts.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.activites.AddAddressActivity;
import com.example.kaizenarts.activites.PaymentActivity;
import com.example.kaizenarts.adapters.AddressAdapter;
import com.example.kaizenarts.models.AddressModel;
import com.example.kaizenarts.models.NewProductsModel;
import com.example.kaizenarts.models.PopularProductsmodel;
import com.example.kaizenarts.models.ShowAllModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.SelectedAddress {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressModelList;
    private Button paymentBtn, addAddressBtn;
    Toolbar toolbar;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String selectedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Get data from detailed activity
        Object obj = getIntent().getSerializableExtra("item");

        setUpToolbar();
        initializeFields();
        fetchAddresses();


        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = 0.0;
                if (obj instanceof NewProductsModel) {
                    NewProductsModel newProductsModel = (NewProductsModel) obj;
                    amount = newProductsModel.getPrice();
                }
                if (obj instanceof PopularProductsmodel) {
                    PopularProductsmodel popularProductsmodel = (PopularProductsmodel) obj;
                    amount = popularProductsmodel.getPrice();
                }
                if (obj instanceof ShowAllModel) {
                    ShowAllModel showAllModel = (ShowAllModel) obj;
                    amount = showAllModel.getPrice();
                }
                Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
                intent.putExtra("amount", amount);
                startActivity(intent); // Corrected placement of startActivity
            }
        });

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddressActivity.this, AddAddressActivity.class)); // Fixed semicolon
            }
        });
    }

    private void setUpToolbar() {
        Toolbar toolbar = findViewById(R.id.address_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Address");


        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // This will navigate back to the previous activity
            }
        });
    }

    private void initializeFields() {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.address_recycler);
        paymentBtn = findViewById(R.id.payment_btn);
        addAddressBtn = findViewById(R.id.add_address_btn);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, addressModelList, this);
        recyclerView.setAdapter(addressAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchAddresses() {
        if (auth.getCurrentUser() != null) {
            firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                    .collection("Address").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            addressModelList.clear();
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                AddressModel addressModel = doc.toObject(AddressModel.class);
                                if (addressModel != null) {
                                    addressModelList.add(addressModel);
                                }
                            }
                            addressAdapter.notifyDataSetChanged();
                        }
                    });
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void setAddress(String address) {
        selectedAddress = address;
    }
}
