package com.example.kaizenarts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kaizenarts.R;

import java.io.File;
import java.util.List;

public class TryOnLookAdapter extends RecyclerView.Adapter<TryOnLookAdapter.ViewHolder> {

    private final Context context;
    private final List<File> looks;

    public TryOnLookAdapter(Context context, List<File> looks) {
        this.context = context;
        this.looks = looks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_try_on_look, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(looks.get(position)).centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        return looks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = (ImageView) itemView;
        }
    }
}
