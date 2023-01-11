package com.rmit.ecommerce.repository;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmit.ecommerce.activity.MainActivity;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.UUID;

import io.grpc.Context;

public class UserImageManager {
    public static int RQ_USER_CODE = 696969;
    private Uri selectedImage = null;
    public static UserImageManager userImageManager = new UserImageManager();
    private boolean shouldFetch = false;

    public boolean isShouldFetch() {
        return shouldFetch;
    }

    public void setShouldFetch(boolean shouldFetch) {
        this.shouldFetch = shouldFetch;
    }

    public Uri getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Uri selectedImage) {
        this.selectedImage = selectedImage;
    }

    public void handlePhotoPick(int requestCode, int resultCode, Intent data) {
        System.out.println("Handle photo pick");
        try {
            if (resultCode == Activity.RESULT_OK) {
                selectedImage = (data.getData());
                System.out.println(selectedImage);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference().child("userimages");
                InputStream stream = MainActivity.context.getContentResolver().openInputStream(selectedImage);
                byte[] targetArray = IOUtils.toByteArray(stream);
                UploadTask uploadTask = storageRef.child(FirebaseAuth.getInstance().getUid() + "/avatar").putBytes(targetArray);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        System.out.println("FAILED TO UPLOAD");
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("UPLOAD OK");
                    }
                });

                setShouldFetch(true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserImageManager getInstance() {
        return userImageManager;
    }


}
