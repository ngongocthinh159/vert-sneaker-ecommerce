package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rmit.ecommerce.R;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentTest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentTest extends Fragment {

    View view;

    // KEYS
    String SECRET_KEY = "sk_test_51MNr5AL5lHyfXOrhD2cgBqpzbmpNbVzNt0YbV95XrqptNfT64Qxqn1DLWpRJQNGeKeZ9JdNJjJ3qGtriAqpuljuU00e8gaB5ZT";
    String PUBLISH_KEY = "pk_test_51MNr5AL5lHyfXOrhTe0MXdiw33twWQyZvzmskpbJvYIxBjdIQV26bPrGPyPhzWTrLL4l1Z5mtQHY6avZJBHD7xJE00o9tnlPGJ";

    PaymentSheet paymentSheet;

    String customerID;
    String EphericalKey;
    String ClientSecret;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PaymentTest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentTest.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentTest newInstance(String param1, String param2) {
        PaymentTest fragment = new PaymentTest();
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
       view = inflater.inflate(R.layout.fragment_payment_test, container, false);
       Button activePaymentBtn = view.findViewById(R.id.activeBtn);

        PaymentConfiguration.init(view.getContext(), PUBLISH_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {

        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    customerID = object.getString("id");
                    System.out.println(customerID);
                    getEphericalKey(customerID);
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }} ,new Response.ErrorListener(){
         @Override
         public void onErrorResponse(VolleyError error ){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);


       activePaymentBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               System.out.println("test");
           }
       });


       return view;
    }

    private void getEphericalKey(String customerID) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
                            Toast.makeText(view.getContext(), ClientSecret, Toast.LENGTH_SHORT).show();
                            getClientSecret(customerID,EphericalKey);

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }} ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error ){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                header.put("Stripe-Version","2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("Authorization","Bearer " + SECRET_KEY);
                return super.getParams();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
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
                            Toast.makeText(view.getContext(), ClientSecret, Toast.LENGTH_SHORT).show();
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }} ,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error ){

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header = new HashMap<>();
                header.put("Authorization","Bearer " + SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "1000" + "00");
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");

                return super.getParams();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(stringRequest);

    }
}