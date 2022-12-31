package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.AdminRVAdapter;
import com.rmit.ecommerce.adapter.SizeRVAdapter;
import com.rmit.ecommerce.repository.SizeModel;
import com.rmit.ecommerce.repository.SneakerModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

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
        previousBtn.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.action_sizeAdminFragment_to_productManageFragment);
        });
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        // Setup recycler view
        RecyclerView sizeRv = view.findViewById(R.id.sizesRv);

        ArrayList<SizeModel> sizes = new ArrayList<>();
        // TODO: Replace mock data with real data
        for (int i = 0; i < 12; i++) {
            sizes.add(new SizeModel("42", 4));
        }
        SizeRVAdapter sizeRVAdapter = new SizeRVAdapter(sizes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false);
        sizeRv.setAdapter(sizeRVAdapter);
        sizeRv.setLayoutManager(linearLayoutManager);


    }
}