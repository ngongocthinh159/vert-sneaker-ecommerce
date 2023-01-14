package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.fragment.NotificationModel;
import com.rmit.ecommerce.helper.Helper;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {
    private ArrayList<NotificationModel> mDataSet;
    Context context;
    private boolean shouldRenderDelete = false;

    public boolean isShouldRenderDelete() {
        return shouldRenderDelete;
    }

    public void setShouldRenderDelete(boolean shouldRenderDelete) {
        this.shouldRenderDelete = shouldRenderDelete;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView notiTitle;
        TextView notiDescription;
        MaterialButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.notiCardView);
            notiTitle = itemView.findViewById(R.id.notiTitle);
            notiDescription = itemView.findViewById(R.id.notiDescription);
            deleteBtn = itemView.findViewById(R.id.btnDelete);
        }

        public TextView getNotiTitle() {
            return notiTitle;
        }

        public TextView getNotiDescription() {
            return notiDescription;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public MaterialButton getDeleteBtn() { return deleteBtn; }
    }

    public NotificationRVAdapter( ArrayList<NotificationModel> dataSet) {
        this.mDataSet = dataSet;
    }

    @NonNull
    @Override
    public NotificationRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_notification, parent, false);

        context = parent.getContext();

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull NotificationRVAdapter.ViewHolder holder, int position) {
        holder.getNotiTitle().setText(mDataSet.get(position).getTitle());
        holder.getNotiDescription().setText(mDataSet.get(position).getContent());
        if (shouldRenderDelete) {
            holder.getDeleteBtn().setVisibility(View.VISIBLE);
            holder.getDeleteBtn().setOnClickListener(v -> {
                // Handle delete
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
