package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.OrderHistoryAdapter;
import com.rmit.ecommerce.repository.CartModel;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistoryFragment extends Fragment {

    private View view;
    private MaterialButton btnBack;
    private ProgressBar progressBarOrderHistory;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
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
        view = inflater.inflate(R.layout.fragment_order_history, container, false);

        progressBarOrderHistory = view.findViewById(R.id.progressBarOrderHistory);

        // Setup back button
        btnBack = view.findViewById(R.id.btnBackOrderHistory);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.navigateUp();
            }
        });

        // Fetch information
        fetchOrderHistory();

        // Inflate the layout for this fragment
        return view;
    }

    private void setupRecyclerView(ArrayList<CartModel> orderHistory) {
        progressBarOrderHistory.setVisibility(View.GONE);

        // Sort
        orderHistory.sort(new Comparator<CartModel>() {
            @Override
            public int compare(CartModel o1, CartModel o2) {
                return o2.getPurchaseDate().compareTo(o1.getPurchaseDate());
            }
        });

        // Setup recycler view
        RecyclerView rv = view.findViewById(R.id.rvOrderHistory);
        OrderHistoryAdapter adapter = new OrderHistoryAdapter(orderHistory);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(MainActivity.context, LinearLayoutManager.VERTICAL, false);

        rv.setAdapter(adapter);
        rv.setLayoutManager(layoutManager2);
    }

    private void fetchOrderHistory() {
        ArrayList<String> cartIds = MainActivity.repositoryManager.getUser().getHistoryCartIds();
        ArrayList<CartModel> orderHistory = new ArrayList<>();
        final int[] count = {cartIds.size()};
        for (int i = 0; i < cartIds.size(); i++) {
            MainActivity.repositoryManager.getFireStore().collection("carts")
                    .document(cartIds.get(i)).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                CartModel cart = task.getResult().toObject(CartModel.class);
                                orderHistory.add(cart);
                                count[0]--;
                                if (count[0] == 0) {
                                    setupRecyclerView(orderHistory);
                                }
                            }
                        }
                    });
        }
        if (cartIds.size() == 0) progressBarOrderHistory.setVisibility(View.GONE);
    }
}