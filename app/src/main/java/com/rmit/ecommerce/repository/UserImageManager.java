package com.rmit.ecommerce.repository;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.rmit.ecommerce.activity.MainActivity;

import java.io.InputStream;

public class UserImageManager {
    public static int RQ_USER_CODE = 696969;
    private Uri selectedImage;

    public void handlePhotoPick(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK) {
                InputStream inputStream = MainActivity.context.getContentResolver().openInputStream(data.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
