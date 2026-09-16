package com.example.kaizenarts.data;

import androidx.annotation.Nullable;

import com.example.kaizenarts.models.ShowAllModel;
import com.example.kaizenarts.models.WishlistModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/** Thin wrapper around the per-user "Wishlist/{uid}/Items" Firestore collection. */
public class WishlistRepository {

    public interface NamesCallback {
        void onResult(List<String> names);
    }

    public interface ItemsCallback {
        void onResult(List<WishlistModel> items);
    }

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Nullable
    private String uid() {
        return FirebaseAuth.getInstance().getCurrentUser() == null
                ? null
                : FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public boolean isLoggedIn() {
        return uid() != null;
    }

    public void loadSavedNames(NamesCallback callback) {
        String uid = uid();
        if (uid == null) {
            callback.onResult(new ArrayList<>());
            return;
        }
        firestore.collection("Wishlist").document(uid).collection("Items")
                .get()
                .addOnCompleteListener(task -> {
                    List<String> names = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            names.add(doc.getId());
                        }
                    }
                    callback.onResult(names);
                });
    }

    public void loadItems(ItemsCallback callback) {
        String uid = uid();
        if (uid == null) {
            callback.onResult(new ArrayList<>());
            return;
        }
        firestore.collection("Wishlist").document(uid).collection("Items")
                .get()
                .addOnCompleteListener(task -> {
                    List<WishlistModel> items = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot result = task.getResult();
                        for (DocumentSnapshot doc : result.getDocuments()) {
                            WishlistModel model = doc.toObject(WishlistModel.class);
                            if (model != null) items.add(model);
                        }
                    }
                    callback.onResult(items);
                });
    }

    public void add(ShowAllModel product) {
        String uid = uid();
        if (uid == null || product.getName() == null) return;
        firestore.collection("Wishlist").document(uid).collection("Items")
                .document(product.getName())
                .set(new WishlistModel(product));
    }

    public void remove(String productName) {
        String uid = uid();
        if (uid == null || productName == null) return;
        firestore.collection("Wishlist").document(uid).collection("Items")
                .document(productName)
                .delete();
    }
}
