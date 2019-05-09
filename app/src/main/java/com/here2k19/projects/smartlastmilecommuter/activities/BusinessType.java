package com.here2k19.projects.smartlastmilecommuter.activities;

import java.io.File;
import java.util.List;
import java.util.zip.Inflater;

import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.GeocodeResult;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;
import com.here2k19.projects.smartlastmilecommuter.R;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BusinessType extends AppCompatActivity {
    Button b2b, b2c;
    private SupportMapFragment mapFragment = null;
    LocationManager locationManager;
    Context mContext;

public static double lat=0.0,lang=0.0;
int count=0;
MapActivity mapActivity;
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_type);
//SupportMapFragment supportMapFragment=findViewById(R.id.mapfragment);
        mContext = this;

        LocationListener locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                if (count == 0) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    lat = latitude;
                    lang = longitude;
                    String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
                    //Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                    Log.e("msgg", msg);
               count++;
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                10, locationListenerGPS);
        b2b=findViewById(R.id.b2b);
b2c=findViewById(R.id.b2c);
b2b.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      Geocoding geocoding=new Geocoding();
      geocoding.geocode("425+W+Randolph+Chicago",getApplicationContext());

    }
});
b2c.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Geocoding geocoding = new Geocoding();
        initEngine();
        GeoCoordinate geoCoordinate=new GeoCoordinate(13.08363, 80.28252);
     //   if (lat != 0.0) {
            geocoding.ReverseGeocode(geoCoordinate,BusinessType.this);
       // }
       // else
        //{
          //  Log.e("null","nulll");
        //}
        }
});
    }
public void initEngine()
{
    boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
            getApplication().getExternalFilesDir(null) + File.separator + ".here-maps",
            "com.here2k19.projects.smartlastmileuser.MapService");

    if(!success){

    }else{
        MapEngine.getInstance().init(new ApplicationContext(getApplicationContext()), new OnEngineInitListener() {

            @Override
            public void onEngineInitializationCompleted(Error error) {

                Toast.makeText(getApplicationContext(), "Map Engine initialized with error code:" + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
}
