package com.example.kaizenarts.activites;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kaizenarts.R;
import com.example.kaizenarts.fragments.ExploreFragment;
import com.example.kaizenarts.fragments.HomeFragment;
import com.example.kaizenarts.fragments.ProfileFragment;
import com.example.kaizenarts.fragments.WishlistFragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Hosts the five root tabs (Home / Explore / Try-On / Wishlist / Profile) behind
 * a single custom bottom navigation bar, matching the Kaizen Arts atelier design.
 * Try-On is not a tab fragment - it opens the full-screen camera experience.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "home";
    private static final String TAG_EXPLORE = "explore";
    private static final String TAG_WISHLIST = "wishlist";
    private static final String TAG_PROFILE = "profile";

    private View navHome, navExplore, navTryOn, navWishlist, navProfile;
    private ImageView navHomeIcon, navExploreIcon, navWishlistIcon, navProfileIcon;
    private TextView navHomeLabel, navExploreLabel, navWishlistLabel, navProfileLabel;

    private String currentTag = TAG_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View bottomNav = findViewById(R.id.bottom_nav);
        navHome = bottomNav.findViewById(R.id.nav_home);
        navExplore = bottomNav.findViewById(R.id.nav_explore);
        navTryOn = bottomNav.findViewById(R.id.nav_try_on);
        navWishlist = bottomNav.findViewById(R.id.nav_wishlist);
        navProfile = bottomNav.findViewById(R.id.nav_profile);

        navHomeIcon = bottomNav.findViewById(R.id.nav_home_icon);
        navExploreIcon = bottomNav.findViewById(R.id.nav_explore_icon);
        navWishlistIcon = bottomNav.findViewById(R.id.nav_wishlist_icon);
        navProfileIcon = bottomNav.findViewById(R.id.nav_profile_icon);

        navHomeLabel = bottomNav.findViewById(R.id.nav_home_label);
        navExploreLabel = bottomNav.findViewById(R.id.nav_explore_label);
        navWishlistLabel = bottomNav.findViewById(R.id.nav_wishlist_label);
        navProfileLabel = bottomNav.findViewById(R.id.nav_profile_label);

        navHome.setOnClickListener(v -> selectTab(TAG_HOME));
        navExplore.setOnClickListener(v -> selectTab(TAG_EXPLORE));
        navWishlist.setOnClickListener(v -> selectTab(TAG_WISHLIST));
        navProfile.setOnClickListener(v -> selectTab(TAG_PROFILE));
        navTryOn.setOnClickListener(v -> startActivity(new Intent(this, TryOnActivity.class)));

        if (savedInstanceState == null) {
            selectTab(TAG_HOME);
        }
    }

    private void selectTab(String tag) {
        currentTag = tag;

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // hide every existing tab fragment first
        for (String t : new String[]{TAG_HOME, TAG_EXPLORE, TAG_WISHLIST, TAG_PROFILE}) {
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
            case TAG_PROFILE:
                return new ProfileFragment();
            case TAG_HOME:
            default:
                return new HomeFragment();
        }
    }

    private void updateTabVisuals(String tag) {
        int gold = getResources().getColor(R.color.onboard_gold);
        int ink = getResources().getColor(R.color.onboard_ink);
        int soft = getResources().getColor(R.color.onboard_ink_soft);

        navHomeIcon.setColorFilter(tag.equals(TAG_HOME) ? ink : soft);
        navHomeLabel.setTextColor(tag.equals(TAG_HOME) ? ink : soft);

        navExploreIcon.setColorFilter(tag.equals(TAG_EXPLORE) ? ink : soft);
        navExploreLabel.setTextColor(tag.equals(TAG_EXPLORE) ? ink : soft);

        navWishlistIcon.setColorFilter(tag.equals(TAG_WISHLIST) ? ink : soft);
        navWishlistIcon.setImageResource(tag.equals(TAG_WISHLIST)
                ? R.drawable.ic_nav_wishlist_filled : R.drawable.ic_nav_wishlist_outline);
        navWishlistLabel.setTextColor(tag.equals(TAG_WISHLIST) ? ink : soft);

        navProfileIcon.setColorFilter(tag.equals(TAG_PROFILE) ? ink : soft);
        navProfileLabel.setTextColor(tag.equals(TAG_PROFILE) ? ink : soft);
    }

    /** Called by ProfileFragment (Wishlist row) and product cards to jump tabs from code. */
    public void goToTab(String tag) {
        selectTab(tag);
    }

    public static String tabWishlist() {
        return TAG_WISHLIST;
    }
}
