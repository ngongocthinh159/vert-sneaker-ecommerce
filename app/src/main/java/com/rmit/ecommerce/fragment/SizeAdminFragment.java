package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.AdminRVAdapter;
import com.rmit.ecommerce.adapter.SizeRVAdapter;
import com.rmit.ecommerce.repository.SizeModel;
import com.rmit.ecommerce.repository.SneakerModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SizeAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SizeAdminFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SizeAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SizeAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SizeAdminFragment newInstance(String param1, String param2) {
        SizeAdminFragment fragment = new SizeAdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_size_admin, container, false);
        Button previousBtn = view.findViewById(R.id.previousBtn);
        TextInputLayout sizeInput = view.findViewById(R.id.textInputSize);
        Button addSizeBtn = view.findViewById(R.id.addSizeBtn);
        previousBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_sizeAdminFragment_to_productManageFragment);
        });

        addSizeBtn.setOnClickListener(v -> {
            String inputContent = sizeInput.getEditText().getText().toString();
            sizeInput.getEditText().setText("");
            // TODO: Add form validation is integer?
            if (!inputContent.isEmpty()) {
                FirebaseFirestore fs = FirebaseFirestore.getInstance();
                ArrayList<HashMap<String, Integer>> data = new ArrayList<>();
                RecyclerView sizesRv = view.findViewById(R.id.sizesRv);
                SizeRVAdapter sizeRVAdapter = (SizeRVAdapter) sizesRv.getAdapter();
                sizeRVAdapter.getSizes().add(new SizeModel(inputContent, 0));
                HashMap<String, Integer> sizes = new HashMap<>();

                for (SizeModel s : ((SizeRVAdapter) sizesRv.getAdapter()).getSizes()) {
                    sizes.put(s.getSizeLabel(), s.getQuantity());
                }

                data.add(sizes);

                fs.collection("sneakers").document(MainActivity.adminCrudService.getInstance().getCurrentSneakerId())
                        .update("size", data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("UPDATE", "DocumentSnapshot successfully updated!");

                                setupRecyclerView(view);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("UPDATE", "Error updating document", e);
                            }
                        });
            } else {
                Toast.makeText(MainActivity.context, "Invalid input", Toast.LENGTH_LONG);
            }
        });
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        // Setup recycler view
        RecyclerView sizeRv = view.findViewById(R.id.sizesRv);
        SizeRVAdapter emptyRVAdapter = new SizeRVAdapter();
        LinearLayoutManager emptyLayoutManager = new LinearLayoutManager(view.getContext());
        sizeRv.setAdapter(emptyRVAdapter);
        sizeRv.setLayoutManager(emptyLayoutManager);

        // Handle firebase41
        ArrayList<SizeModel> sizes = new ArrayList<>();
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        String currentSneakerId = MainActivity.adminCrudService.getInstance().getCurrentSneakerId();
        if (currentSneakerId != null) {
            fs.collection("sneakers").document(currentSneakerId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("DATA", "DocumentSnapshot data: " + document.getData());
                            ArrayList<SizeModel> sizeModels = new ArrayList<>();
                            SneakerModel sneakerModel = document.toObject(SneakerModel.class);
                            if (sneakerModel.getSize().size() > 0) {
                                for (Map.Entry<String, Integer> set : sneakerModel.getSize().get(0).entrySet()) {
                                    sizeModels.add(new SizeModel(set.getKey(), set.getValue()));
                                }
                            }
                            SizeRVAdapter sizeRVAdapter = new SizeRVAdapter(sizeModels);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
                            sizeRv.setAdapter(sizeRVAdapter);
                            sizeRv.setLayoutManager(linearLayoutManager);
                        } else {
                            Log.d("DATA", "No such document");
                        }
                    } else {
                        Log.d("DATA", "get failed with ", task.getException());
                    }
                }
            });
        }



    }
}