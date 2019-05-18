package com.here2k19.projects.smartlastmilecommuter.Delivery;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.activities.MapActivity;

import java.util.List;

public class SubOrdersAdapter extends RecyclerView.Adapter<SubOrdersAdapter.ViewHolder> {
   Context mCtx;
    private List<SubOrdersModel> productList;

    public SubOrdersAdapter(Context mCtx, List<SubOrdersModel> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_order, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        SubOrdersModel ordersModel=productList.get(i);

        viewHolder.orderId.setText(String.format("%s%s", viewHolder.orderId.getText().toString(), ordersModel.getOrderId()));
        viewHolder.quantity.setText(String.format("%s%s", viewHolder.quantity.getText().toString(), String.valueOf(ordersModel.getQuantity())));
        viewHolder.deliveryLocation.setText(String.format("%s%s", viewHolder.deliveryLocation.getText(), ordersModel.getAddress()));
        viewHolder.mobile.setText(String.format("%s%s",viewHolder.mobile.getText(),ordersModel.getMobile()));
        viewHolder.name.setText(String.format("%s%s", viewHolder.name.getText().toString(), ordersModel.getItemName()));

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView orderId, quantity, name, mobile,deliveryLocation;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_item_id);
            quantity = itemView.findViewById(R.id.return_item_quantity);
            name = itemView.findViewById(R.id.return_item_name);
            mobile = itemView.findViewById(R.id.mobile);
            deliveryLocation = itemView.findViewById(R.id.return_item_delivery_location);
        }
    }
}
