package com.here2k19.projects.smartlastmilecommuter.Geocoding;

import java.io.File;
import java.util.List;

import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.GeocodeRequest;
import com.here.android.mpa.search.GeocodeResult;
import com.here.android.mpa.search.Location;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeRequest;
import com.here2k19.projects.smartlastmilecommuter.activities.MapActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class demonstrates the usage of Geocoding and Reverse Geocoding request APIs
 */
public class MainView {
   // private static final Object TODO ="sdf" ;
    private AppCompatActivity m_activity;
    MapActivity mapActivity;
    private TextView m_resultTextView;
    LocationManager locationManager;
    boolean initMap = false;
    PositioningManager posManager;
    public static String revvalue="";
   public static String currentloclat,currentloclang=null;
    double lat, lang;
    int count=0;
  public static boolean latlangvalue=false;
    public static String latitude,longitude=null;
    boolean mapAct=false;
ProgressDialog progressDialog=null;
    public MainView(final AppCompatActivity activity) {
         progressDialog=new ProgressDialog(activity);
        progressDialog.setTitle("Wait we are allocating the job with respected to location ");
        progressDialog.setCancelable(false);
         progressDialog.show();

        m_activity = activity;
        initMapEngine();
        initUIElements();
        //triggerRevGeocodeRequest(lat,lang);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000,
                        10, locationListenerGPS);
            }
        },2000);

    //getCurrentLocation();
    }
