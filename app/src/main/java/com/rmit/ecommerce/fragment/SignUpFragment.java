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
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.repository.UserManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {

    View view;
    TextInputEditText userName;
    TextInputEditText password;
    TextInputEditText rePassword;
    MaterialButton submitBtn;
    MaterialButton loginBtn;

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
        password = view.findViewById(R.id.password);
        rePassword = view.findViewById(R.id.rePassword);
        submitBtn = view.findViewById(R.id.submitBtn);
        loginBtn = view.findViewById(R.id.loginBtn);

        // Setup submit button
        setupSubmitBtn();

        // Setup login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.popBackStack();
                MainActivity.navController.navigate(R.id.action_welcomeFragment_to_loginFragment);
            }
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

                MainActivity.userManager.getAuth().createUserWithEmailAndPassword(userName.getText().toString(),
                        password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Redirect to home screen
                            Helper.popBackStackAll();
                            MainActivity.navController.navigate(R.id.homeFragment);
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
        if (userName.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.context, "User name or password cannot be empty!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!(password.getText().toString().length() >= 6)) {
            Toast.makeText(MainActivity.context, "Password length must be greater than 6!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        if (!password.getText().toString().equals(rePassword.getText().toString())) {
            Toast.makeText(MainActivity.context, "Re-typed password is not identical to password!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}