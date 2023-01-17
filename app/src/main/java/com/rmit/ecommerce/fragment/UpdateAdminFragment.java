package com.rmit.ecommerce.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateAdminFragment extends Fragment {
    ImageSlider imageSlider;
    View loadingView;
    SneakerModel sneakerModel;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    boolean imageChanged = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UpdateAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateAdminFragment newInstance(String param1, String param2) {
        UpdateAdminFragment fragment = new UpdateAdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_update_admin, container, false);
        Button previousBtn = view.findViewById(R.id.previousBtn3);
        Button saveBtn = view.findViewById(R.id.publishBtn);
        Button deleteBtn = view.findViewById(R.id.deleteBtn);
        Button selectImagesBtn = view.findViewById(R.id.selectImageBtn);
        TextInputEditText title = view.findViewById(R.id.title);
        TextInputEditText brand = view.findViewById(R.id.brand);
        TextInputEditText price = view.findViewById(R.id.price);
        TextInputEditText description = view.findViewById(R.id.description);
        imageSlider = view.findViewById(R.id.imageSlider);
        loadingView = view.findViewById(R.id.loadingOverlay);

        fetchSneakerData(title, brand, price, description);

        previousBtn.setOnClickListener(v -> {
            MainActivity.navController.navigateUp();
        });

        saveBtn.setOnClickListener(v -> {
            if (formValidation(
                    title,
                    brand,
                    price,
                    description
            )) {
                handleSaveData(title, brand, price, description);
            }
        });

        deleteBtn.setOnClickListener(v -> {
            handleDeleteData();
        });

        selectImagesBtn.setOnClickListener(v -> {
            handlePhotoUpload();
        });


        return view;
    }

    private void handlePhotoUpload() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
        imageChanged = true;
    }

    private void handleDeleteData() {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("sneakers").document(MainActivity.adminCrudService.getInstance().getCurrentSneakerId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DELETE SNEAKER", "DocumentSnapshot successfully deleted!");
                        MainActivity.repositoryManager.setShouldFetch(true);
                        loadingView.setVisibility(View.GONE);
                        while (MainActivity.navController.getCurrentDestination().getId() != R.id.homeAdminFragment) MainActivity.navController.popBackStack();
                        Toast.makeText(MainActivity.context, "Deleted!", Toast.LENGTH_LONG).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DELETE SNEAKER", "Error deleting document", e);
                        loadingView.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.context, "Failed to delete", Toast.LENGTH_LONG).show();

                    }
                });

    }

    private boolean formValidation(TextInputEditText title, TextInputEditText brand, TextInputEditText price, TextInputEditText description) {
        if (title.getText().toString().equals("") ||
                brand.getText().toString().equals("") ||
                description.getText().toString().equals("")
        ) {
            fireValidationToast("You must fill all fields");
            return false;
        }

        try {
            Double.parseDouble(price.getText().toString());
        } catch (Exception e) {
            price.setError("Should be a decimal or integer");
            fireValidationToast("Price is not valid");
            return false;
        }

        if (MainActivity.adminCrudService.getInstance().getImagesEncodedList().size() == 0) {
            fireValidationToast("No image is selected");
            return false;
        }
        return true;
    }



    private void fireValidationToast(String message) {
        Toast.makeText(MainActivity.context, message, Toast.LENGTH_SHORT).show();
    }

    private void handleSaveData(TextInputEditText title, TextInputEditText brand, TextInputEditText price, TextInputEditText description) {
        loadingView.setVisibility(View.VISIBLE);
        sneakerModel.setTitle(title.getText().toString());
        sneakerModel.setBrand(brand.getText().toString());
        sneakerModel.setPrice(Double.valueOf(price.getText().toString()));
        sneakerModel.setDescription(description.getText().toString());
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("sneakers").document(MainActivity.adminCrudService.getInstance().getCurrentSneakerId())
                .set(sneakerModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingView.setVisibility(View.GONE);
                        Log.d("PUT SNEAKER", "DocumentSnapshot successfully written!");
                        MainActivity.repositoryManager.setShouldFetch(true);
                        Toast.makeText(MainActivity.context, "Sneaker updated!", Toast.LENGTH_LONG).show();
                        if (imageChanged) {
                            String[] splitRes = sneakerModel.getImage().split("/");
                            cleanUpOldImages(sneakerModel.getImage());
                            handleUploadFirebaseStorage(splitRes[splitRes.length - 1]);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingView.setVisibility(View.GONE);
                        Log.w("PUT SNEAKER", "Error writing document", e);
                        Toast.makeText(MainActivity.context, "Failed to updated!", Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void fetchSneakerData(TextInputEditText title, TextInputEditText brand, TextInputEditText price, TextInputEditText description) {
        loadingView.setVisibility(View.VISIBLE);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("sneakers").document(MainActivity.adminCrudService.getInstance().getCurrentSneakerId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        sneakerModel = documentSnapshot.toObject(SneakerModel.class);
                        title.setText(sneakerModel.getTitle());
                        brand.setText(sneakerModel.getBrand());
                        price.setText(Double.toString(sneakerModel.getPrice()));
                        description.setText(sneakerModel.getDescription());
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        storage.getReferenceFromUrl(sneakerModel.getImage()).listAll()
                                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        loadingView.setVisibility(View.GONE);
                                        if (listResult.getItems().isEmpty()) return;
                                        for (StorageReference storageReference : listResult.getItems()) {
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_CROP));
                                                    imageSlider.setImageList(slideModels);
                                                }
                                            });
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Uh-oh, an error occurred!
                                        loadingView.setVisibility(View.GONE);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.context, "Failed to fetch sneaker data", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleUploadFirebaseStorage(String folderName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");
        System.out.println(MainActivity.adminCrudService.getInstance().getImagesEncodedList());
        for (Uri uri : MainActivity.adminCrudService.getInstance().getImagesEncodedList()) {
            try {
                InputStream stream = MainActivity.context.getContentResolver().openInputStream(uri);
                byte[] targetArray = IOUtils.toByteArray(stream);
                UploadTask uploadTask = storageRef.child(folderName + "/" + UUID.randomUUID().toString()).putBytes(targetArray);
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
    }

    private void cleanUpOldImages(String url) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(url);
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference imageRef : listResult.getItems()) {
                            imageRef.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume");
        System.out.println(MainActivity.adminCrudService.getInstance().getImagesEncodedList());
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        for (Uri uri: MainActivity.adminCrudService.getInstance().getImagesEncodedList()) {
            slideModels.add(new SlideModel(uri.toString(), ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(slideModels);
    }
}