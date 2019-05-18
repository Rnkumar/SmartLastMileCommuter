package com.here2k19.projects.smartlastmilecommuter.Routing;

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
AppCompatActivity m_activity;
LocationDataSourceHERE m_hereDataSource;
public static double latitude,longitude;
   int count=0;
    public void getPos(AppCompatActivity m_activity)
{
    this.m_activity=m_activity;
    initMapEngine();
}

    private void initMapEngine() {
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
                        Toast.makeText(m_activity, "Hello Map Engine initialized with error code:" + error,
                                Toast.LENGTH_SHORT).show();
                        m_hereDataSource = LocationDataSourceHERE.getInstance();
                        if (m_hereDataSource != null) {
//        if (MapEngine.isInitialized()) {
                            PositioningManager pm = PositioningManager.getInstance();
                            pm.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR);
                            pm.setDataSource(m_hereDataSource);
                            pm.addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(Positioning.this));
                            if (pm.start(PositioningManager.LocationMethod.GPS_NETWORK_INDOOR)) {
                                // Position updates started successfully.
                                Toast.makeText(m_activity, "Position updates started success", Toast.LENGTH_LONG).show();
                                Log.e("poso", "pos");
                            } else {

                            }
                            //      }
                            //                initMap = true;
                        }

                    }
                    });
                }
        }

    @Override
    public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
if(count==0) {
    MainView mainView = new MainView(m_activity);
    if(!(MainView.initMap)) {
    count=0;
    }
    else {
        latitude = geoPosition.getCoordinate().getLatitude();
        longitude = geoPosition.getCoordinate().getLongitude();

        mainView.triggerRevGeocodeRequest(latitude, longitude);
        count++;
    }
    }
        Log.e("lat",""+geoPosition.getCoordinate().getLatitude());
    }

    @Override
    public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

    }
}


