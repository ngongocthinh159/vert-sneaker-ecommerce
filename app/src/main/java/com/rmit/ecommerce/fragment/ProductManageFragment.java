package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;

import kotlin.text.MatchNamedGroupCollection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductManageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductManageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductManageFragment newInstance(String param1, String param2) {
        ProductManageFragment fragment = new ProductManageFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_manage, container, false);

        Button updateDetailedButton = view.findViewById(R.id.updateDetailedBtn);
        Button sizeManagerBtn = view.findViewById(R.id.sizeManagerBtn);
        Button previousBtn = view.findViewById(R.id.previousBtn);
        Button categoryBtn = view.findViewById(R.id.categoryBtn);

        previousBtn.setOnClickListener(v -> {
            MainActivity.navController.navigateUp();
        });

        sizeManagerBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_productManageFragment_to_sizeAdminFragment);
        });

        updateDetailedButton.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_productManageFragment_to_updateAdminFragment);
        });

        categoryBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_productManageFragment_to_productCategoryManager);
        });

        return view;
    }
}