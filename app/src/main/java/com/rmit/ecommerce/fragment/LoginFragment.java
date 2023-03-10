package com.rmit.ecommerce.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.CartItemModel;
import com.rmit.ecommerce.repository.CartModel;
import com.rmit.ecommerce.repository.UserModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private View view;
    private MaterialButton loginBtn;
    private MaterialButton resetBtn;
    private TextInputEditText userName;
    private TextInputEditText passWord;
    private MaterialButton btnBack;

    private static final String LOGIN_ERROR_TEXT = "Your user name or password is not correct!";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        view = inflater.inflate(R.layout.fragment_login, container, false);

        // Get refs
        loginBtn = view.findViewById(R.id.loginBtn);
        resetBtn = view.findViewById(R.id.resetBtn);
        userName = view.findViewById(R.id.userName);
        passWord = view.findViewById(R.id.passWord);
        btnBack = view.findViewById(R.id.previousBtn);

        // Setup back button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.navigateUp();
            }
        });


        // Setup login button
        setupLoginBtn();

        // Inflate the layout for this fragment
        return view;
    }

    private void setupLoginBtn() {
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.navigate(R.id.action_loginFragment_to_resetPasswordFragment);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Loading icon
                CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(getContext(), null, 0, com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator);
                Drawable progress = IndeterminateDrawable.createCircularDrawable(getContext(), spec);
                loginBtn.setIcon(progress);
                disableAllButtons();

                // Check valid username and password
                if (userName.getText().toString().isEmpty() || passWord.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.context, "Your user name or password is incorrect!", Toast.LENGTH_SHORT).show();
                    loginBtn.setIcon(null);
                    enableAllButtons();
                    return;
                }

                MainActivity.userManager.getAuth().signInWithEmailAndPassword(userName.getText().toString(),
                        passWord.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // If successfully login => fetch user information
                            fetchUserInformation();
                        } else {
                            Toast.makeText(MainActivity.context, "Your user name or password is incorrect!", Toast.LENGTH_SHORT).show();
                            loginBtn.setIcon(null);
                            enableAllButtons();
                        }
                    }
                });
            }
        });
    }

    private void enableAllButtons() {
        loginBtn.setEnabled(true);
        btnBack.setEnabled(true);
        userName.setEnabled(true);
        passWord.setEnabled(true);
        resetBtn.setEnabled(true);
    }


    private void disableAllButtons() {
        loginBtn.setEnabled(false);
        btnBack.setEnabled(false);
        userName.setEnabled(false);
        passWord.setEnabled(false);
        resetBtn.setEnabled(false);
    }

    private void fetchUserInformation() {
        MainActivity.repositoryManager.getFireStore().collection("users").document(MainActivity.userManager.getUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserModel user = documentSnapshot.toObject(UserModel.class);
                            MainActivity.repositoryManager.setUser(user);
                            if (user.getIsAdmin()) {
                                Helper.popBackStackAll();
                                MainActivity.bottomNav.setVisibility(View.GONE);
                                MainActivity.navController.navigate(R.id.homeAdminFragment);
                            } else {
                                fetchCartObject();
                            }
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