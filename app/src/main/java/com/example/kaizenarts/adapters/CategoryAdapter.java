package com.example.kaizenarts.adapters;

import android.annotation.SuppressLint;
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
import com.example.kaizenarts.activites.ShowAllActivity;
import com.example.kaizenarts.models.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private List<CategoryModel> list;

    public CategoryAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CategoryModel category = list.get(position);
        if (context != null && category != null) {
            Glide.with(context)
                    .load(category.getImg_url())
                    .into(holder.catImg);
            holder.catName.setText(category.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ShowAllActivity.class);
                    intent.putExtra("type",list.get(position).getType());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImg;
        TextView catName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImg = itemView.findViewById(R.id.cat_img);
            catName = itemView.findViewById(R.id.cat_name);
        }
    }
}
