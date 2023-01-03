package com.rmit.ecommerce.repository;

import static android.content.ContentValues.TAG;

import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Map;

public class RepositoryManager {
    private static ArrayList<SneakerModel> sneakers = new ArrayList<>();

    public RepositoryManager() {}

    public ArrayList<SneakerModel> getSneakers() {
        return sneakers;
    }

    public void setSneakers(ArrayList<SneakerModel> s) {
        sneakers = s;
    }
}
