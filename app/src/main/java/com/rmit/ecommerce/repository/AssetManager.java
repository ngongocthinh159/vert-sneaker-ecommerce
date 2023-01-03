package com.rmit.ecommerce.repository;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;

public class AssetManager {
    private static AssetManager assetManager = new AssetManager();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private HashMap<String, Drawable> cache = new HashMap<>();

    public Drawable fetchImage(String key) {
        Drawable drawable;
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            StorageReference gsReference = storage.getReferenceFromUrl(key);
            final long TWO_MEGABYTE = 2048 * 2048;
            gsReference.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    cache.put(key, Drawable.createFromStream(new ByteArrayInputStream(bytes), null));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
            if (cache.containsKey(key)) {
                return cache.get(key);
            } else {
                return null;
            }
        }
    }

    public AssetManager() {}

    public static AssetManager getInstance() {
        return assetManager;
    }
}
