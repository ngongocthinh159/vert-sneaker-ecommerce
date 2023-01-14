package com.rmit.ecommerce.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class MLManager {
    public static MLManager mlManager = new MLManager();
    public static int RQ_ML_CODE = 677781;
    public HashMap<Integer, String> labelMap = new HashMap<>();

    public MLManager getInstance() {
        return this.mlManager;
    }

    private Uri selectedImage;

    public Uri getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(Uri selectedImage) {
        this.selectedImage = selectedImage;
    }

    LocalModel localModel = new LocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build();
    CustomImageLabelerOptions customImageLabelerOptions =
            new CustomImageLabelerOptions.Builder(localModel)
                    .build();
    ImageLabeler labeler = ImageLabeling.getClient(customImageLabelerOptions);

    InputImage image;

    public void init(Context context, int resultCode, Intent data) {
        labelMap.clear();
        labelMap.put(0, "Nike");
        labelMap.put(1, "Adidas");
        labelMap.put(2, "Puma");

        if (resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
        }

        if (selectedImage != null) {
            try {
                image = InputImage.fromFilePath(context, selectedImage);
                analyze();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void analyze() {
        if (selectedImage != null) {
            labeler.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                        @Override
                        public void onSuccess(List<ImageLabel> labels) {
                            float maxConfidence = 0;
                            int maxIndex = 0;

                            for (ImageLabel label : labels) {
                                float confidence = label.getConfidence();
                                int index = label.getIndex();
                                if (confidence > maxConfidence) {
                                    maxConfidence = confidence;
                                    maxIndex = index;
                                }
                                System.out.println(label);
                                System.out.println("[MLRES]: " + labelMap.get(index) + ": " + confidence);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString("category", "all");
                            bundle.putString("keyword", labelMap.get(maxIndex));
                            MainActivity.navController.navigate(R.id.action_global_searchFragment, bundle);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.context, "Cannot use machine learning feature", Toast.LENGTH_SHORT);
                        }
                    });
        }
    }
}
