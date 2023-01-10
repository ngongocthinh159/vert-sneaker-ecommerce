package com.rmit.ecommerce.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.CartModel;
import com.rmit.ecommerce.repository.UserManager;
import com.rmit.ecommerce.repository.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    View view;
    TextInputEditText userEmail;
    TextInputEditText userName;
    TextInputEditText password;
    TextInputEditText rePassword;
    MaterialButton submitBtn;
    MaterialButton previousBtn;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        // Get refs
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        password = view.findViewById(R.id.password);
        rePassword = view.findViewById(R.id.rePassword);
        submitBtn = view.findViewById(R.id.submitBtn);
        previousBtn = view.findViewById(R.id.previousBtn);

        // Setup buttons
        setupSubmitBtn();
        previousBtn.setOnClickListener(v -> {
            MainActivity.navController.popBackStack();
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void setupSubmitBtn() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Loading icon
                CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(getContext(), null, 0, com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator);
                Drawable progress = IndeterminateDrawable.createCircularDrawable(getContext(), spec);
                submitBtn.setIcon(progress);

                // Check valid username and password
                if (isNotValid()) {
                    submitBtn.setIcon(null);
                    return;
                }

                // Create new user
                MainActivity.userManager.getAuth().createUserWithEmailAndPassword(userEmail.getText().toString(),
                        password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Create new cart
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

                                            // Create new user profile
                                            String newUserId = task.getResult().getUser().getUid();
                                            Map<String, Object> data2 = new HashMap<>();
                                            data2.put("address", "");
                                            data2.put("cardNumber", "");
                                            data2.put("currentCartId", newCartId);
                                            data2.put("displayName", userName.getText().toString());
                                            data2.put("historyCartIds", new ArrayList<>());
                                            data2.put("image", "");
                                            data2.put("isAdmin", false);
                                            data2.put("phone", "");
                                            MainActivity.repositoryManager.getFireStore()
                                                    .collection("users")
                                                    .document(newUserId).set(data2);

                                            // Fetch user information
                                            fetchUserInformation();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.context, "Failed to create your account!",
                                    Toast.LENGTH_SHORT).show();
                            submitBtn.setIcon(null);
                        }
                    }
                });
            }
        });
    }

    private boolean isNotValid() {
        // Check empty
        if (userName.getText().toString().isEmpty() || userEmail.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.context, "User name/email/password cannot be empty!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        // Check email valid (form of: abc@mail.com)
        if (!Helper.isValidEmail(userEmail.getText().toString())) {
            Toast.makeText(MainActivity.context, "Email is not valid!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        // Check password valid (length >= 6)
        if (!(password.getText().toString().length() >= 6)) {
            Toast.makeText(MainActivity.context, "Password length must be greater than 6!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        // Check re-type password
        if (!password.getText().toString().equals(rePassword.getText().toString())) {
            Toast.makeText(MainActivity.context, "Re-typed password is not identical to password!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private void fetchUserInformation() {
        MainActivity.repositoryManager.getFireStore().collection("users").document(MainActivity.userManager.getUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            MainActivity.repositoryManager.setUser(documentSnapshot.toObject(UserModel.class));
                            fetchCartObject();
                        }
                    }
                });
    }

    private void fetchCartObject() {
        // Get cart information
        DocumentReference cartDoc =  MainActivity.repositoryManager
                .getFireStore()
                .collection("carts")
                .document(MainActivity.repositoryManager.getUser().getCurrentCartId());
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

    private void fetchCartItems() {
        MainActivity.repositoryManager.getCartItems().clear();
        ArrayList<String> cartItemIds = MainActivity.repositoryManager.getCartObject().getCartItemIds();
        CollectionReference collection = MainActivity.repositoryManager
                .getFireStore().collection("cartItems");
        collection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get all the cart items, which has those ids inside cartItemIds list
                        if (cartItemIds.contains(document.getId())) {
                            MainActivity.repositoryManager
                                    .getCartItems().add(document.toObject(CartItemModel.class));
                        }
                    }

                    // Redirect to home screen
                    Helper.popBackStackAll();
                    MainActivity.navController.navigate(R.id.homeFragment);
                }
            }
        });
    }
}