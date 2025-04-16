package com.example.kaizenarts.activites;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.kaizenarts.R;
import com.example.kaizenarts.models.NewProductsModel;
import com.example.kaizenarts.models.PopularProductsmodel;
import com.example.kaizenarts.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;



public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg;
    TextView rating, name, description, price,quantity;
    Button addToCart, buyNow;
    ImageView addItems, removeItems;
    Toolbar toolbar;
    int totalQuantity=1;
    int totalPrice =0;

    NewProductsModel newProductsModel = null ;
//popular product
    ShowAllModel showAllModel = null;
    PopularProductsmodel popularProductsmodel=null;
    //show all
    FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);



        toolbar = findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        final Object obj = getIntent().getSerializableExtra("detailed");


        // Initialize views
        detailedImg = findViewById(R.id.detailed_img);
        rating = findViewById(R.id.rating);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);
        addToCart = findViewById(R.id.add_to_cart);
        buyNow = findViewById(R.id.buy_now);
        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);

        // Retrieve product data


        if (obj  instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof PopularProductsmodel ) {
            popularProductsmodel=(PopularProductsmodel) obj;
        }
        else if (obj instanceof ShowAllModel ) {
            showAllModel=(ShowAllModel) obj;
        }
        detailedImg= findViewById(R.id.detailed_img);
        quantity=findViewById(R.id.quantity);
        name=findViewById(R.id.detailed_name);
        rating=findViewById(R.id.rating);
        description=findViewById(R.id.detailed_desc);
        price=findViewById(R.id.detailed_price);

        addToCart=findViewById(R.id.add_to_cart);
        buyNow=findViewById(R.id.buy_now);

        addItems=findViewById(R.id.add_item);
        removeItems=findViewById(R.id.remove_item);

        //new product
        if(newProductsModel != null){
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            rating.setText(newProductsModel.getRating());
            description.setText(newProductsModel.getDescription());
            price.setText(String.valueOf(newProductsModel.getPrice()));
            name.setText(newProductsModel.getName());

            totalPrice=newProductsModel.getPrice()*totalQuantity;

        }
        //popular product
        if(popularProductsmodel != null){
            Glide.with(getApplicationContext()).load(popularProductsmodel.getImg_url()).into(detailedImg);
            name.setText(popularProductsmodel.getName());
            rating.setText(popularProductsmodel.getRating());
            description.setText(popularProductsmodel.getDescription());
            price.setText(String.valueOf(popularProductsmodel.getPrice()));
            name.setText(popularProductsmodel.getName());

            totalPrice=popularProductsmodel.getPrice()*totalQuantity;

        }
        //show all
        if(showAllModel != null){
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailedImg);
            name.setText(showAllModel.getName());
            rating.setText(showAllModel.getRating());
            description.setText(showAllModel.getDescription());
            price.setText(String.valueOf(showAllModel.getPrice()));
            name.setText(showAllModel.getName());

            totalPrice=showAllModel.getPrice()*totalQuantity;
        }
        //Buy now

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the user is logged in
                if (isUserLoggedIn()) {
                    // User is logged in, proceed to AddressActivity
                    Intent intent = new Intent(DetailedActivity.this, AddressActivity.class);

                    // Pass the product data to AddressActivity
                    if (newProductsModel != null) {
                        intent.putExtra("item", newProductsModel);
                    }
                    if (popularProductsmodel != null) {
                        intent.putExtra("item", popularProductsmodel);
                    }
                    if (showAllModel != null) {
                        intent.putExtra("item", showAllModel);
                    }
                    startActivity(intent);
                }
                else {
                    // User is not logged in, redirect to LoginActivity
                    Toast.makeText(DetailedActivity.this, "Please log in to continue.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailedActivity.this, LoginActivity.class);
                    intent.putExtra("from", "DetailedActivity"); // Pass the source activity

                    startActivity(intent);
                }
            }
        });

        //Add to cart
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });


        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(newProductsModel != null){
                        totalPrice =newProductsModel.getPrice()*totalQuantity;

                    }
                    if(popularProductsmodel != null){
                        totalPrice =popularProductsmodel.getPrice()*totalQuantity;

                    }
                    if(showAllModel != null){
                        totalPrice =showAllModel.getPrice()*totalQuantity;

                    }
                }

            }
        });
        removeItems.setOnClickListener(v -> {
            if (totalQuantity > 1) {
                totalQuantity--;
                quantity.setText(String.valueOf(totalQuantity));
            }
        });

    }

    private void addToCart() {

        if (!isUserLoggedIn()) {
            // User not logged in, redirect to LoginActivity
            Toast.makeText(this, "Please log in to add items to cart", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DetailedActivity.this, LoginActivity.class);
            intent.putExtra("from", "DetailedActivity");
            startActivity(intent);
            return;
        }

        String saveCurrentTime,saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String,Object> cartMap = new HashMap<>();

        cartMap.put("productName",name.getText().toString());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("totalQuantity",quantity.getText().toString());
        cartMap.put("totalPrice",totalPrice);


        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            firestore.collection("AddToCart").document(uid)
                    .collection("User").add(cartMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DetailedActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DetailedActivity.this, "Failed to Add To Cart", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(DetailedActivity.this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}
