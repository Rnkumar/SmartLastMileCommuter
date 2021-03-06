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

public class DeliveriesAdapter extends RecyclerView.Adapter<DeliveriesAdapter.ViewHolder> {
   Context mCtx;

    public DeliveriesAdapter(Context mCtx, List<Deliveries> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    private List<Deliveries> productList;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_deliveries, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
Deliveries deliveries=productList.get(i);
viewHolder.item.setText(deliveries.getItem());
viewHolder.quantity.setText(deliveries.getQuantity());
viewHolder.location.setText(deliveries.getLocation());
viewHolder.bt.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      //  Toast.makeText(mCtx,"OkClicked",Toast.LENGTH_LONG).show();
        Intent intent=new Intent(mCtx, MapActivity.class);
        intent.putExtra("admin_loc",viewHolder.location.getText().toString());
        mCtx.startActivity(intent);
    }
});
viewHolder.bt1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(mCtx,"CloseClicked",Toast.LENGTH_LONG).show();
    }
});
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item,quantity,location;
        Button bt,bt1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.itemvalue);
            quantity=itemView.findViewById(R.id.quantityvalue);
            location=itemView.findViewById(R.id.locvalue);
            bt=itemView.findViewById(R.id.okbt);
            bt1=itemView.findViewById(R.id.clostbt);
        }
    }
}
