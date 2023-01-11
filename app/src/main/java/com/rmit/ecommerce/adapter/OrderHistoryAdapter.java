package com.rmit.ecommerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmit.ecommerce.R;
import com.rmit.ecommerce.repository.CartModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<CartModel> orderList;
    private Context context;

    public OrderHistoryAdapter(ArrayList<CartModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_order_history, parent, false);

        context = parent.getContext();

        return new OrderHistoryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TextView purchaseDate = holder.getPurchaseDate();
        TextView totalPurchase = holder.getTotalPurchase();
        TextView status = holder.getStatus();

        try {
            Date date = orderList.get(position).getPurchaseDate().toDate();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String formattedDate = simpleDateFormat.format(date);
            purchaseDate.setText("Payment at " + formattedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        totalPurchase.setText("Total: $" + orderList.get(position).getTotal());
        if (orderList.get(position).getStatus()) {
            status.setText("Status: success");
        } else status.setText("Status: failed");
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView purchaseDate;
        private TextView totalPurchase;
        private TextView status;

        public ViewHolder(@NonNull View view) {
            super(view);

            purchaseDate = view.findViewById(R.id.purchaseDate);
            totalPurchase = view.findViewById(R.id.totalPurchase);
            status = view.findViewById(R.id.status);
        }

        public TextView getPurchaseDate() {
            return purchaseDate;
        }

        public TextView getTotalPurchase() {
            return totalPurchase;
        }

        public TextView getStatus() {
            return status;
        }
    }
}
