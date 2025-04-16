package com.example.kaizenarts.activites;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.MyCartAdapter;
import com.example.kaizenarts.models.MyCartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class cartActivity extends AppCompatActivity {
    private TextView overAllAmount;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<MyCartModel> cartModelList;
    private MyCartAdapter cartAdapter;
    private Button buyNow;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private double totalAmount = 0.0; // ✅ Fix to store total amount

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        overAllAmount = findViewById(R.id.textView3);
        buyNow = findViewById(R.id.buy_now);
        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        fetchCartItems();

        buyNow.setOnClickListener(view -> {
            if (totalAmount > 0) {
                Intent intent = new Intent(cartActivity.this, PaymentActivity.class);
                intent.putExtra("amount", totalAmount);
                startActivity(intent);
            } else {
                Log.e("CartActivity", "Cart is empty, cannot proceed to payment.");

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private void fetchCartItems() {
        if (auth.getCurrentUser() == null) {
            Log.e("CartActivity", "User not logged in");
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        firestore.collection("AddToCart").document(userId).collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        cartModelList.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                            if (myCartModel != null) {
                                cartModelList.add(myCartModel);
                            }
                        }
                        cartAdapter.notifyDataSetChanged();
                        cartAdapter.calculateTotalAmount();
                    } else {
                        Log.e("Firestore", "Error fetching cart items", task.getException());
                    }
                });
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            totalAmount = intent.getIntExtra("totalAmount", 0);
            overAllAmount.setText("Total Amount: " + totalAmount + " Rs");
        }
    };
}
