package com.example.kaizenarts.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.ProductGridAdapter;
import com.example.kaizenarts.data.WishlistRepository;
import com.example.kaizenarts.models.ShowAllModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Discover / shop grid: search over the "ShowAll" catalogue, wishlist toggling. */
public class ExploreFragment extends Fragment {

    private final List<ShowAllModel> allProducts = new ArrayList<>();
    private final List<ShowAllModel> visibleProducts = new ArrayList<>();
    private ProductGridAdapter adapter;
    private final WishlistRepository wishlistRepository = new WishlistRepository();
    private final Set<String> savedNames = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_explore, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.explore_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductGridAdapter(getContext(), visibleProducts, savedNames, (product, nowSaved) -> {
            if (nowSaved) {
                wishlistRepository.add(product);
            } else {
                wishlistRepository.remove(product.getName());
            }
        });
        recyclerView.setAdapter(adapter);

        EditText searchInput = root.findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        wishlistRepository.loadSavedNames(names -> {
            savedNames.clear();
            savedNames.addAll(names);
            adapter.notifyDataSetChanged();
        });

        loadProducts();

        return root;
    }

    private void loadProducts() {
        FirebaseFirestore.getInstance().collection("ShowAll")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allProducts.clear();
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            ShowAllModel model = doc.toObject(ShowAllModel.class);
                            if (model != null) allProducts.add(model);
                        }
                        visibleProducts.clear();
                        visibleProducts.addAll(allProducts);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void filter(String query) {
        String needle = query.trim().toLowerCase(Locale.getDefault());
        visibleProducts.clear();
        if (needle.isEmpty()) {
            visibleProducts.addAll(allProducts);
        } else {
            for (ShowAllModel product : allProducts) {
                boolean matchesName = product.getName() != null && product.getName().toLowerCase(Locale.getDefault()).contains(needle);
                boolean matchesType = product.getType() != null && product.getType().toLowerCase(Locale.getDefault()).contains(needle);
                if (matchesName || matchesType) {
                    visibleProducts.add(product);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
