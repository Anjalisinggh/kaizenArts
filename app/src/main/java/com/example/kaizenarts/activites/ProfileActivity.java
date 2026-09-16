package com.example.kaizenarts.activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kaizenarts.R;
import com.example.kaizenarts.fragments.ProfileFragment;

/** Hosts the account/profile screen, reached from the header account icon (not a bottom tab). */
public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.profile_fragment_container, new ProfileFragment())
                    .commit();
        }
    }
}
