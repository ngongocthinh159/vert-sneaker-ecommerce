package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.rmit.ecommerce.SaveSharedPreference;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private View view;
    private TextInputEditText userName;
    private TextInputEditText passWord;

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

        Button loginBtn = view.findViewById(R.id.loginBtn);
        userName = view.findViewById(R.id.userName);
        passWord = view.findViewById(R.id.passWord);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidAccount()) {
                    // Save user login state
                    SaveSharedPreference.setUserName(view.getContext(), userName.getText().toString());
                    if (userName.getText().toString().equals("admin")) SaveSharedPreference.setUserRole(view.getContext(), "admin");
                    else SaveSharedPreference.setUserRole(view.getContext(), "normal");

                    // Redirect to home screen
                    Helper.popBackStackAll();
                    if (SaveSharedPreference.getUserRole(view.getContext()).equals("normal")) {
                        MainActivity.navController.navigate(R.id.homeFragment);
                    }
                    if (SaveSharedPreference.getUserRole(view.getContext()).equals("admin")) {
                        MainActivity.navController.navigate(R.id.homeAdminFragment);
                    }
                } else {
                    Toast.makeText(view.getContext(), LOGIN_ERROR_TEXT, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private boolean isValidAccount() {
        return userName.getText().toString().length() >= 1;
    }
}