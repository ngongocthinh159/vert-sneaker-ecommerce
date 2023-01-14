package com.rmit.ecommerce.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.rmit.ecommerce.activity.MainActivity;
import com.stripe.android.EphemeralKey;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PaymentTest extends Fragment {

    View view;

    // KEYS
    String SECRET_KEY = "sk_test_51MNr5AL5lHyfXOrhD2cgBqpzbmpNbVzNt0YbV95XrqptNfT64Qxqn1DLWpRJQNGeKeZ9JdNJjJ3qGtriAqpuljuU00e8gaB5ZT";
    String PUBLISH_KEY = "pk_test_51MNr5AL5lHyfXOrhTe0MXdiw33twWQyZvzmskpbJvYIxBjdIQV26bPrGPyPhzWTrLL4l1Z5mtQHY6avZJBHD7xJE00o9tnlPGJ";

    PaymentSheet paymentSheet;

    String customerID;
    String EphericalKey;
    String ClientSecret;

    public PaymentTest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_payment_test, container, false);

        Button activePaymentBtn = view.findViewById(R.id.activeBtn);
        activePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
        });

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
                    Toast.makeText(MainActivity.context, customerID,Toast.LENGTH_SHORT).show();
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
                            EphericalKey = object.getString("secret");
                            Toast.makeText(MainActivity.context, EphericalKey, Toast.LENGTH_SHORT).show();
                            getClientSecret(customerID,EphericalKey);
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

            // assigned payment detail
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "11" + "000");
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.context);
        requestQueue.add(stringRequest);

    }


    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(MainActivity.context, "Payment success", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(MainActivity.context, "Not Success", Toast.LENGTH_SHORT).show();
        }
    }

    private void PaymentFlow() {
        paymentSheet.presentWithPaymentIntent(
                ClientSecret,new PaymentSheet.Configuration("EET Online Market",
                        new PaymentSheet.CustomerConfiguration(customerID,EphericalKey))
        );
    }


}