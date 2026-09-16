package com.example.kaizenarts.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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
            R.drawable.banner11,
            R.drawable.banner22,
            R.drawable.banner33
    };

    private final int[] headingArray = {
            R.string.first_slide,
            R.string.second_slide,
            R.string.third_slide
    };

    private final int[] descriptionArray = {
            R.string.first_slide_desc,
            R.string.second_slide_desc,
            R.string.third_slide_desc
    };

    private final int[] stepArray = {
            R.string.onboard_step_one,
            R.string.onboard_step_two,
            R.string.onboard_step_three
    };

    @Override
    public int getCount() {
        return headingArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sliding_layout, container, false);

        ImageView imageView = view.findViewById(R.id.slider_img);
        TextView stepLabel = view.findViewById(R.id.stepLabel);
        TextView heading = view.findViewById(R.id.heading);
        TextView description = view.findViewById(R.id.description);

        imageView.setImageResource(imageArray[position]);
        stepLabel.setText(stepArray[position]);
        heading.setText(headingArray[position]);
        description.setText(descriptionArray[position]);

        imageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_scale_in));
        heading.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_up));
        description.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in_up));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
