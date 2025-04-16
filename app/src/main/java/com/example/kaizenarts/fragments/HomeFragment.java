package com.example.kaizenarts.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.example.kaizenarts.activites.ShowAllActivity;
import com.example.kaizenarts.adapters.NewProductsAdapter;
import com.example.kaizenarts.adapters.PopularProductsAdapter;
import com.example.kaizenarts.models.NewProductsModel;
import com.example.kaizenarts.models.PopularProductsmodel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.CategoryAdapter;
import com.example.kaizenarts.models.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    TextView catShowAll, popularShowAll, newProductShowAll;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView catRecycleview, newProductRecyclerview, popularRecycleview;

    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    // New Products
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

    // Popular Products
    PopularProductsAdapter popularProductsAdapter;
    List<PopularProductsmodel> popularProductsmodelList;

    FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Welcome to Kaizen Arts!!");
        progressDialog.setMessage("Loading, please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show(); // Show progress dialog while fetching data

        // Initialize Views
        catRecycleview = root.findViewById(R.id.rec_category);
        newProductRecyclerview = root.findViewById(R.id.new_product_rec);
        popularRecycleview = root.findViewById(R.id.popular_rec);
        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE); // Initially hide layout

        catShowAll = root.findViewById(R.id.category_see_all);
        popularShowAll = root.findViewById(R.id.popular_see_all);
        newProductShowAll = root.findViewById(R.id.newProducts_see_all);

        // Click Listeners for "See All"
        View.OnClickListener seeAllClickListener = v -> startActivity(new Intent(getContext(), ShowAllActivity.class));
        catShowAll.setOnClickListener(seeAllClickListener);
        newProductShowAll.setOnClickListener(seeAllClickListener);
        popularShowAll.setOnClickListener(seeAllClickListener);

        // Setup Image Slider
        setupImageSlider(root);

        // Setup RecyclerViews
        setupCategoryRecyclerView();
        setupNewProductsRecyclerView();
        setupPopularProductsRecyclerView();

        return root;
    }

    private void setupImageSlider(View root) {
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        if (imageSlider != null) {
            List<SlideModel> slideModels = new ArrayList<>();
            slideModels.add(new SlideModel(R.drawable.banner11, "Upto 70% Discount on Earrings", ScaleTypes.FIT));
            slideModels.add(new SlideModel(R.drawable.banner22, "Huge Discount on Bangles", ScaleTypes.FIT));
            slideModels.add(new SlideModel(R.drawable.banner33, "Discount on these lovely Chokers", ScaleTypes.FIT));

            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        }
    }

    private void setupCategoryRecyclerView() {
        catRecycleview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        catRecycleview.setAdapter(categoryAdapter);

        db.collection("Category")
                .get()
                .addOnCompleteListener(task -> handleCategoryResult(task));
    }

    private void handleCategoryResult(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                categoryModelList.add(document.toObject(CategoryModel.class));
            }
            categoryAdapter.notifyDataSetChanged();
            checkDataLoaded();
        } else {
            Toast.makeText(getActivity(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNewProductsRecyclerView() {
        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getContext(), newProductsModelList);
        newProductRecyclerview.setAdapter(newProductsAdapter);

        db.collection("NewProducts")
                .get()
                .addOnCompleteListener(task -> handleNewProductsResult(task));
    }

    private void handleNewProductsResult(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                newProductsModelList.add(document.toObject(NewProductsModel.class));
            }
            newProductsAdapter.notifyDataSetChanged();
            checkDataLoaded();
        } else {
            Toast.makeText(getActivity(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPopularProductsRecyclerView() {
        popularRecycleview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        popularProductsmodelList = new ArrayList<>();
        popularProductsAdapter = new PopularProductsAdapter(getContext(), popularProductsmodelList);
        popularRecycleview.setAdapter(popularProductsAdapter);

        db.collection("AllProducts")
                .get()
                .addOnCompleteListener(task -> handlePopularProductsResult(task));
    }

    private void handlePopularProductsResult(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                popularProductsmodelList.add(document.toObject(PopularProductsmodel.class));
            }
            popularProductsAdapter.notifyDataSetChanged();
            checkDataLoaded();
        } else {
            Toast.makeText(getActivity(), "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkDataLoaded() {
        if (!categoryModelList.isEmpty() && !newProductsModelList.isEmpty() && !popularProductsmodelList.isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }
    }
}
