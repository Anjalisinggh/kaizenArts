package com.example.kaizenarts.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kaizenarts.R; // Replace with your actual package and layout imports.
import com.example.kaizenarts.activites.DetailedActivity;
import com.example.kaizenarts.models.NewProductsModel;

import java.util.List;

public class NewProductsAdapter extends RecyclerView.Adapter<NewProductsAdapter.ViewHolder> {

    private Context context;
    private List<NewProductsModel>list;
    public NewProductsAdapter(Context context,List<NewProductsModel>list)
    {
        this.context=context;
        this.list=list;
    }
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.new_products,parent,false)) ;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Bind data to the ViewHolder here
        Glide.with(context).load(list.get(position).getImg_url()).into(holder.newImg);
        holder.newName.setText(list.get(position).getName());
        holder.newPrice.setText(String.valueOf(list.get(position).getPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed",list.get(position));
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        // Return the number of items in your dataset
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        ImageView newImg;
        TextView newName,newPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newImg = itemView.findViewById(R.id.new_img);
            newName = itemView.findViewById(R.id.new_product_name);
            newPrice = itemView.findViewById(R.id.new_price);


            // Initialize your views
            // here using itemView.findViewById
        }
    }
}
