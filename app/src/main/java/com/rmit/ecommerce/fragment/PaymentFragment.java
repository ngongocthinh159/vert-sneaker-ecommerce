package com.rmit.ecommerce.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.CartModel;
import com.rmit.ecommerce.repository.RepositoryManager;
import com.rmit.ecommerce.repository.SneakerModel;
import com.rmit.ecommerce.repository.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {

    View view;
    double total_payment = -1.0;
    ProgressDialog pd;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
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
        view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Get refs
        MaterialButton btnBack = view.findViewById(R.id.btnBackPayment);
        MaterialButton btnPayment = view.findViewById(R.id.btnPayment);
        TextView total = view.findViewById(R.id.totalPayment);

        // Setup total money
        if (getArguments() != null) {
            String temp = getArguments().getString("total");
            total.setText("Total: $" + temp);
            total_payment = Double.parseDouble(temp);
        }

        // Setup back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.popBackStack();
            }
        });

        // Setup payment button
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.context);
                builder.setTitle("Confirm payment")
                        .setMessage("Do you want to proceed payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Show loading dialog
                                pd = new ProgressDialog(MainActivity.context);
                                pd.setMessage("loading");
                                pd.setCancelable(false);
                                pd.show();

                                // Check valid transaction
                                proceedTransaction();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void proceedTransaction() {
        // Update database
        // Create new cart
        UserModel user = MainActivity.repositoryManager.getUser();
        Map<String, Object> data = new HashMap<>();
        data.put("cartItemIds", new ArrayList<>());
        data.put("status", false);
        data.put("timestamp", Timestamp.now());
        data.put("purchaseDate", Timestamp.now());
        data.put("total", "");
        MainActivity.repositoryManager.getFireStore().
                collection("carts").
                add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String newCartId = documentReference.getId();
                        final int[] count = {0};
                        int pdDismissSize = MainActivity.repositoryManager.getCartItems().size() + 3;

                        // Update local and remote: current cart attributes
                        if (total_payment > 0) {
                            // Update local
                            MainActivity.repositoryManager.getCartObject().setTotal(String.valueOf(total_payment));
                            MainActivity.repositoryManager.getCartObject().setStatus(true);
                            MainActivity.repositoryManager.getCartObject().setPurchaseDate(Timestamp.now());
                            // Update remote
                            MainActivity.repositoryManager.getFireStore()
                                    .collection("carts")
                                    .document(user.getCurrentCartId())
                                    .update("total", String.valueOf(total_payment), "status", true, "purchaseDate", Timestamp.now())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            count[0]++;
                                            if (count[0] == pdDismissSize) {
                                                pd.dismiss();
                                                MainActivity.repositoryManager.getCartItems().clear();
                                                popBackStackUntilHome();
                                            }
                                        }
                                    });
                        }

                        // Update local and remote: user new cart id and cart history
                        // Update local
                        user.getHistoryCartIds().add(user.getCurrentCartId()); // Move current cart to cart history
                        user.setCurrentCartId(newCartId); // User new cart = newly created cart
                        // Update remote
                        MainActivity.repositoryManager.getFireStore() // Update user new cart and cart history
                                .collection("users")
                                .document(user.getId())
                                .update("historyCartIds", user.getHistoryCartIds(),
                                        "currentCartId", user.getCurrentCartId())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        count[0]++;
                                        if (count[0] == pdDismissSize) {
                                            pd.dismiss();
                                            MainActivity.repositoryManager.getCartItems().clear();
                                            popBackStackUntilHome();
                                        }
                                    }
                                });

                        // Update local and remote: decrease quantity
                        for (SneakerModel sneaker : MainActivity.repositoryManager.getSneakers()) {
                            for (CartItemModel item : MainActivity.repositoryManager.getCartItems()) {
                                if (sneaker.getId().equals(item.getPid().getId())) {
                                    // Update local
                                    int cur_quantity = sneaker.getSize().get(0).get(item.getSize() + "");
                                    sneaker.getSize().get(0).put(String.valueOf(item.getSize()), cur_quantity - item.getQuantity());

                                    // Update remote
                                    item.getPid().update("size", sneaker.getSize())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    count[0]++;
                                                    if (count[0] == pdDismissSize) {
                                                        pd.dismiss();
                                                        MainActivity.repositoryManager.getCartItems().clear();
                                                        popBackStackUntilHome();
                                                    }
                                                }
                                            });
                                }
                            }
                        }

                        // Update local cart
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    CartModel newCart = task.getResult().toObject(CartModel.class);
                                    MainActivity.repositoryManager.setCartObject(newCart);
                                }

                                count[0]++;
                                if (count[0] == pdDismissSize) {
                                    pd.dismiss();
                                    MainActivity.repositoryManager.getCartItems().clear();
                                    popBackStackUntilHome();
                                }
                            }
                        });
                    }
                });
    }

    private void popBackStackUntilHome() {
        while (MainActivity.navController.getCurrentDestination().getId() != R.id.homeFragment) MainActivity.navController.popBackStack();
        Toast.makeText(MainActivity.context, "Payment done!", Toast.LENGTH_SHORT).show();
    }

    private boolean isNotValidTransaction() {



        MainActivity.navController.navigateUp();
        return true;
    }
}