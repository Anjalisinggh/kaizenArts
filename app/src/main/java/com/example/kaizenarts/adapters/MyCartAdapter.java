package com.example.kaizenarts.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.models.MyCartModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.ViewHolder> {
    private final Context context;
    private final List<MyCartModel> list;

    public MyCartAdapter(Context context, List<MyCartModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyCartModel model = list.get(position);

        holder.date.setText(model.getCurrentDate() != null ? model.getCurrentDate() : "N/A");
        holder.time.setText(model.getCurrentTime() != null ? model.getCurrentTime() : "N/A");
        holder.price.setText(String.format("%s Rs", model.getProductPrice() != null ? model.getProductPrice() : "N/A"));
        holder.name.setText(model.getProductName() != null ? model.getProductName() : "N/A");
        holder.totalQuantity.setText(model.getTotalQuantity() != null ? model.getTotalQuantity() : "0");

        // ✅ Fixed null issue using Objects.requireNonNullElse
        int totalPrice = Objects.requireNonNullElse(model.getTotalPrice(), 0);
        holder.totalPrice.setText(String.format("%d Rs", totalPrice));

        holder.remove_button.setOnClickListener(v -> {
            removeFromCart(model.getProductName(), position);
        });
    }

    // Function to remove item from Firestore and update UI
    private void removeFromCart(String productId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference cartItemRef = db.collection("Cart").document(userId)
                .collection("Items").document(productId);

        cartItemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // ✅ Remove from list only if deletion is successful
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size()); // Update UI properly
                    calculateTotalAmount(); // ✅ Update total after removal
                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, date, time, totalQuantity, totalPrice,remove_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            totalQuantity = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
            remove_button = itemView.findViewById(R.id.remove_button);
        }
    }

    // ✅ Optimized: Call this only when dataset changes
    public void calculateTotalAmount() {
        int totalAmount = 0;
        for (MyCartModel item : list) {
            totalAmount += Objects.requireNonNullElse(item.getTotalPrice(), 0);
        }

        Intent intent = new Intent("MyTotalAmount");
        intent.putExtra("totalAmount", totalAmount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // ✅ New Method: Update list safely
    public void updateList(List<MyCartModel> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
        calculateTotalAmount(); // ✅ Update total when data changes
    }
}
