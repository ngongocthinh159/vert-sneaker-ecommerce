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

    public void handlePhotosPick(int requestCode, int resultCode, @Nullable Intent data, ContentResolver contentResolver)  {
        int PICK_IMAGE_MULTIPLE = 1;
        System.out.println("START ACTIVITY RESULT");
        System.out.println(resultCode);
        System.out.println(requestCode);
        System.out.println(data.getData());
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
                        ArrayList<Uri> mArrayUri = new ArrayList<>();
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
