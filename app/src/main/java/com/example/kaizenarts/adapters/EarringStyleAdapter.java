package com.example.kaizenarts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;

import java.util.List;

public class EarringStyleAdapter extends RecyclerView.Adapter<EarringStyleAdapter.ViewHolder> {

    public interface OnStyleSelected {
        void onSelected(int drawableRes);
    }

    private final Context context;
    private final List<Integer> drawableResIds;
    private final OnStyleSelected listener;
    private int selectedPosition = 0;

    public EarringStyleAdapter(Context context, List<Integer> drawableResIds, OnStyleSelected listener) {
        this.context = context;
        this.drawableResIds = drawableResIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earring_style, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int resId = drawableResIds.get(position);
        holder.icon.setImageResource(resId);
        holder.frame.setBackgroundResource(position == selectedPosition
                ? R.drawable.bg_earring_swatch_selected : R.drawable.bg_earring_swatch);

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            if (listener != null) listener.onSelected(resId);
        });
    }

    @Override
    public int getItemCount() {
        return drawableResIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout frame;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            frame = (FrameLayout) itemView;
            icon = itemView.findViewById(R.id.style_icon);
        }
    }
}
