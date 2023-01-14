package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.rmit.ecommerce.R;
import com.rmit.ecommerce.activity.MainActivity;
import com.rmit.ecommerce.fragment.NotificationModel;
import com.rmit.ecommerce.helper.Helper;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {
    private ArrayList<NotificationModel> mDataSet;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView notiTitle;
        TextView notiDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.notiCardView);
            notiTitle = itemView.findViewById(R.id.notiTitle);
            notiDescription = itemView.findViewById(R.id.notiDescription);
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
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
