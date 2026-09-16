package com.example.kaizenarts.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kaizenarts.R;
import com.example.kaizenarts.activites.AddressActivity;
import com.example.kaizenarts.activites.LoginActivity;
import com.example.kaizenarts.activites.MainActivity;
import com.example.kaizenarts.adapters.TryOnLookAdapter;
import com.example.kaizenarts.data.TryOnLookStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView looksRec;
    private View looksEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView name = root.findViewById(R.id.profile_name);
        TextView email = root.findViewById(R.id.profile_email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty()
                    ? user.getDisplayName() : "Kaizen Member");
            email.setText(user.getEmail() != null ? user.getEmail() : "");
        } else {
            name.setText("Guest");
            email.setText("Not signed in");
        }

        looksRec = root.findViewById(R.id.profile_looks_rec);
        looksRec.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        looksEmpty = root.findViewById(R.id.profile_looks_empty);

        root.findViewById(R.id.row_orders).setOnClickListener(v -> toast("Orders coming soon"));
        root.findViewById(R.id.row_wishlist).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goToTab(MainActivity.tabWishlist());
            }
        });
        root.findViewById(R.id.row_saved_looks).setOnClickListener(v -> toast("Saved Looks coming soon"));
        root.findViewById(R.id.row_try_on_history).setOnClickListener(v -> toast("Try-On History coming soon"));
        root.findViewById(R.id.row_recently_viewed).setOnClickListener(v -> toast("Recently Viewed coming soon"));
        root.findViewById(R.id.row_addresses).setOnClickListener(v -> startActivity(new Intent(getContext(), AddressActivity.class)));
        root.findViewById(R.id.row_payment).setOnClickListener(v -> toast("Payment coming soon"));
        root.findViewById(R.id.row_notifications).setOnClickListener(v -> toast("Notifications coming soon"));
        root.findViewById(R.id.row_settings).setOnClickListener(v -> toast("Settings coming soon"));
        root.findViewById(R.id.row_sign_out).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() == null) return;
        List<File> looks = TryOnLookStore.listNewestFirst(getContext());
        looksRec.setAdapter(new TryOnLookAdapter(getContext(), looks));
        looksEmpty.setVisibility(looks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void toast(String message) {
        if (getContext() != null) Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
