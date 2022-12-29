package com.rmit.ecommerce.repository;

import static android.content.ContentValues.TAG;

import android.nfc.Tag;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RepositoryManager {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RepositoryManager() {}

    public void getAllSneakers() {
        db.collection("sneakers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                System.out.println(document.getId() + "=>" + document.getData().get("size"));

                            }
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });
    }
}
