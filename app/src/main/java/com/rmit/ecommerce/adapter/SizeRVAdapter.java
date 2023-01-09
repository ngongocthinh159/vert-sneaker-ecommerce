package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.repository.SizeModel;
import com.rmit.ecommerce.repository.SneakerModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SizeRVAdapter extends RecyclerView.Adapter<SizeRVAdapter.ViewHolder> {
    ArrayList<SizeModel> sizes;
    Context context;

    public ArrayList<SizeModel> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<SizeModel> sizes) {
        this.sizes = sizes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView sizeLabel;
        TextView quantity;
        MaterialButton minusQuantity;
        MaterialButton plusQuantity;
        MaterialButton deleteSize;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            sizeLabel = itemView.findViewById(R.id.sizeLabel);
            quantity = itemView.findViewById(R.id.quantity);
            minusQuantity = itemView.findViewById(R.id.minusQuantity);
            plusQuantity = itemView.findViewById(R.id.plusQuantity);
            deleteSize = itemView.findViewById(R.id.btnDeleteSize);

        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public TextView getSizeLabel() {
            return sizeLabel;
        }

        public TextView getQuantity() {
            return quantity;
        }

        public MaterialButton getMinusQuantity() {
            return minusQuantity;
        }

        public MaterialButton getPlusQuantity() {
            return plusQuantity;
        }

        public MaterialButton getDeleteSize() {
            return deleteSize;
        }
    }

    public SizeRVAdapter(ArrayList<SizeModel> sizes) {
        this.sizes = sizes;
    }
    public SizeRVAdapter() {
        this.sizes = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_size, parent, false);
        context = parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialCardView materialCardView = holder.getCardView();
        TextView sizeLabel = holder.getSizeLabel();
        TextView quantity =  holder.getQuantity();
        MaterialButton minusQuantity = holder.getMinusQuantity();
        MaterialButton plusQuantity = holder.getPlusQuantity();
        MaterialButton deleteSize = holder.getDeleteSize();
        SizeModel sizeModel = sizes.get(position);
        sizeLabel.setText(sizeModel.getSizeLabel());
        quantity.setText(Integer.toString(sizeModel.getQuantity()));

        plusQuantity.setOnClickListener(v -> {
            Integer newQuantity = sizes.get(position).getQuantity() + 1;
            sizes.get(position).setQuantity(newQuantity);
            quantity.setText(Integer.toString(newQuantity));
            if (minusQuantity.getVisibility() == View.GONE) {
                minusQuantity.setVisibility(View.VISIBLE);
            }
        });

        minusQuantity.setOnClickListener(v -> {
            Integer newQuantity = sizes.get(position).getQuantity() - 1;
            sizes.get(position).setQuantity(newQuantity);
            quantity.setText(Integer.toString(newQuantity));
            if (newQuantity == 0) {
                minusQuantity.setVisibility(View.GONE);
            } else {
                minusQuantity.setVisibility(View.VISIBLE);
            }
        });

        deleteSize.setOnClickListener(v -> {
            handleDelete(position);
        });
    }

    private void handleDelete(int position) {
        String toBeDeletedKey = sizes.get(position).getSizeLabel();
        FirebaseFirestore fs = FirebaseFirestore.getInstance();

        ArrayList<HashMap<String, Integer>> data = new ArrayList<>();
        HashMap<String, Integer> sizesMap = new HashMap<>();

        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).getSizeLabel() == toBeDeletedKey) {
                sizes.remove(i);
                break;
            }
        }

        for (SizeModel s : sizes) {
            sizesMap.put(s.getSizeLabel(), s.getQuantity());
        }
        data.add(sizesMap);

        fs.collection("sneakers").document(MainActivity.adminCrudService.getInstance().getCurrentSneakerId())
                .update("size", data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DELETE SIZE", "DocumentSnapshot successfully updated!");
                        SizeRVAdapter.this.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DELETE SIZE", "Error updating document", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }
}
