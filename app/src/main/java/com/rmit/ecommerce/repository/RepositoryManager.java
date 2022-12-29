package com.rmit.ecommerce.repository;

import static android.content.ContentValues.TAG;

import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map;

public class RepositoryManager {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static ArrayList<SneakerModel> sneakers = new ArrayList<>();

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
                                System.out.println(s.getTitle() + " - " + s.getBrand() + " - " + s.getImage());
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

    public Drawable getImageInByte(String storageURL) {
        if (storageURL == null || storageURL.isEmpty()) return null;
        StorageReference gsReference = storage.getReferenceFromUrl(storageURL);

        final long ONE_MEGABYTE = 1024 * 1024;
        final byte[][] imageData = {null};
        gsReference.getBytes(ONE_MEGABYTE * 30).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                imageData[0] = bytes;
            }
        });

        // If get the image successfully => return drawable
        if (imageData[0] != null) return Drawable.createFromStream(new ByteArrayInputStream(imageData[0]), null);

        return null;
    }

    public void signInAnonymously() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            mAuth.signInAnonymously();
        }
    }
}
