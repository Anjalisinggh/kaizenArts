package com.example.kaizenarts.activites;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.ShowAllAdapter;
import com.example.kaizenarts.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowAllActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    List<ShowAllModel> showAllModelList;
    Toolbar toolbar;
    FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        toolbar = findViewById(R.id.show_all_toolbar);
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

        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        showAllModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, showAllModelList);
        recyclerView.setAdapter(showAllAdapter);

        String type = getIntent().getStringExtra("type");
        if (type == null || type.isEmpty()) {
            fetchAllProducts();
        } else {
            fetchProductsByType(type);
        }
    }

    private void fetchAllProducts() {
        firestore.collection("ShowAll")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            showAllModelList.clear();
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                if (showAllModel != null) {
                                    showAllModelList.add(showAllModel);
                                }
                            }
                            showAllAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("Firestore", "Error fetching all products", task.getException());
                        }
                    }
                });
    }

    private void fetchProductsByType(String type) {
        firestore.collection("ShowAll")
                .whereEqualTo("type", type)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            showAllModelList.clear();
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                ShowAllModel showAllModel = doc.toObject(ShowAllModel.class);
                                if (showAllModel != null) {
                                    showAllModelList.add(showAllModel);
                                }
                            }
                            showAllAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("Firestore", "Error fetching products by type", task.getException());
                        }
                    }
                });
    }
}
