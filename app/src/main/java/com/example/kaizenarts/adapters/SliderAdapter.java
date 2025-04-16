package com.example.kaizenarts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.kaizenarts.R;

public class SliderAdapter extends PagerAdapter {

    private final Context context;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    private final int[] imageArray = {
            R.drawable.onboardscreen1,
            R.drawable.onboardscreen2,
            R.drawable.onboardscreen3
    };

    private final int[] headingArray = {
            R.string.first_slide,
            R.string.second_slide,
            R.string.third_slide
    };

    private final int[] descriptionArray = {
            R.string.description,
            R.string.description,
            R.string.description
    };

    @Override
    public int getCount() {
        return headingArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object; // Fixed potential type mismatch issue
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sliding_layout, container, false);

        ImageView imageView = view.findViewById(R.id.slider_img);
        TextView heading = view.findViewById(R.id.heading);
        TextView description = view.findViewById(R.id.description);

        // Set image, heading, and description dynamically
        imageView.setImageResource(imageArray[position]);
        heading.setText(headingArray[position]);
        description.setText(descriptionArray[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object); // Fixed incorrect casting
    }
}
