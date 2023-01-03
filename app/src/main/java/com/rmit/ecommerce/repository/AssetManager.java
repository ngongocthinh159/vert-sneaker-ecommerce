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

    public Drawable fetchImage(String key, RecyclerView.Adapter adapter) {
        if (cache.containsKey(key)) {
            System.out.println("Cached invoked, KEY: " + key);
            return cache.get(key);
        } else {
            StorageReference gsReference = storage.getReferenceFromUrl(key);
            final long TWO_MEGABYTE = 2048 * 2048;
            gsReference.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("ON SUCCESS");
                    cache.put(key, Drawable.createFromStream(new ByteArrayInputStream(bytes), null));
                    adapter.notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
            if (cache.containsKey(key)) {
                System.out.println("RETURN THE IMAGE");
                return cache.get(key);
            } else {
                System.out.println("NULL detected");
                return null;
            }
        }
    }

    public void fetchImage(String key, RecyclerView.Adapter adapter, ImageView imageView, int position) {
        if (cache.containsKey(key)) {
            System.out.println("Cached invoked, KEY: " + key);
                imageView.setImageDrawable(cache.get(key));
        } else {
            StorageReference gsReference = storage.getReferenceFromUrl(key);
            final long TWO_MEGABYTE = 2048 * 2048;
            gsReference.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    System.out.println("ON SUCCESS");
                    cache.put(key, Drawable.createFromStream(new ByteArrayInputStream(bytes), null));
                    imageView.setImageDrawable(cache.get(key));
                    adapter.notifyItemChanged(position);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        }
    }

    public Drawable fetchImageFromFolder(String key, RecyclerView.Adapter adapter) {
        StorageReference gsReference = storage.getReferenceFromUrl(key);
        final String[] tempKey = {""};
        gsReference.listAll()
                .addOnSuccessListener(listResult -> {
//                        for (StorageReference prefix : listResult.getPrefixes()) {
//                            // All the prefixes under listRef.
//                            // You may call listAll() recursively on them.
//                            System.out.println(prefix);
//                        }

//                        for (StorageReference item : listResult.getItems()) {
//                            System.out.println(item.getName());
//                            System.out.println(item.getBucket());
//
//                            // All the items under listRef.
//                        }
                    tempKey[0] = key + "/" + listResult.getItems().get(0).getName();
                    fetchImage(tempKey[0], adapter);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
        if (cache.containsKey(tempKey[0])) {
            return cache.get(tempKey[0]);
        }
        // TODO: Return default image
        return null;
    }


    public AssetManager() {}

    public static AssetManager getInstance() {
        return assetManager;
    }
}
