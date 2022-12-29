package com.rmit.ecommerce.repository;

import static android.content.ContentValues.TAG;

import android.nfc.Tag;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class RepositoryManager {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<SneakerModel> sneakers = new ArrayList<>();

    public RepositoryManager() {}

    public void fetchAllSneakers() {
        db.collection("sneakers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                System.out.println(document.getId() + "=>" + document.getData().get("size"));
                                Map<String, Object> data = document.getData();
                                sneakers.add(new SneakerModel((String) data.get("title"),
                                        (String) data.get("brand"),
                                        (String) data.get("image"),
                                        (ArrayList<String>) data.get("size")));
                            }
                            for (SneakerModel s : sneakers) {
                                System.out.println(s.getTitle() + " " + s.getBrand());
                            }
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });
    }

    public ArrayList<SneakerModel> getSneakers() {
        return sneakers;
    }
}
