package com.example.kaizenarts.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.ProductGridAdapter;
import com.example.kaizenarts.data.WishlistRepository;
import com.example.kaizenarts.models.ShowAllModel;
import com.example.kaizenarts.models.WishlistModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Saved pieces, backed by the same per-user Firestore wishlist used from Explore. */
public class WishlistFragment extends Fragment {

    private final List<ShowAllModel> savedProducts = new ArrayList<>();
    private final Set<String> savedNames = new HashSet<>();
    private ProductGridAdapter adapter;
    private final WishlistRepository wishlistRepository = new WishlistRepository();

    private View emptyState;
    private TextView countLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wishlist, container, false);

        emptyState = root.findViewById(R.id.wishlist_empty);
        countLabel = root.findViewById(R.id.wishlist_count);

        RecyclerView recyclerView = root.findViewById(R.id.wishlist_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new ProductGridAdapter(getContext(), savedProducts, savedNames, (product, nowSaved) -> {
            if (nowSaved) {
                wishlistRepository.add(product);
            } else {
                wishlistRepository.remove(product.getName());
                savedProducts.remove(product);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }
        });
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        wishlistRepository.loadItems(items -> {
            savedProducts.clear();
            savedNames.clear();
            for (WishlistModel item : items) {
                savedProducts.add(item.toShowAllModel());
                savedNames.add(item.getName());
            }
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private void updateEmptyState() {
        countLabel.setText(savedProducts.size() + (savedProducts.size() == 1 ? " piece saved" : " pieces saved"));
        emptyState.setVisibility(savedProducts.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
