package com.rmit.ecommerce.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.MyRecyclerViewAdapter;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.SneakerModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    MaterialCardView sortPicker;
    TextInputEditText searchBar;
    MaterialCardView rangePicker;

    private static final int SORT_TYPE_LTH = 0;
    private static final int SORT_TYPE_HTL = 1;
    private static final int SORT_TYPE_ATZ = 2;
    private static final int SORT_TYPE_ZTA = 3;
    int sortType = SORT_TYPE_LTH;

    private static final double RANGE_MIN_VALUE = 0.0;
    private static final double RANGE_MAX_VALUE = 1000.0;
    private static final double RANGE_STEP = 50.0;
    private static final String[] LOCAL_COUNTRY = {"US", "$"};
    private double range_lower_bound = RANGE_MIN_VALUE;
    private double range_upper_bound = RANGE_MAX_VALUE;
    double[] range = {range_lower_bound, range_upper_bound};


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
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Setup recycler  view
        setupRecyclerView(view);

        // Setup search bar
        setupSearchBar(view);

        // Setup back button
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.popBackStack();
            }
        });

        // Setup sort button
        setupSortPicker(view);

        // Setup filter button
        setupRangePicker(view);

        // Inflate the layout for this fragment
        return view;
    }

    private void sort() {
        switch (sortType) {
            case SORT_TYPE_LTH:
                break;
            case SORT_TYPE_HTL:
                break;
            case SORT_TYPE_ATZ:
                break;
            case SORT_TYPE_ZTA:
                break;
            default:
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupRangePicker(View view) {
        // Price range text
        TextView tvPriceRange = view.findViewById(R.id.tvPriceRange);

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
                sort();

                tvPriceRange.setText("Price range: " + RANGE_MIN_VALUE + "$ - " +
                        RANGE_MAX_VALUE + "$");
            }
        });

        // Setup apply button
        Button btnApply = view.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rangePicker.setVisibility(View.GONE);
                sort();

                float lower_bound = rangeSlider.getValues().get(0);
                float upper_bound = rangeSlider.getValues().get(1);
                tvPriceRange.setText("Price range: " + (int) lower_bound + "$ - " +
                        (int) upper_bound + "$");
            }
        });
    }

    private void setupSortPicker(View view) {
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
                sort();

                tvSortType.setText("Sort: low to high");
            }
        });

        btnSortPriceHTL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_HTL;
                sort();

                tvSortType.setText("Sort: high to low");
            }
        });

        btnSortPriceATZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_ATZ;
                sort();

                tvSortType.setText("Sort: A to Z");
            }
        });

        btnSortPriceZTA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortPicker.setVisibility(View.GONE);
                sortType = SORT_TYPE_ZTA;
                sort();

                tvSortType.setText("Sort: Z to A");
            }
        });
    }

    private void setupSearchBar(View view) {
        // Request focus
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.requestFocus();

        // Setup end icon on click
        TextInputLayout textInputLayoutSearchBar = view.findViewById(R.id.textInputLayoutSearchBar);
        textInputLayoutSearchBar.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "abc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView(View view) {
        // Setup recycler view
        RecyclerView rVSearch = view.findViewById(R.id.rVSearch);

        ArrayList<SneakerModel> sneakers = MainActivity.repositoryManager.getSneakers();
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(sneakers, "search");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        rVSearch.setAdapter(myRecyclerViewAdapter);
        rVSearch.setLayoutManager(gridLayoutManager);

        // Setup visibility behaviour of sort picker and filter picker
        rVSearch.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int curState = recyclerView.getScrollState();
                if (curState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    sortPicker.setVisibility(View.GONE);
                    rangePicker.setVisibility(View.GONE);
                    searchBar.clearFocus();
                    Helper.hideKeyBoard(rVSearch);
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}