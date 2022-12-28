package com.rmit.ecommerce;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    int count = 0;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // If user not log in => Transition to getting started
        if (!MainActivity.isLoggedIn) {
            MainActivity.navController.navigate(R.id.action_homeFragment_to_gettingStartedFragment);
        }

        // Setup recycle view
        setupRecyclerView(view);



        // Setup search bar
        TextInputEditText searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    MainActivity.navController.navigate(R.id.action_global_searchFragment);
                }
            }
        });
//        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
//                .addSharedElement(searchBar, "secondTrans")
//                .build();

        // Setup image slider
        setupImageSlider(view);

        // Setup see all buttons
        setupSeeAllButton(view);

        return view;
    }

    private void setupSeeAllButton(View view) {
        Button seeAllBestSeller = view.findViewById(R.id.seeAllBestSeller);
        Button seeAllPopular = view.findViewById(R.id.seeAllPopular);
        Button seeAllNewArrival = view.findViewById(R.id.seeAllNewArrival);

        seeAllBestSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_global_searchFragment);
            }
        });

        seeAllPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_global_searchFragment);
            }
        });

        seeAllNewArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.navigate(R.id.action_global_searchFragment);
            }
        });
    }

    private void setupImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel("https://cdn.vortexs.io/api/images/3cda9c91-d69c-493b-b0c9-4af8b958d220/1920/w/giay-nike-air-force-1-low-next-nature-white-university-blue-dn1430-100.jpeg",  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://cdn.vortexs.io/api/images/3cda9c91-d69c-493b-b0c9-4af8b958d220/1920/w/giay-nike-air-force-1-low-next-nature-white-university-blue-dn1430-100.jpeg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://cdn.vortexs.io/api/images/3cda9c91-d69c-493b-b0c9-4af8b958d220/1920/w/giay-nike-air-force-1-low-next-nature-white-university-blue-dn1430-100.jpeg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://cdn.vortexs.io/api/images/3cda9c91-d69c-493b-b0c9-4af8b958d220/1920/w/giay-nike-air-force-1-low-next-nature-white-university-blue-dn1430-100.jpeg", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel("https://cdn.vortexs.io/api/images/3cda9c91-d69c-493b-b0c9-4af8b958d220/1920/w/giay-nike-air-force-1-low-next-nature-white-university-blue-dn1430-100.jpeg", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                MainActivity.navController.navigate(R.id.action_global_productDetailFragment);
            }
        });
    }


    private void setupRecyclerView(View view) {
        RecyclerView rV1 = view.findViewById(R.id.rV1);
        RecyclerView rV2 = view.findViewById(R.id.rV2);
        RecyclerView rV3 = view.findViewById(R.id.rV3);
        String[] brand = {"NIKE", "PUMA", "NIKE", "NIKE"};
        MyRecyclerViewAdapter myRecyclerViewAdapter1 = new MyRecyclerViewAdapter(brand);
        MyRecyclerViewAdapter myRecyclerViewAdapter2 = new MyRecyclerViewAdapter(brand);
        MyRecyclerViewAdapter myRecyclerViewAdapter3 = new MyRecyclerViewAdapter(brand);

        LinearLayoutManager layoutManager1
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        rV1.setAdapter(myRecyclerViewAdapter1);
        rV1.setLayoutManager(layoutManager1);

        rV2.setAdapter(myRecyclerViewAdapter2);
        rV2.setLayoutManager(layoutManager2);

        rV3.setAdapter(myRecyclerViewAdapter3);
        rV3.setLayoutManager(layoutManager3);
    }
}