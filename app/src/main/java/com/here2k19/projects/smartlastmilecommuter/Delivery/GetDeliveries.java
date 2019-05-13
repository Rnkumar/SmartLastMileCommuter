package com.here2k19.projects.smartlastmilecommuter.Delivery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Notification.Notification;

import java.util.ArrayList;
import java.util.List;

public class GetDeliveries extends AppCompatActivity  {
    List<Deliveries> productList;
    RecyclerView recyclerView;
   Notification notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_deliveries);
//        notification=new Notification();
//        notification.getNotification("Hello","This is notification",this);
        String value=getIntent().getExtras().getString("reversed_value");
    if(value!=null)
    {
            recyclerView=findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            productList = new ArrayList<>();
            productList.add(new Deliveries("apple","50","kundrathur"));
            productList.add(new Deliveries("orange","150","tambaram"));
            productList.add(new Deliveries("apple","50","kundrathur"));
            productList.add(new Deliveries("orange","150","tambaram"));
            productList.add(new Deliveries("apple","50","kundrathur"));
            productList.add(new Deliveries("orange","150","tambaram"));
            DeliveriesAdapter adapter=new DeliveriesAdapter(this,productList);
            recyclerView.setAdapter(adapter);

    }
    }
}