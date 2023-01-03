package com.rmit.ecommerce.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.repository.SneakerBase;
import com.rmit.ecommerce.repository.SneakerModel;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        Button prevBtn = view.findViewById(R.id.previousBtn3);
        Button selectImageBtn = view.findViewById(R.id.selectImageBtn);
        Button submitBtn = view.findViewById(R.id.addProductBtn);
        TextInputEditText title = view.findViewById(R.id.title);
        TextInputEditText brand = view.findViewById(R.id.brand);
        TextInputEditText price = view.findViewById(R.id.price);
        TextInputEditText description = view.findViewById(R.id.description);
        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
        });
        prevBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_addProductFragment_to_homeAdminFragment);
        });
        submitBtn.setOnClickListener(v -> {
            System.out.println(MainActivity.adminCrudService.getInstance().getImagesEncodedList());


            // TODO: ADD FORM VALIDATION

            // TODO: Add progress bar, after upload, notify success or fail, navigate to home admin, refetch db
            String folderName = title.getText().toString().replace(' ', '-');
            // Handle upload firebase storage
            handleUploadFirebaseStorage(folderName);
            // Handle upload firestore
            handleUploadFireStore(title.getText().toString(),
                    brand.getText().toString(),
                    "gs://vert-ecommerce.appspot.com/images/" + folderName,
                    description.getText().toString(),
                    Double.valueOf(price.getText().toString()));

        });
        return view;
    }

    private void handleUploadFirebaseStorage(String folderName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");
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
                        System.out.println("UPLOAD OK");
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUploadFireStore(String title, String brand, String image, String description, double price) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        SneakerBase sneakerModel = new SneakerBase(title, brand, image, description, price, new ArrayList<>());
        firestore.collection("sneakers").add(sneakerModel)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("FIRESTORE", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FIRESTORE", "Error adding document", e);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "Resume");
        System.out.println(MainActivity.adminCrudService.getInstance().getImagesEncodedList());

    }
}