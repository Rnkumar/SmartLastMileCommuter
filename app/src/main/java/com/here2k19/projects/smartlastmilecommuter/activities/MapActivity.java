package com.here2k19.projects.smartlastmilecommuter.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Geocoding.MainView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements CoreRouter.Listener {

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
     MapRoute Ordersroute ;
    AppCompatActivity appCompatActivity;
    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    // map embedded in the map fragment
    private Map map = null;
MapRoute adminlocroute;
    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;
CoreRouter coreRouter;
double adminlat,adminlang;
int count=0;
GeoCoordinate currentloc,adminloc;
double currentloclat,currentloclang;
Button orders;
public static double nearbyvalue;
ArrayList<GeoCoordinate> nearbycentres;
ArrayList<GeoCoordinate> listOfValues;
    ArrayList<GeoCoordinate> orderlocation;
    private MapMarker ordersMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        nearbycentres=new ArrayList<GeoCoordinate>();
      listOfValues=new ArrayList<GeoCoordinate>();
        nearbycentres.add(new GeoCoordinate(12.9716,80.0434));
        nearbycentres.add(new GeoCoordinate(13.0500,80.2121));
        nearbycentres.add(new GeoCoordinate(13.0382,80.1565));
        orders=findViewById(R.id.orders);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOrders();
            }
        });
        appCompatActivity=new AppCompatActivity();
        final String admin_loc=getIntent().getExtras().getString("admin_loc");
        if(admin_loc!=null)
        {
            MainView mainView=new MainView(MapActivity.this);
        mainView.triggerGeocodeRequest(admin_loc);
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(MainView.latitude!=null && MainView.longitude!=null)
                    {
            if(count==0) {
                adminlat = Double.parseDouble(MainView.latitude);
                adminlang = Double.parseDouble(MainView.longitude);
                adminloc=new GeoCoordinate(adminlat,adminlang);
            currentloclat= Double.parseDouble(MainView.currentloclat);
            currentloclang= Double.parseDouble(MainView.currentloclang);
            if(map!=null)
            {
               ArrayList<MapObject> markers=new ArrayList<MapObject>();
               markers.add(new MapMarker(new GeoCoordinate(currentloclat,currentloclang)).setTitle("ahii").setDescription("dfdf"));
               markers.add(new MapMarker(new GeoCoordinate(adminlat,adminlang)).setTitle(admin_loc));
               map.addMapObjects(markers);

                map.setCenter(new GeoCoordinate(currentloclat, currentloclang, 0.0),
                    Map.Animation.NONE);

            }
                currentloc=new GeoCoordinate(currentloclat,currentloclang);
                drawRoute(adminloc,currentloc);
                count++;
            }
                    }
                }
            },2000);

        }
    }

    private void getOrders() {
    orderlocation=new ArrayList<GeoCoordinate>();
    orderlocation.add(new GeoCoordinate(adminloc));
   orderlocation.add(new GeoCoordinate(12.9675,80.1491));
   orderlocation.add(new GeoCoordinate(12.9516,80.1462));
   orderlocation.add(new GeoCoordinate(12.9249,80.1000));
   orderlocation.add(new GeoCoordinate(12.9749,80.1328));
drawRouteForOrder(orderlocation);
    }
    public  SupportMapFragment getMapFragment() {
    Log.e("cll","called");
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initialize() {
        setContentView(R.layout.activity_map);

        // Search for the map fragment to finish setup by calling init().
        mapFragment = getMapFragment();
        // Set up disk cache path for the map service for this application
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "com.here2k19.projects.smartlastmilecommuter.MapService");

        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();
                        // Set the map center to the Vancouver region (no animation)
                        // Set the zoom level to the average between min and max
                        map.setZoomLevel(15);
                        if(mapFragment!=null)
                        {
                            mapFragment.getMapGesture().addOnGestureListener(new MapGesture.OnGestureListener() {
                                @Override
                                public void onPanStart() {

                                }

                                @Override
                                public void onPanEnd() {

                                }

                                @Override
                                public void onMultiFingerManipulationStart() {

                                }

                                @Override
                                public void onMultiFingerManipulationEnd() {

                                }

                                @Override
                                public boolean onMapObjectsSelected(List<ViewObject> list) {
                                    for (ViewObject viewObject : list) {
                                        if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                                            MapObject mapObject = (MapObject) viewObject;

                                            if (mapObject.getType() == MapObject.Type.MARKER) {

                                                MapMarker window_marker = ((MapMarker) mapObject);

                                            //    System.out.println("Title is................."+window_marker.getTitle());
                                                Log.e("Totle",""+window_marker.getCoordinate());
                                                popup(window_marker.getCoordinate());
                                                return true;
                                            }
                                        }
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onTapEvent(PointF pointF) {
                                    return false;
                                }

                                @Override
                                public boolean onDoubleTapEvent(PointF pointF) {
                                    return false;
                                }

                                @Override
                                public void onPinchLocked() {

                                }

                                @Override
                                public boolean onPinchZoomEvent(float v, PointF pointF) {
                                    return false;
                                }

                                @Override
                                public void onRotateLocked() {

                                }

                                @Override
                                public boolean onRotateEvent(float v) {
                                    return false;
                                }

                                @Override
                                public boolean onTiltEvent(float v) {
                                    return false;
                                }

                                @Override
                                public boolean onLongPressEvent(PointF pointF) {
                                    return false;
                                }

                                @Override
                                public void onLongPressRelease() {

                                }

                                @Override
                                public boolean onTwoFingerTapEvent(PointF pointF) {
                                    return false;
                                }
                            },0,false);
                        }

                    } else {
                        System.out.println("ERROR: Cannot initialize Map Fragment");
                    }
                }
            });
        }
    }
