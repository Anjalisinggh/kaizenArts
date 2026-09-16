package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kaizenarts.R;
import com.example.kaizenarts.fragments.ExploreFragment;
import com.example.kaizenarts.fragments.HomeFragment;
import com.example.kaizenarts.fragments.WishlistFragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Hosts the four root tabs (Home / Explore / Wishlist / Cart) behind a single
 * custom bottom navigation bar, with Try-On raised as the standout action.
 * Try-On and Cart are not tab fragments - Cart reuses the existing cartActivity
 * and Try-On opens the full-screen camera experience.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "home";
    private static final String TAG_EXPLORE = "explore";
    private static final String TAG_WISHLIST = "wishlist";

    private ImageView navHomeIcon, navExploreIcon, navWishlistIcon;
    private TextView navHomeLabel, navExploreLabel, navWishlistLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View bottomNav = findViewById(R.id.bottom_nav);
        View navHome = bottomNav.findViewById(R.id.nav_home);
        View navExplore = bottomNav.findViewById(R.id.nav_explore);
        View navTryOn = bottomNav.findViewById(R.id.nav_try_on);
        View navWishlist = bottomNav.findViewById(R.id.nav_wishlist);
        View navCart = bottomNav.findViewById(R.id.nav_cart);

        navHomeIcon = bottomNav.findViewById(R.id.nav_home_icon);
        navExploreIcon = bottomNav.findViewById(R.id.nav_explore_icon);
        navWishlistIcon = bottomNav.findViewById(R.id.nav_wishlist_icon);

        navHomeLabel = bottomNav.findViewById(R.id.nav_home_label);
        navExploreLabel = bottomNav.findViewById(R.id.nav_explore_label);
        navWishlistLabel = bottomNav.findViewById(R.id.nav_wishlist_label);

        navHome.setOnClickListener(v -> selectTab(TAG_HOME));
        navExplore.setOnClickListener(v -> selectTab(TAG_EXPLORE));
        navWishlist.setOnClickListener(v -> selectTab(TAG_WISHLIST));
        navTryOn.setOnClickListener(v -> startActivity(new Intent(this, TryOnActivity.class)));
        navCart.setOnClickListener(v -> startActivity(new Intent(this, cartActivity.class)));

        if (savedInstanceState == null) {
            selectTab(TAG_HOME);
        }
    }

    private void selectTab(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        for (String t : new String[]{TAG_HOME, TAG_EXPLORE, TAG_WISHLIST}) {
            Fragment existing = getSupportFragmentManager().findFragmentByTag(t);
            if (existing != null) {
                transaction.hide(existing);
            }
        }

        if (fragment == null) {
            fragment = createFragment(tag);
            transaction.add(R.id.fragment_container, fragment, tag);
        } else {
            transaction.show(fragment);
        }

        transaction.commit();
        updateTabVisuals(tag);
    }

    private Fragment createFragment(String tag) {
        switch (tag) {
            case TAG_EXPLORE:
                return new ExploreFragment();
            case TAG_WISHLIST:
                return new WishlistFragment();
            case TAG_HOME:
            default:
                return new HomeFragment();
        }
    }

    private void updateTabVisuals(String tag) {
        int ink = getResources().getColor(R.color.color_espresso);
        int soft = getResources().getColor(R.color.color_warm_gray);

        navHomeIcon.setColorFilter(tag.equals(TAG_HOME) ? ink : soft);
        navHomeLabel.setTextColor(tag.equals(TAG_HOME) ? ink : soft);

        navExploreIcon.setColorFilter(tag.equals(TAG_EXPLORE) ? ink : soft);
        navExploreLabel.setTextColor(tag.equals(TAG_EXPLORE) ? ink : soft);

        navWishlistIcon.setColorFilter(tag.equals(TAG_WISHLIST) ? ink : soft);
        navWishlistIcon.setImageResource(tag.equals(TAG_WISHLIST)
                ? R.drawable.ic_nav_wishlist_filled : R.drawable.ic_nav_wishlist_outline);
        navWishlistLabel.setTextColor(tag.equals(TAG_WISHLIST) ? ink : soft);
    }

    /** Called from code (e.g. product cards) to jump tabs. */
    public void goToTab(String tag) {
        selectTab(tag);
    }

    public static String tabWishlist() {
        return TAG_WISHLIST;
    }

    public static String tabExplore() {
        return TAG_EXPLORE;
    }
}
