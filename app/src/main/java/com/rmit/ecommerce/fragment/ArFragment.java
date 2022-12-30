package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;

import org.checkerframework.checker.units.qual.A;

import io.github.sceneview.ar.ArSceneView;
import io.github.sceneview.ar.node.ArModelNode;
import io.github.sceneview.ar.node.PlacementMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ArFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArFragment extends Fragment {

    View view;
    public static ArSceneView sceneView;
    public static ArModelNode arModelNode;

    String MODEL_3D_SNEAKER = "https://drive.google.com/uc?export=download&id=1K2A3s2zhHMumMkDL8JHWxOmQSC-VLfTz";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ArFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArFragment newInstance(String param1, String param2) {
        ArFragment fragment = new ArFragment();
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
        view = inflater.inflate(R.layout.fragment_ar, container, false);


        sceneView = view.findViewById(R.id.sceneView);

        // Create new AR model
        arModelNode = new ArModelNode(PlacementMode.PLANE_HORIZONTAL,
                ArModelNode.Companion.getDEFAULT_HIT_POSITION(),
                true,
                 false);

        arModelNode.loadModelAsync(MainActivity.context,
                null,
                MODEL_3D_SNEAKER,
                true,
                0.25f,
                null,
                null,
                null);

        // Add model to AR view
        sceneView.addChild(arModelNode);

        // Back button setup
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sceneView.removeChild(arModelNode);
                MainActivity.navController.popBackStack();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}