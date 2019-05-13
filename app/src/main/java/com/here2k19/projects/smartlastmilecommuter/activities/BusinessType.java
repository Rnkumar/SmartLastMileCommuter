package com.here2k19.projects.smartlastmilecommuter.activities;

import com.here.android.mpa.mapping.SupportMapFragment;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Delivery.GetDeliveries;
import com.here2k19.projects.smartlastmilecommuter.Geocoding.MainView;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BusinessType extends AppCompatActivity {
    Button b2b, b2c;
    private SupportMapFragment mapFragment = null;
    LocationManager locationManager;
    Context mContext;

    public static double lat = 0.0, lang = 0.0;
    int count = 0;
    MapActivity mapActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_type);
//        initEngine();
        //SupportMapFragment supportMapFragment=findViewById(R.id.mapfragment);

        mContext = this;
MainView mainView=new MainView(this);
        b2b = findViewById(R.id.b2b);
        b2c = findViewById(R.id.b2c);
        b2b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Intent intent=new Intent(BusinessType.this, GetDeliveries.class);
intent.putExtra("reversed_value",MainView.revvalue);
startActivity(intent);
            }
        });
        b2c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                       }
        });
    }


}
