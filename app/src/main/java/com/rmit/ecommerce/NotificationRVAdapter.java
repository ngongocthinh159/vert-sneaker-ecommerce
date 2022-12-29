package com.rmit.ecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {
    private ArrayList<String> mDataSet;
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

    public NotificationRVAdapter( ArrayList<String> dataSet) {
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
        // Resize width for card view (responsive)
        float cardWidthPixel = (MainActivity.device_width_pxl - Helper.convertDpToPixel(9*3, context)) / 2;
        ViewGroup.LayoutParams params = holder.cardView.getLayoutParams();
//        params.width = (int) cardWidthPixel;
        holder.getCardView().setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
