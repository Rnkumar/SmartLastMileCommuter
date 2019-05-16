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
        View view = inflater.inflate(R.layout.layout_deliveries, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        SubOrdersModel deliveries=productList.get(i);
        viewHolder.item.setText(deliveries.getItemName());
        viewHolder.quantity.setText(deliveries.getQuantity());
        viewHolder.location.setText(deliveries.getAddress());
        /*viewHolder.bt.setOnClickListener(new View.OnClickListener() {
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
        });*/
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView item,quantity,location;
        Button bt,bt1;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            item=itemView.findViewById(R.id.itemvalue);
            quantity=itemView.findViewById(R.id.quantityvalue);
            location=itemView.findViewById(R.id.locvalue);
            bt=itemView.findViewById(R.id.okbt);
            bt1=itemView.findViewById(R.id.clostbt);
            bt.setVisibility(View.GONE);
            bt1.setVisibility(View.GONE);
        }
    }
}
