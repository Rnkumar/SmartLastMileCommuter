package com.here2k19.projects.smartlastmilecommuter.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here2k19.projects.smartlastmilecommuter.Adapter.MapFragmentView;
import com.here2k19.projects.smartlastmilecommuter.Delivery.GetDeliveries;
import com.here2k19.projects.smartlastmilecommuter.Delivery.SubOrdersModel;
import com.here2k19.projects.smartlastmilecommuter.Geocoding.MainView;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Routing.MapFragmentView1;
import com.here2k19.projects.smartlastmilecommuter.Routing.Positioning;
import com.here2k19.projects.smartlastmilecommuter.Routing.Waypoints;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements CoreRouter.Listener {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    MapRoute Ordersroute ;
    AppCompatActivity appCompatActivity;
    List<SubOrdersModel> productsList = GetDeliveries.staticProducstsList;

    boolean getalert=false;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    boolean value,value1=false;

    public static String vehicle="bike";
    private Map map = null;
    MapRoute adminlocroute;

    private SupportMapFragment mapFragment = null;
    CoreRouter coreRouter;
    double adminlat,adminlang;
    int count=0;
    public static GeoCoordinate currentloc,adminloc;
    double currentloclat,currentloclang;
    Button orders;
    public static double nearbyvalue;
    ArrayList<GeoCoordinate> nearbycentres;
    ArrayList<GeoCoordinate> listOfValues;
    ArrayList<GeoCoordinate> orderlocation;
    private MapMarker ordersMarker;
    private MapFragmentView m_mapFragmentView;
    Button bt;
    TextView time;
    @Override
    public void onDestroy() {
        m_mapFragmentView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        adminloc=GetDeliveries.adminLoc;
        setupMapFragmentView();
        getAlert();



        nearbycentres= new ArrayList<>();
        listOfValues= new ArrayList<>();
        nearbycentres.add(new GeoCoordinate(12.9716,80.0434));
        nearbycentres.add(new GeoCoordinate(13.0500,80.2121));
        nearbycentres.add(new GeoCoordinate(13.0382,80.1565));
        orders=findViewById(R.id.orders);
        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrders();
                if(orderlocation!=null)
                {
                    Waypoints waypoints=new Waypoints();
                    waypoints.getWaypoints(orderlocation,MapActivity.this);
                }

            }
        });
        appCompatActivity=new AppCompatActivity();

            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(MainView.latitude!=null && MainView.longitude!=null)
                    {
            if(count==0) {
                adminlat = Double.parseDouble(MainView.latitude);
                adminlang = Double.parseDouble(MainView.longitude);
                currentloclat= Double.parseDouble(String.valueOf(Positioning.latitude));
                currentloclang= Double.parseDouble(String.valueOf(Positioning.longitude));
            if(map!=null)
            {
                getAlert();
               Handler handler1=new Handler();
               handler1.postDelayed(new Runnable() {
                   @Override
                   public void run() {
                       if(getalert) {
                           ArrayList<MapObject> markers = new ArrayList<MapObject>();
                           markers.add(new MapMarker(new GeoCoordinate(currentloclat, currentloclang)).setTitle("ahii").setDescription("dfdf"));
                           markers.add(new MapMarker(new GeoCoordinate(adminlat, adminlang)).setTitle(adminloc.getLatitude()+","+adminloc.getLongitude()));
                           map.addMapObjects(markers);

                           map.setCenter(new GeoCoordinate(currentloclat, currentloclang, 0.0),
                                   Map.Animation.NONE);
                       }
                   }
               },5000);


            }
                currentloc=new GeoCoordinate(currentloclat,currentloclang);
                drawRoute(adminloc,currentloc);
                count++;
            }
                    }
                }
            },2000);
    }

    private void getAlert() {
        LayoutInflater li = LayoutInflater.from(MapActivity.this);
        View promptsView = li.inflate(R.layout.custom, null);

        final ImageButton bike=promptsView.findViewById(R.id.bike);
        final ImageButton car=promptsView.findViewById(R.id.car);
        bike.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
              value=true;
              value1=false;
             bike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cf1020")));
                car.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#537CFF")));
            }
        });
        car.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                value=false;
                value1=true;
                bike.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#537CFF")));
                car.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#cf1020")));
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MapActivity.this);

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
             if(value){
                 vehicle="bike";
                 getalert=true;
             }else{
                 vehicle="car";
             getalert=true;
             }
             Log.e("vehicle",vehicle);
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void getOrders() {
       orderlocation= new ArrayList<>();
       orderlocation.add(adminloc);
       for(SubOrdersModel subOrdersModel:productsList){
           String[] ordersLocation = subOrdersModel.getLocation().split(",");
           orderlocation.add(new GeoCoordinate(Double.parseDouble(ordersLocation[0]),Double.parseDouble(ordersLocation[1].trim())));
       }
       drawRouteForOrder(orderlocation);
    }
    public  SupportMapFragment getMapFragment() {
    Log.e("cll","called");
        return (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }
    private void setupMapFragmentView() {
        // All permission requests are being handled. Create map fragment view. Please note
        // the HERE SDK requires all permissions defined above to operate properly.
        m_mapFragmentView = new MapFragmentView(MapActivity.this);
    }
    private void initialize() {
        setContentView(R.layout.activity_map);
        bt=findViewById(R.id.navigation_btn);
        time=findViewById(R.id.tim);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragmentView1 mapFragmentView1=new MapFragmentView1(MapActivity.this);
            }
        });
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
  //                                              popup(window_marker.getCoordinate());
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

