package com.here2k19.projects.smartlastmilecommuter.Delivery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.android.mpa.common.GeoCoordinate;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Routing.Positioning;
import com.here2k19.projects.smartlastmilecommuter.activities.LoginActivity;
import com.here2k19.projects.smartlastmilecommuter.activities.MapActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetDeliveries extends AppCompatActivity  {

    List<SubOrdersModel> productList;
    RecyclerView recyclerView;

    public static List<SubOrdersModel> staticProducstsList;
    public static GeoCoordinate adminLoc;

    boolean flag = false;

    TextView noOrdersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_deliveries);

        getSupportActionBar().setTitle(getString(R.string.orders));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin").child("xxx").child("location");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Data",dataSnapshot.getValue().toString());
                Map<String,Object> loc = (Map<String, Object>) dataSnapshot.getValue();
                adminLoc = new GeoCoordinate(Double.parseDouble(loc.get("latitude").toString()),Double.parseDouble(loc.get("longitude").toString()));
//                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
//                double latitude = Double.parseDouble(map.get("latitude"));
//                double longitude = Double.parseDouble(map.get("longitude"));
//                adminLoc = new GeoCoordinate(latitude,longitude);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        noOrdersTextView =findViewById(R.id.noorderstext);
        FloatingActionButton floatingActionButton = findViewById(R.id.startnavigation);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("Loc",Positioning.latitude+","+Positioning.longitude);

                if(flag){
                    Intent intent = new Intent(GetDeliveries.this, MapActivity.class);
                    staticProducstsList = productList;
                    startActivity(intent);
                }else{
                    Toast.makeText(GetDeliveries.this, "No orders yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Positioning positioning=new Positioning();
        positioning.getPos(GetDeliveries.this);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        final SubOrdersAdapter subOrdersAdapter = new SubOrdersAdapter(this,productList);
        recyclerView.setAdapter(subOrdersAdapter);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference rr = FirebaseDatabase.getInstance().getReference("drivers").child(user.getUid()).child("currentorder");
        rr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SubOrdersModel subOrdersModel = dataSnapshot.getValue(SubOrdersModel.class);
                Log.e("Datas", subOrdersModel.toString());
                productList.add(subOrdersModel);
                subOrdersAdapter.notifyDataSetChanged();
                noOrdersTextView.setVisibility(View.GONE);
                flag = true;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error",databaseError.getMessage());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(GetDeliveries.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
