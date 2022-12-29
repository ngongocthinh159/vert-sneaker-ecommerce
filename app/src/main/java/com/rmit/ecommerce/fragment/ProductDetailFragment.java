package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailFragment extends Fragment {

    String product_image_demo = "https://w7.pngwing.com/pngs/869/483/png-transparent-nike-" +
            "free-air-force-1-sneakers-nike-air-max-nike-white-football-boot-outdoor-shoe.png";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductDetailFragment newInstance(String param1, String param2) {
        ProductDetailFragment fragment = new ProductDetailFragment();
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
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        setupImageSlider(view);

        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navController.popBackStack();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setupImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);

        ArrayList<SlideModel> slideModels = new ArrayList<>();

//        slideModels.add(new SlideModel(product_image_demo,  ScaleTypes.CENTER_CROP));
//        slideModels.add(new SlideModel(product_image_demo,  ScaleTypes.CENTER_CROP));
//        slideModels.add(new SlideModel(product_image_demo,  ScaleTypes.CENTER_CROP));
//        slideModels.add(new SlideModel(product_image_demo,  ScaleTypes.CENTER_CROP));
//        slideModels.add(new SlideModel(product_image_demo,  ScaleTypes.CENTER_CROP));

        slideModels.add(new SlideModel(R.drawable.af1_demo,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.af1_demo,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.af1_demo,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.af1_demo,  ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.af1_demo,  ScaleTypes.CENTER_CROP));

        imageSlider.setImageList(slideModels);
    }
}