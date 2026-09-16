package com.example.kaizenarts.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kaizenarts.R;
import com.example.kaizenarts.activites.DetailedActivity;
import com.example.kaizenarts.models.ShowAllModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2-column product grid used by both Explore and Wishlist. Wishlist state is kept
 * as a set of product names; tapping the heart toggles it and notifies the host
 * fragment so it can persist the change in Firestore.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ViewHolder> {

    public interface OnWishlistToggle {
        void onToggle(ShowAllModel product, boolean nowSaved);
    }

    private final Context context;
    private final List<ShowAllModel> list;
    private final Set<String> savedNames;
    private final OnWishlistToggle listener;

    public ProductGridAdapter(Context context, List<ShowAllModel> list, Set<String> savedNames, OnWishlistToggle listener) {
        this.context = context;
        this.list = list;
        this.savedNames = savedNames != null ? savedNames : new HashSet<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowAllModel product = list.get(position);

        Glide.with(context).load(product.getImg_url()).into(holder.image);
        holder.name.setText(product.getName());
        holder.desc.setText(product.getDescription());
        holder.price.setText(String.format("₹ %d", product.getPrice()));

        boolean saved = savedNames.contains(product.getName());
        holder.wishlistIcon.setImageResource(saved ? R.drawable.ic_nav_wishlist_filled : R.drawable.ic_nav_wishlist_outline);
        holder.wishlistIcon.setColorFilter(saved
                ? context.getResources().getColor(R.color.color_gold)
                : context.getResources().getColor(R.color.color_champagne));

        holder.wishlistBtn.setOnClickListener(v -> {
            boolean nowSaved = !savedNames.contains(product.getName());
            if (nowSaved) {
                savedNames.add(product.getName());
            } else {
                savedNames.remove(product.getName());
            }
            notifyItemChanged(holder.getAdapterPosition());
            if (listener != null) listener.onToggle(product, nowSaved);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailedActivity.class);
            intent.putExtra("detailed", product);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, wishlistIcon;
        View wishlistBtn;
        TextView name, desc, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.grid_img);
            wishlistIcon = itemView.findViewById(R.id.grid_wishlist_icon);
            wishlistBtn = itemView.findViewById(R.id.grid_wishlist_btn);
            name = itemView.findViewById(R.id.grid_name);
            desc = itemView.findViewById(R.id.grid_desc);
            price = itemView.findViewById(R.id.grid_price);
        }
    }
}
