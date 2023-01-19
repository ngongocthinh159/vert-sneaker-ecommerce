package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.OrderDetailRVAdapter;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.CartModel;
import com.rmit.ecommerce.repository.SneakerModel;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends Fragment {

    private View view;
    private TextView time;
    private TextView total;
    private TextView id;
    private MaterialButton btnBack;
    private RecyclerView rv;
    private String orderId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
        view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        // Get refs
        rv = view.findViewById(R.id.rvOrderDetail);
        time = view.findViewById(R.id.timeOderDetail);
        total = view.findViewById(R.id.totalOderDetail);
        id = view.findViewById(R.id.idOderDetail);
        btnBack = view.findViewById(R.id.btnBackOrderDetail);

        // Setup back button action
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.navigateUp();
            }
        });

        // Setup empty recycler view
        setupEmptyRecyclerView();

        // Fetch data => Then setup recycler view again
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getString("orderId");

            fetch();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void setupEmptyRecyclerView() {
        OrderDetailRVAdapter adapter = new OrderDetailRVAdapter(new ArrayList<>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv.setAdapter(adapter);
        rv.setLayoutManager(linearLayoutManager);
    }

    private void setupRecyclerView(ArrayList<CartItemModel> items) {
        OrderDetailRVAdapter adapter = new OrderDetailRVAdapter(items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv.setAdapter(adapter);
        rv.setLayoutManager(linearLayoutManager);
    }

    private void fetchCartItems(ArrayList<String> ids) {
        MainActivity.repositoryManager.getFireStore().collection("cartItems")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<CartItemModel> items = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (ids.contains(document.getId())) {
                                    CartItemModel item = document.toObject(CartItemModel.class);
                                    items.add(item);
                                }
                            }
                            setupRecyclerView(items);
                        }
                    }
                });
    }

    private void fetchCartObject() {
        MainActivity.repositoryManager.getFireStore().collection("carts")
                .document(orderId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            CartModel cart = task.getResult().toObject(CartModel.class);
                            time.setText("Purchased time: " + getFormattedDate(cart));
                            total.setText("Total payment: $" + cart.getTotal().toString());
                            id.setText("Order ID: " + cart.getId());

                            if (cart != null && cart.getCartItemIds() != null && !cart.getCartItemIds().isEmpty()) {
                                fetchCartItems(cart.getCartItemIds());
                            }
                        }
                    }
                });
    }

    private void fetch() {
        fetchCartObject();
    }

    private String getFormattedDate(CartModel cart) {
        Date date = cart.getPurchaseDate().toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }
}