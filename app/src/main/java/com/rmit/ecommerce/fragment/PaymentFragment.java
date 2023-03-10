package com.rmit.ecommerce.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.CartModel;
import com.rmit.ecommerce.repository.SneakerModel;
import com.rmit.ecommerce.repository.UserModel;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {

    MaterialButton btnBack;
    MaterialButton btnPayment;

    // KEYS
    String SECRET_KEY = "sk_test_51MNr5AL5lHyfXOrhD2cgBqpzbmpNbVzNt0YbV95XrqptNfT64Qxqn1DLWpRJQNGeKeZ9JdNJjJ3qGtriAqpuljuU00e8gaB5ZT";
    String PUBLISH_KEY = "pk_test_51MNr5AL5lHyfXOrhTe0MXdiw33twWQyZvzmskpbJvYIxBjdIQV26bPrGPyPhzWTrLL4l1Z5mtQHY6avZJBHD7xJE00o9tnlPGJ";

    PaymentSheet paymentSheet;

    String customerID;
    String EphericalKey;
    String ClientSecret;

    String amount = "2481";
    String currency = "usd";
    boolean payed = false;

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

        // Map text data
        mapTextData();

        // Show loading dialog for initialize KEYs in stripe
        pd = new ProgressDialog(MainActivity.context);
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        // Get refs
        btnBack = view.findViewById(R.id.btnBackPayment);
        btnPayment = view.findViewById(R.id.btnPayment);
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
                if (!isValidUserInformation()) {
                    Toast.makeText(MainActivity.context, "Personal information is not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.context);
                builder.setTitle("Confirm payment")
                        .setMessage("Do you want to proceed payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnPayment.setActivated(false);
                                btnBack.setActivated(false);

                                // Stripe
                                PaymentFlow();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize stripe
        initializeStripe();
    }

    private boolean isValidUserInformation() {
        TextView tvUserPhone = view.findViewById(R.id.tvUserPhone);
        TextView tvUserAddress = view.findViewById(R.id.tvUserAddress);
        TextView tvUserCardNum = view.findViewById(R.id.tvUserCardNum);

        if (tvUserPhone.getText().toString().equals("Add your phone number")) {
            return false;
        }

        if (tvUserAddress.getText().toString().equals("Add your address")) {
            return false;
        }

        if (tvUserCardNum.getText().toString().equals("Add your card number")) {
            return false;
        }

        return true;
    }

    private void mapTextData() {
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);
        TextView tvUserPhone = view.findViewById(R.id.tvUserPhone);
        TextView tvUserAddress = view.findViewById(R.id.tvUserAddress);
        TextView tvUserCardNum = view.findViewById(R.id.tvUserCardNum);

        // Phone
        if (MainActivity.repositoryManager.getUser().getPhone() != null
                && !MainActivity.repositoryManager.getUser().getPhone().isEmpty()) {
            tvUserPhone.setText(MainActivity.repositoryManager.getUser().getPhone().substring(0, 3) + "-" + MainActivity.repositoryManager.getUser().getPhone().substring(3));
        }

        // Email
        tvUserEmail.setText(MainActivity.userManager.getUser().getEmail());

        // Address
        if (MainActivity.repositoryManager.getUser().getAddress() != null
                && !MainActivity.repositoryManager.getUser().getAddress().isEmpty()) {
            tvUserAddress.setText(MainActivity.repositoryManager.getUser().getAddress());
        }

        System.out.println("1111111111111111111111111111111111111");

        // Card number
        if (MainActivity.repositoryManager.getUser().getCardNumber() != null
                && !MainActivity.repositoryManager.getUser().getCardNumber().isEmpty()) {
            String temp = MainActivity.repositoryManager.getUser().getCardNumber();
            String cardNum_formatted = temp.substring(0, 4) + " " + temp.substring(4, 8) + " " + temp.substring(8, 12) + " " + temp.substring(12, 16);
            tvUserCardNum.setText(cardNum_formatted);
        }
    }

    private void initializeStripe(){
        PaymentConfiguration.init(MainActivity.context, PUBLISH_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
                            getEphericalKey(customerID);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }} ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error ){
                Log.w("error in response", "Error: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.context);
        requestQueue.add(stringRequest);
    }

    private void getEphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("secret");
                            getClientSecret(customerID,EphericalKey);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }} ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error ){
                Log.w("2 error in response", "Error: " + error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Stripe-Version","2022-11-15");
                header.put("Authorization","Bearer " + SECRET_KEY);

                return header;
            }


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.context);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephericalKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            pd.dismiss();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }} ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error ){
                Log.w("3 error in response", "Error: " + error.getMessage());
                pd.dismiss();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }

            // assigned payment detail
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "248100");
                params.put("currency", currency);
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.context);
        requestQueue.add(stringRequest);
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        String status;
        String message;
        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            // Show loading dialog
            pd = new ProgressDialog(MainActivity.context);
            pd.setMessage("loading");
            pd.setCancelable(false);
            pd.show();

            payed = true;
            proceedFirebaseTransaction();
        }
        else {
            Toast.makeText(MainActivity.context, "Not Success", Toast.LENGTH_SHORT).show();
            payed = false;
            status = "Payment Failed";
            message = "Your transaction is not completed";
            Dialog dialog = createDialog(status,message);
            dialog.show();
            btnPayment.setActivated(false);
            btnBack.setActivated(false);
        }
    }

    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(
                ClientSecret,new PaymentSheet.Configuration("EET Online Market",
                        new PaymentSheet.CustomerConfiguration(customerID,EphericalKey))
        );
    }

    private Dialog createDialog(String status, String message) {
        // Use the Builder class for convenient dialog construction
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle(status)
                .setMessage(message)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();

    }

    private void proceedFirebaseTransaction() {
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
                                                String status = "Payment Succeed";
                                                String message = "Your transaction is completed";
                                                Dialog dialog = createDialog(status,message);
                                                dialog.show();
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
                                            String status = "Payment Succeed";
                                            String message = "Your transaction is completed";
                                            Dialog dialog = createDialog(status,message);
                                            dialog.show();
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
                                                        String status = "Payment Succeed";
                                                        String message = "Your transaction is completed";
                                                        Dialog dialog = createDialog(status,message);
                                                        dialog.show();
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