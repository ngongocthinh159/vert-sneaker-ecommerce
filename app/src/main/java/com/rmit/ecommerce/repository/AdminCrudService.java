package com.rmit.ecommerce.repository;

import static android.app.Activity.RESULT_OK;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.rmit.ecommerce.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class AdminCrudService {
    public static AdminCrudService adminCrudService = new AdminCrudService();
    private List<Uri> imagesEncodedList = new ArrayList<>();
    private String currentSneakerId;
    private Uri selectedImage;

    public Uri getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Uri selectedImage) {
        this.selectedImage = selectedImage;
    }

    public static int ADMIN_RQ_CODE = 96780;
    public static int ADMIN_RQ_SINGLE = 712391;

    public String getCurrentSneakerId() {
        return currentSneakerId;
    }

    public void setCurrentSneakerId(String currentSneakerId) {
        this.currentSneakerId = currentSneakerId;
    }

    public void setImagesEncodedList(List<Uri> imagesEncodedList) {
        this.imagesEncodedList = imagesEncodedList;
    }

    public List<Uri> getImagesEncodedList() {
        return imagesEncodedList;
    }

    public AdminCrudService() {}

    public AdminCrudService getInstance() {
        return adminCrudService;
    }

    public void handleTrendingPhotoPick(int requestCode, int resultCode, @Nullable Intent data) {
        assert data != null;
        setSelectedImage(data.getData());
    }

    public void handlePhotosPick(int requestCode, int resultCode, @Nullable Intent data, ContentResolver contentResolver)  {
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK
                    && null != data) {
                imagesEncodedList = new ArrayList<>();
                if(data.getData()!=null){
                    Uri mImageUri=data.getData();
                    imagesEncodedList.add(mImageUri);
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            imagesEncodedList.add(uri);
                        }
                        Log.v("LOG_TAG", "Selected Images" + imagesEncodedList.size());
                    }
                }
            } else {
                Toast.makeText(MainActivity.context, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.context, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
