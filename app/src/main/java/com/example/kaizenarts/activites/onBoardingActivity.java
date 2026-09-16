package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.SliderAdapter;
import com.google.android.material.button.MaterialButton;

public class onBoardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private MaterialButton btn;
    private TextView skipBtn;
    private SliderAdapter sliderAdapter;
    private View[] indicators;
    private Animation animation;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_ivory));

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        btn = findViewById(R.id.get_started_btn);
        skipBtn = findViewById(R.id.skip_btn);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addIndicators(0);
        viewPager.addOnPageChangeListener(changeListener);

        btn.setOnClickListener(v -> {
            if (currentPage < sliderAdapter.getCount() - 1) {
                viewPager.setCurrentItem(currentPage + 1, true);
            } else {
                finishOnboarding();
            }
        });

        skipBtn.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        startActivity(new Intent(onBoardingActivity.this, MainActivity.class));
        finish();
    }

    private void addIndicators(int position) {
        indicators = new View[3];
        dotsLayout.removeAllViews();

        for (int i = 0; i < indicators.length; i++) {
            View indicator = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dp(i == position ? 36 : 18),
                    dp(3)
            );
            params.setMargins(dp(4), 0, dp(4), 0);
            indicator.setLayoutParams(params);
            indicator.setBackgroundResource(
                    i == position
                            ? R.drawable.indicator_onboarding_active
                            : R.drawable.indicator_onboarding_inactive
            );
            indicators[i] = indicator;
            dotsLayout.addView(indicator);
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void updatePrimaryButton(int position) {
        boolean isLast = position == sliderAdapter.getCount() - 1;
        btn.setText(isLast ? R.string.onboard_start : R.string.onboard_next);
        skipBtn.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);

        if (isLast) {
            animation = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
            btn.startAnimation(animation);
        }
    }

    private final ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            currentPage = position;
            addIndicators(position);
            updatePrimaryButton(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
}
