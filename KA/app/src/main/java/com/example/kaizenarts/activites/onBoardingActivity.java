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
import androidx.viewpager.widget.ViewPager;

import com.example.kaizenarts.R;
import com.example.kaizenarts.adapters.SliderAdapter;

public class onBoardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout dotsLayout;

    Button btn;
    SliderAdapter sliderAdapter;

    TextView[] dots;
    Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_on_boarding);

        getSupportActionBar();
        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        btn = findViewById(R.id.get_started_btn);
        addDots(0);

        viewPager.addOnPageChangeListener(changeListener);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(onBoardingActivity.this,registrationActivity.class));
            finish();
            }
        });

    }

    private void addDots(int position) {

        dots = new TextView[3];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));  // Use bullet character
            dots[i].setTextSize(35);  // Set size of dots
            dots[i].setTextColor(getResources().getColor(R.color.pink));  // Set default color for inactive dots
            dotsLayout.addView(dots[i]);
        }
        // Set the active dot color
        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.white));  // Highlight the current dot
        }
    }
    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Handle the event when the page is being scrolled
        }

        @Override
        public void onPageSelected(int position) {

            addDots(position);
            if (position == 0){
                btn.setVisibility(View.INVISIBLE);
            }else if(position == 1){
                btn.setVisibility(View.INVISIBLE);
            }else{
                animation= AnimationUtils.loadAnimation(onBoardingActivity.this,R.anim.slide_animation);
                btn.setAnimation(animation);
                btn.setVisibility(View.VISIBLE);
            }

            // Handle the event when a new page is selected
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            // Handle the event when the scroll state changes
        }
    };

}
