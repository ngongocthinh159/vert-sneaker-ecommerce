package com.rmit.ecommerce.fragment;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.AdminRVAdapter;
import com.rmit.ecommerce.adapter.MyRecyclerViewAdapter;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.SneakerModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeAdminFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAdminFragment extends Fragment {

    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeAdminFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeAdminFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAdminFragment newInstance(String param1, String param2) {
        HomeAdminFragment fragment = new HomeAdminFragment();
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
        view = inflater.inflate(R.layout.fragment_home_admin, container, false);
        setupRecyclerView(view);

        Button addBtn = view.findViewById(R.id.addBtn);
        Button sortBtn = view.findViewById(R.id.btnSort);
        Button logoutBtn = view.findViewById(R.id.logOutBtn);
        Button filterBtn = view.findViewById(R.id.btnFilter);
        Button notiBtn = view.findViewById(R.id.addNoti);
        View sortPicker = view.findViewById(R.id.sortPicker);
        View rangePicker = view.findViewById(R.id.rangePicker);

        logoutBtn.setOnClickListener(v -> {
            Dialog dialog = createDialog();
            dialog.show();
        });

        notiBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_homeAdminFragment_to_notificationManager);
        });

        addBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_homeAdminFragment_to_addProductFragment);
        });

        sortBtn.setOnClickListener(v -> {
            if (rangePicker.getVisibility() == View.VISIBLE) { rangePicker.setVisibility(View.GONE); }
            if (sortPicker.getVisibility() == View.VISIBLE) {
                sortPicker.setVisibility(View.GONE);
            } else {
                sortPicker.setVisibility(View.VISIBLE);
            }
        });

        filterBtn.setOnClickListener(v -> {
            if (sortPicker.getVisibility() == View.VISIBLE) { sortPicker.setVisibility(View.GONE); }
            if (rangePicker.getVisibility() == View.VISIBLE) {
                rangePicker.setVisibility(View.GONE);
            } else {
                rangePicker.setVisibility(View.VISIBLE);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private Dialog createDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You are about logging out")
                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.userManager.getAuth().signOut();

                        // Redirect to getting started page
                        Helper.popBackStackAll();
                        MainActivity.navController.navigate(R.id.gettingStartedFragment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void setupRecyclerView(View view) {
        // Setup recycler view, init will no data
        RecyclerView rVSearch = view.findViewById(R.id.rVSearch);
        AdminRVAdapter emptyAdapter = new AdminRVAdapter();
        GridLayoutManager emptyGridLayoutManager = new GridLayoutManager(getContext(), 2);
        rVSearch.setAdapter(emptyAdapter);
        rVSearch.setLayoutManager(emptyGridLayoutManager);

        // If there are no data
        if (MainActivity.repositoryManager.getShouldFetch()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("sneakers")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<SneakerModel> sneakers = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> data = document.getData();
                                    sneakers.add(document.toObject(SneakerModel.class));
                                }

                                // Cache
                                MainActivity.repositoryManager.setShouldFetch(false);
                                MainActivity.repositoryManager.setSneakers(sneakers);

                                // Handle RecyclerView
                                AdminRVAdapter adminRVAdapter = new AdminRVAdapter(sneakers);
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
                                rVSearch.setAdapter(adminRVAdapter);
                                rVSearch.setLayoutManager(gridLayoutManager);
                            } else {
                                Log.d(TAG, "Error", task.getException());
                            }
                        }
                    });
        } else {
            // Reuse data
            AdminRVAdapter adminRVAdapter = new AdminRVAdapter(MainActivity.repositoryManager.getSneakers());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            rVSearch.setAdapter(adminRVAdapter);
            rVSearch.setLayoutManager(gridLayoutManager);
        }

    }
}