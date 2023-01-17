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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.AdminRVAdapter;
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
    MaterialButton filterBtn;

    private MaterialCardView sortPicker;
    private TextInputEditText etSearch;
    private MaterialCardView rangePicker;

    public static final int SORT_TYPE_LTH = 0;
    public static final int SORT_TYPE_HTL = 1;
    public static final int SORT_TYPE_ATZ = 2;
    public static final int SORT_TYPE_ZTA = 3;
    int sortType = SORT_TYPE_ATZ;

    private static final double RANGE_MIN_VALUE = 0.0;
    private static final double RANGE_MAX_VALUE = 10000.0;
    private static final double RANGE_STEP = 50.0;
    private static final String[] LOCAL_COUNTRY = {"US", "$"};
    private double range_lower_bound = RANGE_MIN_VALUE;
    private double range_upper_bound = RANGE_MAX_VALUE;
    private ArrayList<SneakerModel> originalList = new ArrayList<>();
    private ArrayList<SneakerModel> currentList = new ArrayList<>();
    private String searchString = "";

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
        setupButton();
        setupRangePicker();
        setupSearchBar();

        Button addBtn = view.findViewById(R.id.addBtn);
        Button sortBtn = view.findViewById(R.id.btnSort);
        Button logoutBtn = view.findViewById(R.id.logOutBtn);
        filterBtn = view.findViewById(R.id.btnFilter);
        Button notiBtn = view.findViewById(R.id.addNoti);

        sortPicker = view.findViewById(R.id.sortPicker);
        rangePicker = view.findViewById(R.id.rangePicker);

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

        // Inflate the layout for this fragment
        return view;
    }

    private void setupSearchBar() {
        // Request focus
        etSearch = view.findViewById(R.id.adminSearchBar);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchString = etSearch.getText().toString();
                filterNow();
            }
        });
    }

    private void setupRangePicker() {
        // Setup range slider
        RangeSlider rangeSlider = view.findViewById(R.id.rangeSlider);
        rangeSlider.setValueFrom((float) RANGE_MIN_VALUE);
        rangeSlider.setValueTo((float) RANGE_MAX_VALUE);
        rangeSlider.setStepSize((float) RANGE_STEP);
        rangeSlider.setValues((float) RANGE_MIN_VALUE, (float) RANGE_MAX_VALUE);
        rangeSlider.setLabelFormatter(new LabelFormatter() { // Change label formatter
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                return Helper.getFormattedAmount((int) value);
            }
        });

        // Setup filter button
        Button btnFilter = view.findViewById(R.id.btnFilter);
        rangePicker = view.findViewById(R.id.rangePicker);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);

                int visibility = rangePicker.getVisibility();
                if (visibility == View.GONE) {
                    rangePicker.setVisibility(View.VISIBLE);
                } else rangePicker.setVisibility(View.GONE);
            }
        });

        // Setup reset button
        Button btnReset = view.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rangeSlider.setValues((float) RANGE_MIN_VALUE, (float) RANGE_MAX_VALUE);
                rangePicker.setVisibility(View.GONE);

                range_lower_bound = rangeSlider.getValues().get(0);
                range_upper_bound = rangeSlider.getValues().get(1);

                filterNow();
            }
        });

        // Setup apply button
        Button btnApply = view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rangePicker.setVisibility(View.GONE);

                range_lower_bound = rangeSlider.getValues().get(0);
                range_upper_bound = rangeSlider.getValues().get(1);

                filterNow();
            }
        });
    }

    private void setupButton() {
        Button btnSortPriceLTH = view.findViewById(R.id.btnSortPriceLTH);
        Button btnSortPriceHTL = view.findViewById(R.id.btnSortPriceHTL);
        Button btnSortPriceATZ = view.findViewById(R.id.btnSortPriceATZ);
        Button btnSortPriceZTA = view.findViewById(R.id.btnSortPriceZTA);

        btnSortPriceLTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_LTH;

                filterNow();
            }
        });

        btnSortPriceHTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_HTL;

                filterNow();
            }
        });

        btnSortPriceATZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_ATZ;

                filterNow();
            }
        });

        btnSortPriceZTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_ZTA;

                filterNow();
            }
        });
    }

    private void filterNow() {
        currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
        RecyclerView rVSearch = view.findViewById(R.id.rVSearch);
        rVSearch.setAdapter(new AdminRVAdapter(currentList));
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

                                originalList = MainActivity.repositoryManager.getSneakers();
                                currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);

                                // Handle RecyclerView
                                AdminRVAdapter adminRVAdapter = new AdminRVAdapter(currentList);
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
            AdminRVAdapter adminRVAdapter = new AdminRVAdapter(currentList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            rVSearch.setAdapter(adminRVAdapter);
            rVSearch.setLayoutManager(gridLayoutManager);
        }

    }
}