LocationListener locationListenerGPS=new LocationListener() {
    @Override
    public void onLocationChanged(android.location.Location location) {
      if(count==0) {
          Log.e("location", "" + location.getLatitude());
       if(!initMap)
       {
           count=0;
       }
          if(initMap) {
   MainView.currentloclat=""+location.getLatitude();
MainView.currentloclang=""+location.getLongitude();
Log.e("currentlat",currentloclat);
triggerRevGeocodeRequest(location.getLatitude(), location.getLongitude());
           count++;
       }
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

    public MainView(final MapActivity activity) {

mapAct=true;
        mapActivity = activity;
        initMapEngine();
        initUIElements();
        //triggerRevGeocodeRequest(lat,lang);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        2000,
                        10, locationListenerGPS);
            }
        },2000);

    }

    private void getCurrentLocation() {

    }

    private void initMapEngine() {
        // Set path of isolated disk cache
       if(!mapAct) {
           boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                   m_activity.getExternalFilesDir(null) + File.separator + ".here-maps",
                   "com.here2k19.projects.smartlastmilecommuter.MapService");
           if (!success) {
               // Setting the isolated disk cache was not successful, please check if the path is valid and
               // ensure that it does not match the default location
               // (getExternalStorageDirectory()/.here-maps).
               // Also, ensure the provided intent name does not match the default intent name.
           } else {
               /*
                * Even though we don't display a map view in this application, in order to access any
                * services that HERE Android SDK provides, the MapEngine must be initialized as the
                * prerequisite.
                */
               MapEngine.getInstance().init(new ApplicationContext(m_activity), new OnEngineInitListener() {
                   @Override
                   public void onEngineInitializationCompleted(Error error) {
                       Toast.makeText(m_activity, "Map Engine initialized with error code:" + error,
                               Toast.LENGTH_SHORT).show();
                       initMap = true;
                   }
               });
           }
       }
       else
       {
           boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                   mapActivity.getExternalFilesDir(null) + File.separator + ".here-maps",
                   "com.here2k19.projects.smartlastmilecommuter.MapService");
           if (!success) {
               // Setting the isolated disk cache was not successful, please check if the path is valid and
               // ensure that it does not match the default location
               // (getExternalStorageDirectory()/.here-maps).
               // Also, ensure the provided intent name does not match the default intent name.
           } else {
               /*
                * Even though we don't display a map view in this application, in order to access any
                * services that HERE Android SDK provides, the MapEngine must be initialized as the
                * prerequisite.
                */
               MapEngine.getInstance().init(new ApplicationContext(mapActivity), new OnEngineInitListener() {
                   @Override
                   public void onEngineInitializationCompleted(Error error) {
                       Toast.makeText(mapActivity, "Map Engine initialized with error code:" + error,
                               Toast.LENGTH_SHORT).show();
                       initMap = true;
                   }
               });
           }

       }
    }

    private void initUIElements() {
//        m_resultTextView = (TextView) m_activity.findViewById(R.id.geotext);
//        Button geocodeButton = (Button) m_activity.findViewById(R.id.geocode);
//        Button revGeocodeButton = (Button) m_activity.findViewById(R.id.reversegeocode);

//        geocodeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                triggerGeocodeRequest();
//            }
//        });
//
//        revGeocodeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            //    triggerRevGeocodeRequest();
//            }
//        });

    }

    public void triggerGeocodeRequest(String city) {
//        m_resultTextView.setText("");
        /*
         * Create a GeocodeRequest object with the desired query string, then set the search area by
         * providing a GeoCoordinate and radius before executing the request.
         */
        String query = city;
        GeocodeRequest geocodeRequest = new GeocodeRequest(query);
        GeoCoordinate coordinate = new GeoCoordinate(49.266787, -123.056640);
        geocodeRequest.setSearchArea(coordinate, 5000);
        geocodeRequest.execute(new ResultListener<List<GeocodeResult>>() {
            @Override
            public void onCompleted(List<GeocodeResult> results, ErrorCode errorCode) {
                if (errorCode == ErrorCode.NONE) {
                    /*
                     * From the result object, we retrieve the location and its coordinate and
                     * display to the screen. Please refer to HERE Android SDK doc for other
                     * supported APIs.
                     */
                    StringBuilder sb = new StringBuilder();

                    for (GeocodeResult result : results) {
                        sb.append(result.getLocation().getCoordinate().toString());
                        try {
                            latitude=""+result.getLocation().getCoordinate().getLatitude();
                        longitude=""+result.getLocation().getCoordinate().getLongitude();
                       latlangvalue=true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sb.append("\n");
                    }
                    //Log.e("geoo",sb.g);
      //              updateTextView(sb.toString());

                } else {
    //                updateTextView("ERROR:Geocode Request returned error code:" + errorCode);
                }
            }
        });
    }

    public void triggerRevGeocodeRequest(double lat,double lang) {
     //   m_resultTextView.setText("");
        /* Create a ReverseGeocodeRequest object with a GeoCoordinate. */
        GeoCoordinate coordinate = new GeoCoordinate(lat, lang);
        ReverseGeocodeRequest revGecodeRequest = new ReverseGeocodeRequest(coordinate);
        revGecodeRequest.execute(new ResultListener<Location>() {
            @Override
            public void onCompleted(Location location, ErrorCode errorCode) {
                if (errorCode == ErrorCode.NONE) {
                    /*
                     * From the location object, we retrieve the address and display to the screen.
                     * Please refer to HERE Android SDK doc for other supported APIs.
                     */
                //    updateTextView(location.getAddress().getCity().toString());

                    revvalue=location.getAddress().getDistrict();
              //  Log.e("reverse",revvalue);
              //  Log.e("city",location.getAddress().getCity());
               // Log.e("country",location.getAddress().getCounty());
                //Log.e("Street",location.getAddress().getStreet());
                //Log.e("suitenumber",location.getAddress().getSuiteNumberOrName());
                //Log.e("Floor",location.getAddress().getFloorNumber());
                Log.e("district",location.getAddress().getDistrict());
                //Log.e("address",location.getAddress().getText());
                if(mapAct!=true) {
                    progressDialog.dismiss();
                }
                } else {
  //                  updateTextView("ERROR:RevGeocode Request returned error code:" + errorCode);
                }
            }
        });
    }


}