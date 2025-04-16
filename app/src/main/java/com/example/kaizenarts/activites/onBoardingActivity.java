package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.SliderAdapter;

public class onBoardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btn;
    private SliderAdapter sliderAdapter;
    private TextView[] dots;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        // Hide Action Bar (Prevents potential crashes)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Views
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        btn = findViewById(R.id.get_started_btn);

        // Ensure button is initially hidden
        btn.setVisibility(View.INVISIBLE);

        // Setup ViewPager and Adapter
        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        // Add Dots for navigation
        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

        // Handle Button Click
        btn.setOnClickListener(v -> {
            startActivity(new Intent(onBoardingActivity.this, MainActivity.class));
            finish();
        });
    }

    private void addDots(int position) {
        dots = new TextView[3]; // Update this if slide count changes
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));  // Bullet point
            dots[i].setTextSize(35);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.pink));  // Default color
            dotsLayout.addView(dots[i]);
        }

        // Ensure position is valid before modifying the array
        if (dots.length > 0 && position < dots.length) {
            dots[position].setTextColor(ContextCompat.getColor(this, R.color.white));  // Highlight current dot
        }
    }

    private final ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addDots(position);

            if (position == 2) {  // Last slide
                animation = AnimationUtils.loadAnimation(onBoardingActivity.this, R.anim.slide_animation);
                btn.setAnimation(animation);
                btn.setVisibility(View.VISIBLE);
            } else {
                btn.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
