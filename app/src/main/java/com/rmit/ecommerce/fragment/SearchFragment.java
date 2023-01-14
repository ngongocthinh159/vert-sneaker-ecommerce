package com.rmit.ecommerce.fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.MyRecyclerViewAdapter;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.SneakerModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private View view;
    private RecyclerView rVSearch;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;

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

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        view = inflater.inflate(R.layout.fragment_search, container, false);

        // Setup recycler  view
        setupRecyclerView();

        // Setup search bar
        setupSearchBar();

        // Setup back button
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.popBackStack();
            }
        });

        // Setup sort button
        setupSortPicker();

        // Setup filter button
        setupRangePicker();

        // Trigger image search if machine learning is used
        handleImageSearch();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!ProductDetailFragment.product_is_available) {
            MainActivity.repositoryManager.fetchAllSneakers();
            ProductDetailFragment.product_is_available = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupRangePicker() {
        // Price range text
        TextView tvPriceRange = view.findViewById(R.id.tvPriceRange);
        tvPriceRange.setText("Price: " + (int) range_lower_bound + "$ - " +
                (int) range_upper_bound + "$");

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


        // Setup range text
        TextView tvRange = view.findViewById(R.id.tvRange);
        tvRange.setText(Helper.getFormattedAmount((int) RANGE_MIN_VALUE) + LOCAL_COUNTRY[1] + " - " +
                Helper.getFormattedAmount((int) RANGE_MAX_VALUE) + LOCAL_COUNTRY[1]);

        // Change range text on sliding
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                float lower_bound_float = slider.getValues().get(0);
                float upper_bound_float = slider.getValues().get(1);
                String lower_bound_string = Helper.getFormattedAmount((int) lower_bound_float);
                String upper_bound_string = Helper.getFormattedAmount((int) upper_bound_float);

                // Set range text
                String rangeText = lower_bound_string + LOCAL_COUNTRY[1] + " - " +
                        upper_bound_string + LOCAL_COUNTRY[1];
                tvRange.setText(rangeText);
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
                tvPriceRange.setText("Price: " + (int) range_lower_bound + "$ - " +
                        (int) range_upper_bound + "$");

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
                tvPriceRange.setText("Price: " + (int) range_lower_bound + "$ - " +
                        (int) range_upper_bound + "$");

                filterNow();
            }
        });
    }

    private void filterNow() {
        currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
        rVSearch.setAdapter(new MyRecyclerViewAdapter(currentList, "search"));
    }

    private void setupSortPicker() {
        // Setup sort button (hide/show sort dialog)
        Button btnSort = view.findViewById(R.id.btnSort);
        TextView tvSortType = view.findViewById(R.id.tvSortType);
        sortPicker = view.findViewById(R.id.sortPicker);
        btnSort.setOnClickListener(view1 -> {
            rangePicker.setVisibility(View.GONE);

            int visibility = sortPicker.getVisibility();
            if (visibility == View.GONE) {
                sortPicker.setVisibility(View.VISIBLE);
            } else sortPicker.setVisibility(View.GONE);
        });

        // Setup button inside sort dialog
        Button btnSortPriceLTH = view.findViewById(R.id.btnSortPriceLTH);
        Button btnSortPriceHTL = view.findViewById(R.id.btnSortPriceHTL);
        Button btnSortPriceATZ = view.findViewById(R.id.btnSortPriceATZ);
        Button btnSortPriceZTA = view.findViewById(R.id.btnSortPriceZTA);

        btnSortPriceLTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_LTH;

                tvSortType.setText("Sort: low to high");

                filterNow();
            }
        });

        btnSortPriceHTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_HTL;

                tvSortType.setText("Sort: high to low");

                filterNow();
            }
        });

        btnSortPriceATZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_ATZ;

                tvSortType.setText("Sort: A to Z");

                filterNow();
            }
        });

        btnSortPriceZTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_ZTA;

                tvSortType.setText("Sort: Z to A");

                filterNow();
            }
        });
    }

    private void setupSearchBar() {
        // Request focus
        etSearch = view.findViewById(R.id.searchBar);
        etSearch.requestFocus();
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

    private void handleImageSearch() {
        if (getArguments().getString("keyword") == null) {
            return;
        }

        String keyword = getArguments().getString("keyword");
        if (etSearch != null) {
            etSearch.setText(keyword);
        }
        searchString = keyword;
        filterNow();

    }

    private void setupRecyclerView() {
        if (getArguments() == null) return;

        // Setup recycler view
        rVSearch = view.findViewById(R.id.rVSearch);

        // Get search list
        String category = getArguments().getString("category");
        switch (category) {
            case "all":
                originalList = MainActivity.repositoryManager.getSneakers();
                currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
                break;
            case "bestseller":
                originalList = MainActivity.repositoryManager.getBestSellerSneakers();
                currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
                break;
            case "popular":
                originalList = MainActivity.repositoryManager.getPopularSneakers();
                currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
                break;
            case "newarrival":
                originalList = MainActivity.repositoryManager.getNewArrivalSneakers();
                currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
                break;
            default:
                originalList = MainActivity.repositoryManager.getSneakers();
                currentList = Helper.getFilterList(sortType, range_lower_bound, range_upper_bound, searchString, originalList);
                break;
        }

        // Adapter
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(currentList, "search");

        // Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        // Bind adapter and layout
        rVSearch.setAdapter(myRecyclerViewAdapter);
        rVSearch.setLayoutManager(gridLayoutManager);

        // Setup visibility behaviour of sort picker and filter picker
        rVSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int curState = recyclerView.getScrollState();
                if (curState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    sortPicker.setVisibility(View.GONE);
                    rangePicker.setVisibility(View.GONE);
                    etSearch.clearFocus();
                    Helper.hideKeyBoard(rVSearch);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public void fetchAllSneakers() {
        MainActivity.repositoryManager.getFireStore().collection("sneakers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            MainActivity.repositoryManager.getSneakers().clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> data = document.getData();
                                MainActivity.repositoryManager.getSneakers().add(document.toObject(SneakerModel.class));
                            }

                            getSneakersInCategory();
                        } else {
                            Log.d(TAG, "Error", task.getException());
                        }
                    }
                });
    }

    private void getSneakersInCategory() {
        MainActivity.repositoryManager.getBestSellerSneakers().clear();
        MainActivity.repositoryManager.getPopularSneakers().clear();
        MainActivity.repositoryManager.getNewArrivalSneakers().clear();
        MainActivity.repositoryManager.getTrendingSneakers().clear();
        for (SneakerModel sneaker : MainActivity.repositoryManager.getSneakers()) {
            if (sneaker.getCategory() == null) continue;
            if (sneaker.getCategory().contains("bestseller")) {
                MainActivity.repositoryManager.getBestSellerSneakers().add(sneaker);
            }

            if (sneaker.getCategory().contains("popular")) {
                MainActivity.repositoryManager.getPopularSneakers().add(sneaker);
            }

            if (sneaker.getCategory().contains("newarrival")) {
                MainActivity.repositoryManager.getNewArrivalSneakers().add(sneaker);
            }

            if (sneaker.getCategory().contains("trending")) {
                MainActivity.repositoryManager.getTrendingSneakers().add(sneaker);
            }
        }

        setupRecyclerView();
    }
}