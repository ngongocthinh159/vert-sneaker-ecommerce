package com.rmit.ecommerce;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.adapter.NotificationRVAdapter;
import com.rmit.ecommerce.fragment.NotificationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationManager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationManager extends Fragment {
    View view;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationManager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationManager.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationManager newInstance(String param1, String param2) {
        NotificationManager fragment = new NotificationManager();
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
        view = inflater.inflate(R.layout.fragment_notification_manager, container, false);
        fetchNotifications();
        Button previousBtn = view.findViewById(R.id.previousBtn);
        TextInputLayout title = view.findViewById(R.id.notiTitle);
        TextInputLayout content = view.findViewById(R.id.notiContent);
        Button publishBtn = view.findViewById(R.id.publishBtn);

        publishBtn.setOnClickListener(v -> {
            handlePublishNotification(title, content);
            fetchNotifications();
        });

        previousBtn.setOnClickListener(v -> {
            MainActivity.navController.navigateUp();
        });
        return view;
    }

    private void fetchNotifications() {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        ArrayList<NotificationModel> notifications = new ArrayList<>();
        fs.collection("notifications").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot snapshot) {
                for (DocumentSnapshot document: snapshot.getDocuments()) {
                    notifications.add(document.toObject(NotificationModel.class));
                }
                Collections.sort(notifications);
                NotificationRVAdapter notificationRVAdapter = new NotificationRVAdapter(notifications);
                notificationRVAdapter.setShouldRenderDelete(true);
                recyclerView.setAdapter(notificationRVAdapter);
                LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.context, "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePublishNotification(TextInputLayout title, TextInputLayout content) {
        FirebaseFirestore fs = FirebaseFirestore.getInstance();
        fs.collection("notifications")
                .add(new NotificationModel(
                        title.getEditText().getText().toString(),
                        content.getEditText().getText().toString(),
                        new Timestamp(new Date())))
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                fetchNotifications();
                title.getEditText().setText("");
                content.getEditText().setText("");
                Toast.makeText(MainActivity.context, "Published", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.context, "Failed to publish", Toast.LENGTH_SHORT).show();
                        System.out.println("Fail");
                    }
                });
    }
}