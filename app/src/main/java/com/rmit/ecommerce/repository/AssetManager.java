package com.rmit.ecommerce.repository;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.rmit.ecommerce.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;

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

    public ArrayList<Drawable> fetchAllImages(String imgUrl, ImageSlider imageSlider, ArrayList<SlideModel> slideModels) {
        ArrayList<Drawable> drawables = new ArrayList<>();

        StorageReference gsReference = storage.getReferenceFromUrl(imgUrl);
        gsReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    // All the items under listRef.
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            slideModels.add(new SlideModel(String.valueOf(uri),  ScaleTypes.CENTER_INSIDE));
                            imageSlider.setImageList(slideModels);
                        }
                    });
                }
            }
        });

        return drawables;
    }

    public AssetManager() {}

    public static AssetManager getInstance() {
        return assetManager;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }
}