public void nearby(GeoCoordinate first,ArrayList<GeoCoordinate> list)
{
    Location location1,location2;
    location1=new Location("value1");
    location2=new Location("value2");
   location1.setLatitude(first.getLatitude());
   location1.setLongitude(first.getLongitude());
   GeoCoordinate geoCoordinate;
    for(int i=0;i<list.size();i++)
    {
        location2.setLatitude(list.get(i).getLatitude());
        location2.setLatitude(list.get(i).getLongitude());
        if(nearbyvalue==0)
        {
            nearbyvalue=location1.distanceTo(location2);
        }
        else
        {
            if(nearbyvalue<location1.distanceTo(location2))
            {
                nearbyvalue=location1.distanceTo(location2);
            geoCoordinate=new GeoCoordinate(location2.getLatitude(),location2.getLongitude());
                listOfValues.add(geoCoordinate);
            Log.e("nearbyvalue",""+nearbyvalue);

            }
        }
    }
}
    private void popup(GeoCoordinate title) {
        LayoutInflater li = LayoutInflater.from(MapActivity.this);
        View promptsView = li.inflate(R.layout.custom, null);
nearby(title,nearbycentres);
for(int i=0;i<listOfValues.size();i++)
{
    Log.e("size",""+listOfValues.size());
 Log.e("valueslist",listOfValues.get(i).toString());
}
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapActivity.this);

         alertDialogBuilder.setView(promptsView);
        // set prompts.xml to alertdialog builder

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                               // result.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }



    private void drawRoute(GeoCoordinate adminloc, GeoCoordinate currentloc) {
        coreRouter=new CoreRouter();
        RoutePlan routePlan=new RoutePlan();
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(adminloc)));
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(currentloc)));
        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);
        coreRouter.calculateRoute(routePlan, new Router.Listener<List<RouteResult>, RoutingError>() {
            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
                if (routingError == RoutingError.NONE) {
                    // Render the route on the map
                    adminlocroute = new MapRoute(routeResults.get(0).getRoute());
                    map.addMapObject(adminlocroute);

                }
                else {
                    // Display a message indicating route calculation failure
                }
            }
        });
    }

private void drawRouteForOrder(ArrayList<GeoCoordinate> arrayList)
{
    if(adminlocroute!=null)
    {
        map.removeMapObject(adminlocroute);
    }
    coreRouter=new CoreRouter();
    RoutePlan routePlan=new RoutePlan();
    MainView mainView=new MainView(this);
    double lat,lang;
for(int i=0;i<arrayList.size();i++)
{
    routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(arrayList.get(i))));
    lat=arrayList.get(i).getLatitude();
    lang=arrayList.get(i).getLongitude();
    mainView.triggerRevGeocodeRequest(lat,lang);
    ordersMarker=new MapMarker(new GeoCoordinate(arrayList.get(i))).setTitle(""+i);
//    Log.e("MapActRev",MainView.revvalue);
    map.addMapObject(ordersMarker);
}


    RouteOptions routeOptions = new RouteOptions();
    routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
    routeOptions.setRouteType(RouteOptions.Type.FASTEST);
    routePlan.setRouteOptions(routeOptions);
    coreRouter.calculateRoute(routePlan, new Router.Listener<List<RouteResult>, RoutingError>() {
        @Override
        public void onProgress(int i) {

        }

        @Override
        public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
            if (routingError == RoutingError.NONE) {
                // Render the route on the map
                Ordersroute = new MapRoute(routeResults.get(0).getRoute());
                map.addMapObject(Ordersroute);

            }
            else {
                // Display a message indicating route calculation failure
            }
        }
    });
}
    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }

    @Override
    public void onProgress(int i) {

    }

    @Override
    public void onCalculateRouteFinished(List<RouteResult> list, RoutingError routingError) {
        if (routingError == RoutingError.NONE) {
            // Render the route on the map
            MapRoute mapRoute = new MapRoute(list.get(0).getRoute());
            map.addMapObject(mapRoute);
        }
    }
}
