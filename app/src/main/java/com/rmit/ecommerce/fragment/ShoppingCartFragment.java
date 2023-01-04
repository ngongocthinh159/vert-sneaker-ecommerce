package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.MyRecyclerViewAdapter2;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.CartModel;
import com.rmit.ecommerce.repository.SneakerModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingCartFragment extends Fragment {

    private MyRecyclerViewAdapter2 adapter2;
    TextView subtotal;
    TextView shippingFee;
    TextView total;
    private static final double SHIPPING_FEE = 10.00;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingCartFragment newInstance(String param1, String param2) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
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
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        // Get refs
        subtotal = view.findViewById(R.id.subtotal);
        shippingFee = view.findViewById(R.id.shippingFee);
        total = view.findViewById(R.id.total);

        // Setup recycler view
        RecyclerView rv = view.findViewById(R.id.rv);
        ArrayList<CartItemModel> cartItems = MainActivity.repositoryManager.getCartItems();
        adapter2 = new MyRecyclerViewAdapter2(cartItems);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);

        rv.setAdapter(adapter2);
        rv.setLayoutManager(linearLayoutManager);

        // Setup button
        MaterialButton btnCheckout = view.findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItems.size() == 0) {
                    Toast.makeText(MainActivity.context, "Please add somethings to the shopping cart!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("total", total.getText().toString().substring(1));
                MainActivity.navController.navigate(R.id.action_shoppingCartFragment_to_paymentFragment, bundle);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Fetch cart object (all cart item ids)
        // Then fetch all cart items
        fetchCartObject();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save cart item information (quantity change)
        for (CartItemModel cartItem : MainActivity.repositoryManager.getCartItems()) {
            MainActivity.repositoryManager.getFireStore().collection("cartItems").
                    document(cartItem.getId()).
                    update("quantity", cartItem.getQuantity()).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
    }

    private void fetchCartObject() {
        // Get cart information
        DocumentReference cartDoc = MainActivity.repositoryManager.getFireStore().
                collection("carts").
                document(MainActivity.repositoryManager.getUser().getCurrentCartId());
        cartDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    MainActivity.repositoryManager.setCartObject(document.toObject(CartModel.class));
                    fetchCartItems();
                } else {

                }
            }
        });
    }

    public void fetchCartItems() {
        MainActivity.repositoryManager.getCartItems().clear();
        ArrayList<String> cartItemIds = MainActivity.repositoryManager.
                getCartObject().
                getCartItemIds();
        CollectionReference collection = MainActivity.repositoryManager.
                getFireStore().
                collection("cartItems");
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (cartItemIds.contains(document.getId())) {
                            MainActivity.repositoryManager.getCartItems().add(document.toObject(CartItemModel.class));
                            if (adapter2 != null) adapter2.notifyDataSetChanged();
                            updatePrice();
                        }
                    }
                }
            }
        });
    }

    private void updatePrice() {
        // Get products (with quantity) inside cart list
        HashMap<SneakerModel, Integer> sneaker_quantity = new HashMap<>();
        for (SneakerModel sneaker : MainActivity.repositoryManager.getSneakers()) {
            for (CartItemModel item : adapter2.getCartItems()) {
                if (item.getPid().getId().equals(sneaker.getId())) {
                    sneaker_quantity.put(sneaker, item.getQuantity());
                }
            }
        }

        // Get price
        double subtotal_double = 0.0;
        double shippingFee_double = SHIPPING_FEE;
        double total_double;
        for (Map.Entry<SneakerModel, Integer> entry : sneaker_quantity.entrySet()) {
            int quantity = entry.getValue();
            double price = entry.getKey().getPrice();
            subtotal_double += (price * quantity);
        }
        total_double = subtotal_double + shippingFee_double;

        // Map data to view
        subtotal.setText("$" + String.valueOf(subtotal_double));
        shippingFee.setText("$" + String.valueOf(shippingFee_double));
        total.setText("$" + String.valueOf(total_double));
    }
}