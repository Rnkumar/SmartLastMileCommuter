package com.here2k19.projects.smartlastmilecommuter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.here2k19.projects.smartlastmilecommuter.R;

import java.util.ArrayList;
import java.util.List;

public class GetDeliveries extends AppCompatActivity  {
    List<Deliveries> productList;

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_deliveries);
    recyclerView=findViewById(R.id.recyclerView);
    recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        productList.add(new Deliveries("apple","50"));
        productList.add(new Deliveries("orange","150"));
        DeliveriesAdapter adapter=new DeliveriesAdapter(this,productList);
        recyclerView.setAdapter(adapter);
    }
}
