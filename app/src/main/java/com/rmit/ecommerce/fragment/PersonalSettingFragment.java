package com.rmit.ecommerce.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rmit.ecommerce.helper.Helper;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.UserImageManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalSettingFragment extends Fragment {

    View view;
    TextView tvUserName;
    TextView tvEmail;
    TextView tvPhone;
    TextView tvAddress;
    TextView tvCardNum;
    public static final int PICK_IMAGE_CODE = 200;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PersonalSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalSettingFragment newInstance(String param1, String param2) {
        PersonalSettingFragment fragment = new PersonalSettingFragment();
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
        view = inflater.inflate(R.layout.fragment_personal_setting, container, false);

        // Get refs
        tvUserName = view.findViewById(R.id.tvUserName);
        tvEmail = view.findViewById(R.id.tvUserEmail);
        tvPhone = view.findViewById(R.id.tvUserPhone);
        tvAddress = view.findViewById(R.id.tvUserAddress);
        tvCardNum = view.findViewById(R.id.tvUserCardNum);

        // Setup logout button
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = createDialog();
                dialog.show();
            }
        });

        // Map text data (user information)
        mapTextData(view);

        // Setup edit button
        setupEditButton(view);

        // Setup order history button
        MaterialButton btnOrderHistory = view.findViewById(R.id.btnOrderHistory);
        btnOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.navController.navigate(R.id.action_personalSettingFragment_to_orderHistoryFragment);
            }
        });

        // Setup
        TextView userName = view.findViewById(R.id.tvUserName);
        MaterialButton btnEditUserName = view.findViewById(R.id.editUserName);
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditUserName.getVisibility() == View.VISIBLE) btnEditUserName.setVisibility(View.GONE);
                else btnEditUserName.setVisibility(View.VISIBLE);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void mapTextData(View view) {
        // Set text data
        // Display name
        if (MainActivity.repositoryManager.getUser().getDisplayName() != null
                && !MainActivity.repositoryManager.getUser().getDisplayName().isEmpty()) {
            tvUserName.setText(MainActivity.repositoryManager.getUser().getDisplayName());
        }

        // Phone
        if (MainActivity.repositoryManager.getUser().getPhone() != null
                && !MainActivity.repositoryManager.getUser().getPhone().isEmpty()) {
            tvPhone.setText(MainActivity.repositoryManager.getUser().getPhone().substring(0, 3) + "-" + MainActivity.repositoryManager.getUser().getPhone().substring(3));
        }

        // Email
        tvEmail.setText(MainActivity.userManager.getUser().getEmail());

        // Address
        if (MainActivity.repositoryManager.getUser().getAddress() != null
        && !MainActivity.repositoryManager.getUser().getAddress().isEmpty()) {
            tvAddress.setText(MainActivity.repositoryManager.getUser().getAddress());
        }

        // Card number
        if (MainActivity.repositoryManager.getUser().getCardNumber() != null
                && !MainActivity.repositoryManager.getUser().getCardNumber().isEmpty()) {
            String temp = MainActivity.repositoryManager.getUser().getCardNumber();
            String cardNum_formatted = temp.substring(0, 4) + " " + temp.substring(4, 8) + " " + temp.substring(8, 12) + " " + temp.substring(12, 16);
            tvCardNum.setText(cardNum_formatted);
        }
    }

    private void setupEditButton(View view) {
        // Setup button Edit address
        MaterialButton btnEditAddress = view.findViewById(R.id.editAddress);
        btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocationPermissionGranted()) {
                    getLocationPermission();
                    Toast.makeText(MainActivity.context, "Need location permission!", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.navController.navigate(R.id.action_personalSettingFragment_to_googleMapFragment);
                }
            }
        });

        // Setup button Edit image
        MaterialCardView card = view.findViewById(R.id.cardUserImage);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, UserImageManager.RQ_USER_CODE);
            }
        });

        // Setup button Edit display name
        MaterialButton btnEditUserName = view.findViewById(R.id.editUserName);
        btnEditUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog("Update display name", "displayName");
            }
        });

        // Setup button Edit display name
        MaterialButton btnEditEmail = view.findViewById(R.id.editEmail);
        btnEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog("Update email address", "email");
            }
        });

        // Setup button Edit phone
        MaterialButton btnEditPhone = view.findViewById(R.id.editPhone);
        btnEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog("Update phone number", "phone");
            }
        });

        // Setup button Edit card number
        MaterialButton btnEditCardNumber = view.findViewById(R.id.editCard);
        btnEditCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog("Update card number", "cardnumber");
            }
        });
    }

    private void showEditTextDialog(String title, String type) {
        FrameLayout frameLayout = null;
        switch (type) {
            case "displayName":
                frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.edit_text_pick_display_name, null);
                break;
            case "email":
                frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.edit_text_pick_email, null);
                break;
            case "phone":
                frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.edit_text_pick_phone, null);
                break;
            case "cardnumber":
                frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.edit_text_pick_card_number, null);
                break;
        }

        // Setup dialog
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.context);
        alert.setTitle(title);
        alert.setCancelable(false);
        alert.setView(frameLayout);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Get refs
        TextInputEditText name = frameLayout.findViewById(R.id.pickName);
        TextInputEditText email = frameLayout.findViewById(R.id.pickEmail);
        TextInputEditText phone = frameLayout.findViewById(R.id.pickPhone);
        TextInputEditText cardNumber = frameLayout.findViewById(R.id.pickCardNumber);
        TextInputEditText password = frameLayout.findViewById(R.id.pickCurrentPassword);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (type) {
                    case "displayName":
                        updateDisplayName(name.getText().toString(), dialog);
                        break;
                    case "email":
                        updateEmail(email.getText().toString(), password.getText().toString(), dialog);
                        break;
                    case "phone":
                        updatePhone(phone.getText().toString(), dialog);
                        break;
                    case "cardnumber":
                        updateCardNumber(cardNumber.getText().toString(), dialog);
                        break;
                }
            }
        });
        alert.show();
    }

    private void updateDisplayName(String name, DialogInterface dialog) {
        if (name.isEmpty()) {
            Toast.makeText(MainActivity.context, "Display name cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update local
        MainActivity.repositoryManager.getUser().setDisplayName(name);

        // Update remote
        MainActivity.repositoryManager.getFireStore()
                .collection("users")
                .document(MainActivity.userManager.getUser().getUid())
                .update("displayName", name)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            tvUserName.setText(name);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.context, "Display name updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.context, "Update failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateEmail(String email, String password, DialogInterface dialog) {
        if (!Helper.isValidEmail(email)) {
            Toast.makeText(MainActivity.context, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.context, "Email or password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update remote
        // Re-authenticate
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Get credential
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), password);

            // Re-authenticate
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update email in server
                                MainActivity.userManager.getUser().updateEmail(email)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    tvEmail.setText(email);
                                                    dialog.dismiss();
                                                    Toast.makeText(MainActivity.context, "Email updated!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(MainActivity.context, "Update failed!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(MainActivity.context, "Password is not correct!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void updatePhone(String phone, DialogInterface dialog) {
        if (phone.isEmpty()) {
            Toast.makeText(MainActivity.context, "Phone cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() < 9) {
            Toast.makeText(MainActivity.context, "Not a valid phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update local
        MainActivity.repositoryManager.getUser().setPhone("+84" + phone);

        // Update remote
        MainActivity.repositoryManager.getFireStore()
                .collection("users")
                .document(MainActivity.userManager.getUser().getUid())
                .update("phone", "+84" + phone)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            tvPhone.setText("+84-" + phone);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.context, "Phone number updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.context, "Update failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateCardNumber(String cardNumber, DialogInterface dialog) {
        if (cardNumber.length() != 16) {
            Toast.makeText(MainActivity.context, "Invalid card number!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update local
        MainActivity.repositoryManager.getUser().setCardNumber(cardNumber);

        // Update remote
        MainActivity.repositoryManager.getFireStore()
                .collection("users")
                .document(MainActivity.userManager.getUser().getUid())
                .update("cardNumber", cardNumber)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String temp = MainActivity.repositoryManager.getUser().getCardNumber();
                            String cardNum_formatted = temp.substring(0, 4) + " " + temp.substring(4, 8) + " " + temp.substring(8, 12) + " " + temp.substring(12, 16);
                            tvCardNum.setText(cardNum_formatted);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.context, "Card number updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.context, "Update failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getLocationPermission() {
        if (!isLocationPermissionGranted()) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GoogleMapFragment.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(MainActivity.context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private Dialog createDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You are about logging out")
                .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.userManager.getAuth().signOut();

                        // Redirect to getting started page
                        Helper.popBackStackAll();
                        MainActivity.navController.navigate(R.id.gettingStartedFragment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
