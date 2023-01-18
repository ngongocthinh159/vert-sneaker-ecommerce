package com.rmit.ecommerce.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.helper.Helper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    private View view;
    MaterialButton btnBack;
    MaterialButton btnReset;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetPasswordFragment newInstance(String param1, String param2) {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
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
        view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        TextInputEditText email = view.findViewById(R.id.etEmailReset);
        btnReset = view.findViewById(R.id.btnEmailReset);
        btnBack = view.findViewById(R.id.btnBackReset);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.navigateUp();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_str = email.getText().toString();

                if (email_str.isEmpty()) {
                    Toast.makeText(MainActivity.context, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Helper.isValidEmail(email_str)) {
                    Toast.makeText(MainActivity.context, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnReset.setEnabled(false);
                btnBack.setEnabled(false);
                MainActivity.userManager.getAuth().sendPasswordResetEmail(email_str).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.context, "Please follow the instructions were sent to your email to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.context, "Your email is not linked to any account!", Toast.LENGTH_SHORT).show();
                        }

                        btnReset.setEnabled(true);
                        btnBack.setEnabled(true);
                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}