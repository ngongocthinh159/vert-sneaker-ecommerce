package com.rmit.ecommerce.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.repository.SneakerModel;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductCategoryManager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductCategoryManager extends Fragment {
    ImageView previewImage;
    boolean hasTrendingPhoto = false;
    int sneakerIdx = -1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductCategoryManager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductCategoryManager.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductCategoryManager newInstance(String param1, String param2) {
        ProductCategoryManager fragment = new ProductCategoryManager();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_category_manager, container, false);

        // Find sneaker idx
        findSneakerIdx();

        // Init checkbox state
        initCheckboxState(view);

        // Render trending image (if applicable)



        previewImage = view.findViewById(R.id.previewImageView);
        Button prevBtn = view.findViewById(R.id.previousBtn);
        prevBtn.setOnClickListener(v -> {
            MainActivity.navController.navigateUp();
        });

        Button saveBtn = view.findViewById(R.id.publishBtn);

        saveBtn.setOnClickListener(v -> {
            handleSaveChanges(view);
        });

        Button selectTrendingPhoto = view.findViewById(R.id.selectImageBtn);
        selectTrendingPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            getActivity().startActivityForResult(intent, MainActivity.adminCrudService.ADMIN_RQ_SINGLE);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
            Uri uri = MainActivity.adminCrudService.getInstance().getSelectedImage();
            if (uri != null && previewImage != null) {
                try {
                    InputStream inputStream = MainActivity.context.getContentResolver().openInputStream(uri);
                    Drawable drawable = Drawable.createFromStream(inputStream, uri.toString() );
                    previewImage.setImageDrawable(drawable);
                    hasTrendingPhoto = true;
                } catch (FileNotFoundException e) {
                }
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.adminCrudService.getInstance().setSelectedImage(null);
    }

    private void findSneakerIdx() {
        ArrayList<SneakerModel> sneakers = MainActivity.repositoryManager.getSneakers();
        String id = MainActivity.adminCrudService.getInstance().getCurrentSneakerId();
        if (id != null) {
            for (int i = 0; i < sneakers.size(); i++) {
                if (Objects.equals(sneakers.get(i).getId(), id)) {
                    sneakerIdx = i;
                    break;
                }
            }
        }

    }

    private void initCheckboxState(View view) {
        CheckBox cbTrending = view.findViewById(R.id.cbTrending);
        CheckBox cbNewArrival = view.findViewById(R.id.cbNewArrival);
        CheckBox cbBestSeller = view.findViewById(R.id.cbBestSeller);
        CheckBox cbPopular = view.findViewById(R.id.cbPopular);
        String id = MainActivity.adminCrudService.getInstance().getCurrentSneakerId();
        if (id != null) {
            ArrayList<SneakerModel> sneakers = MainActivity.repositoryManager.getSneakers();
            for (int i = 0; i < sneakers.size(); i++) {
                if (sneakers.get(i).getId() == id && sneakers.get(i).getCategory() != null) {
                    for (String category: sneakers.get(i).getCategory()) {
                        if (category.equals("trending")) {
                            cbTrending.setChecked(true);
                            hasTrendingPhoto = true;
                            fetchTrendingImage(view);
                        }
                        if (category.equals("newarrival")) {
                            cbNewArrival.setChecked(true);
                        }
                        if (category.equals("bestseller")) {
                            cbBestSeller.setChecked(true);
                        }
                        if (category.equals("popular")) {
                            cbPopular.setChecked(true);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void fetchTrendingImage(View view) {
        previewImage = view.findViewById(R.id.previewImageView);

    }



    private void handleSaveChanges(View view) {
        CheckBox cbTrending = view.findViewById(R.id.cbTrending);
        CheckBox cbNewArrival = view.findViewById(R.id.cbNewArrival);
        CheckBox cbBestSeller = view.findViewById(R.id.cbBestSeller);
        CheckBox cbPopular = view.findViewById(R.id.cbPopular);
        ArrayList<String> categories = new ArrayList<>();
        boolean allowProcess = true;
        if (cbTrending.isChecked()) {
            if (hasTrendingPhoto) {
                categories.add("trending");
            } else {
                Toast.makeText(MainActivity.context, "Must select 1 photo for trending", Toast.LENGTH_LONG).show();
                allowProcess = false;
            }
        }
        if (cbNewArrival.isChecked()) {
            categories.add("newarrival");
        }
        if (cbBestSeller.isChecked()) {
            categories.add("bestseller");
        }
        if (cbPopular.isChecked()) {
            categories.add("popular");
        }

        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String id = MainActivity.adminCrudService.getInstance().getCurrentSneakerId();

        if (id != null && allowProcess) {
            ArrayList<SneakerModel> sneakers = MainActivity.repositoryManager.getSneakers();
            SneakerModel currentSneakerModel = null;
            for (int i = 0; i < sneakers.size(); i++) {
                if (sneakers.get(i).getId() == id) {
                    currentSneakerModel = sneakers.get(i);
                    break;
                }
            }
            if (currentSneakerModel != null) {
                currentSneakerModel.setCategory(categories);
                fs.collection("sneakers").document(id)
                        .update("category", categories)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.repositoryManager.setShouldFetch(true);
                                if (cbTrending.isChecked()) {
                                    cleanUpOldImagesAndUploadNewImage(sneakers.get(sneakerIdx).getImage() + "/trending");
                                }
                                Toast.makeText(MainActivity.context, "Sneaker updated!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("PUT SNEAKER", "Error writing document", e);
                                Toast.makeText(MainActivity.context, "Failed to updated!", Toast.LENGTH_LONG).show();
                            }
                        });
                
            }
        } else {
            if (id != null) {
                cbTrending.isChecked();
            }
        }
    }

    private void handleUploadFirebaseStorage(String folderName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(folderName);
        try {
            Uri uri = MainActivity.adminCrudService.getInstance().getSelectedImage();
            InputStream stream = MainActivity.context.getContentResolver().openInputStream(uri);
            byte[] targetArray = IOUtils.toByteArray(stream);
            UploadTask uploadTask = storageRef.child(UUID.randomUUID().toString()).putBytes(targetArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    System.out.println("FAILED TO UPLOAD");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println("UPLOAD OK???????????????fjsdklfjdsklfdjs");

                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void cleanUpOldImagesAndUploadNewImage(String folderName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(folderName);
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        System.out.println("UH OH I HAVE TO FETCH AGAIN");
                        handleUploadFirebaseStorage(folderName);

                        for (StorageReference imageRef : listResult.getItems()) {
                            imageRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // File deleted successfully
                                            System.out.println("please run");
                                            Log.d("DELETE IMG", "Delete img successfully");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.d("DELETE IMG", "Failed to delete image");
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }
}