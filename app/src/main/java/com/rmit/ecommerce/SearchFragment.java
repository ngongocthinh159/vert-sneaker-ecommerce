package com.rmit.ecommerce;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

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

        // Setup recycler view
        RecyclerView rVSearch = view.findViewById(R.id.rVSearch);

        String[] brand = {"NIKE", "PUMA", "NIKE", "NIKE", "NIKE", "PUMA", "NIKE", "NIKE", "NIKE",
        "PUMA", "NIKE", "NIKE", "NIKE", "PUMA", "NIKE", "NIKE"};
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(brand);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        rVSearch.setAdapter(myRecyclerViewAdapter);
        rVSearch.setLayoutManager(gridLayoutManager);

        // Setup back button
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.popBackStack();
            }
        });

        // Setup search bar
        TextInputEditText searchBar = view.findViewById(R.id.searchBar);
        searchBar.requestFocus();

        // Inflate the layout for this fragment
        return view;
    }
}