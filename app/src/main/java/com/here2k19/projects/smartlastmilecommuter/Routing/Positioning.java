package com.here2k19.projects.smartlastmilecommuter.Routing;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.LocationDataSourceHERE;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here2k19.projects.smartlastmilecommuter.Geocoding.MainView;

import java.io.File;
import java.lang.ref.WeakReference;

public class Positioning implements PositioningManager.OnPositionChangedListener{
    private Activity m_activity;
    private LocationDataSourceHERE m_hereDataSource;
    public static double latitude,longitude;
    private PositioningManager pm;
    private int count=0;

    public void getPos(Activity m_activity) {
        this.m_activity=m_activity;
        initMapEngine();
    }

    private void initMapEngine() {
            boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                    m_activity.getExternalFilesDir(null) + File.separator + ".here-maps",
                    "com.here2k19.projects.smartlastmilecommuter.MapService");
            if (!success) {
                Log.e("Map Load","Failed");
            } else {
                MapEngine.getInstance().init(new ApplicationContext(m_activity.getApplicationContext()), new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {
                        m_hereDataSource = LocationDataSourceHERE.getInstance();
                        if (m_hereDataSource != null) {
                            pm = PositioningManager.getInstance();
                            //pm.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                            pm.setDataSource(m_hereDataSource);
                            pm.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(Positioning.this));
                            if (pm.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)) {
                                Log.e("poso", "position Update Started");
                            } else {
                                Log.e("positioning","failed");
                            }
                        }
                    }
                    });
                }
        }

    @Override
    public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
//            MainView mainView = new MainView(m_activity);
//                if(!(MainView.initMap)) {
//                    count=0;
//                }
//                else {
                    latitude = geoPosition.getCoordinate().getLatitude();
                    longitude = geoPosition.getCoordinate().getLongitude();

 //                   mainView.triggerRevGeocodeRequest(latitude, longitude);
//                    count++;
 //               }
            Log.e("LatLng",""+geoPosition.getCoordinate().getLatitude());
        }

    @Override
    public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

    }
}