/*public void nearby(GeoCoordinate first,ArrayList<GeoCoordinate> list)
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
}*/
    /*private void popup(GeoCoordinate title) {
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
*/


    private void drawRoute(GeoCoordinate adminloc, GeoCoordinate currentloc) {
        coreRouter=new CoreRouter();
        RoutePlan routePlan=new RoutePlan();
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(adminloc)));
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(currentloc)));
        RouteOptions routeOptions = new RouteOptions();
        if(vehicle.equals("bike")) {
            routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
            routeOptions.setRouteType(RouteOptions.Type.FASTEST);
            routePlan.setRouteOptions(routeOptions);

        }
        else
        {
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
            routeOptions.setRouteType(RouteOptions.Type.FASTEST);
            routePlan.setRouteOptions(routeOptions);
        }
        coreRouter.calculateRoute(routePlan, new Router.Listener<List<RouteResult>, RoutingError>() {
            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
                if (routingError == RoutingError.NONE) {
                    // Render the route on the map
                    int duration=routeResults.get(0).getRoute().getTtaExcludingTraffic(Route.WHOLE_ROUTE).getDuration();
                    int hours=duration/60;
                    time.setText(hours+"min");
                    if(hours<1)
                    {
                        time.setText(duration+"min");
                    }

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
    if(vehicle.equals("bike")) {
        routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);
    }
    else
    {
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);
    }
    coreRouter.calculateRoute(routePlan, new Router.Listener<List<RouteResult>, RoutingError>() {
        @Override
        public void onProgress(int i) {

        }

        @Override
        public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
            if (routingError == RoutingError.NONE) {
                // Render the route on the map
                int duration=routeResults.get(0).getRoute().getTtaExcludingTraffic(Route.WHOLE_ROUTE).getDuration();
              int hours=duration/60;
              time.setText(hours+"min");
               if(hours<1)
               {
                   time.setText(duration+"min");
               }
                Ordersroute = new MapRoute(routeResults.get(0).getRoute());
                map.addMapObject(Ordersroute);

            }
            else {
                // Display a message indicating route calculation failure
            }
        }
    });
}
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

           //     if(getalert) {
                    initialize();
             //      }

                break;
        }
    }

    @Override
    public void onProgress(int i) {

    }

    @Override
    public void onCalculateRouteFinished(List<RouteResult> list, RoutingError routingError) {
        if (routingError == RoutingError.NONE) {
            MapRoute mapRoute = new MapRoute(list.get(0).getRoute());
            map.addMapObject(mapRoute);
        }
    }
}

