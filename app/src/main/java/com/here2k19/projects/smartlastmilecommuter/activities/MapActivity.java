package com.here2k19.projects.smartlastmilecommuter.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapTrafficLayer;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.TrafficEvent;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteTta;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here2k19.projects.smartlastmilecommuter.Adapter.MapFragmentView;
import com.here2k19.projects.smartlastmilecommuter.Delivery.GetDeliveries;
import com.here2k19.projects.smartlastmilecommuter.Delivery.SubOrdersModel;
import com.here2k19.projects.smartlastmilecommuter.Geocoding.MainView;
import com.here2k19.projects.smartlastmilecommuter.Notification.SendNotification;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Routing.AdvancedNavigation;
import com.here2k19.projects.smartlastmilecommuter.Routing.Positioning;
import com.here2k19.projects.smartlastmilecommuter.Routing.WaypointListener;
import com.here2k19.projects.smartlastmilecommuter.Routing.Waypoints;
import com.here2k19.projects.smartlastmilecommuter.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements CoreRouter.Listener {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    MapRoute ordersRoute;
    AppCompatActivity appCompatActivity;
    List<SubOrdersModel> productsList = GetDeliveries.staticProducstsList;

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };
    boolean value,value1=false;
AdvancedNavigation advancedNavigation;
    public static String vehicle="bike";
    private Map map = null;
    MapRoute adminLocationRoute;

    private SupportMapFragment mapFragment = null;
    CoreRouter coreRouter;
public static RoutePlan routePlanOrder;
    public static GeoCoordinate currentloc,adminloc;
    double currentloclat,currentloclang;
    Button orders;
    ArrayList<GeoCoordinate> nearbycentres;
    ArrayList<GeoCoordinate> listOfValues;
    ArrayList<GeoCoordinate> orderlocation;
    private MapMarker ordersMarker;
    public static List<GeoCoordinate> orderlist;
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
//        Positioning positioning = new Positioning();
//        positioning.getPos(MapActivity.this);
        setupMapFragmentView();
        //getAlert();



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
                if(orderlocation!=null){
                    SharedPreferences sharedPreferences = getSharedPreferences("commuter", Context.MODE_PRIVATE);
                    String name = sharedPreferences.getString("name","");
                    String mobile = sharedPreferences.getString("mobile","");
                    //SendNotification.notify(MapActivity.this,"push","Your items will be delivered by "+name+"("+mobile+")today.");
                    Waypoints waypoints=new Waypoints();
                    waypoints.getWaypoints(orderlocation, MapActivity.this, new WaypointListener() {
                        @Override
                        public void waypoints(List<GeoCoordinate> waypoints) {
                            orderlist = orderlocation;
                            drawRouteForOrder(waypoints);
                        }

                        @Override
                        public void waypointsError(String error) {
                            drawRouteForOrder(orderlocation);
                            Log.e("Error","Error:"+error);
                        }
                    });
                }
            }
        });
        appCompatActivity=new AppCompatActivity();


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

        alertDialogBuilder.setTitle("Select mode of Transport:");

        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
             if(value){
                 vehicle="bike";
             }else{
                 vehicle="car";

             }
                                ArrayList<MapObject> markers = new ArrayList<MapObject>();
                                markers.add(new MapMarker(new GeoCoordinate(currentloclat, currentloclang)).setTitle("ahii").setDescription("dfdf"));
                                markers.add(new MapMarker(new GeoCoordinate(adminloc.getLatitude(), adminloc.getLongitude())).setTitle(adminloc.getLatitude()+","+adminloc.getLongitude()));
                                map.addMapObjects(markers);
                                currentloc = new GeoCoordinate(currentloclat,currentloclang);
                                map.setCenter(new GeoCoordinate(currentloclat, currentloclang, 0.0),
                                        Map.Animation.NONE);
                                drawRoute(adminloc,currentloc);


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
           Log.e("Orders",subOrdersModel.getLocation());
           orderlocation.add(new GeoCoordinate(Double.parseDouble(ordersLocation[0]),Double.parseDouble(ordersLocation[1].trim())));
       }
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
                advancedNavigation=new AdvancedNavigation(MapActivity.this);
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
                        MapTrafficLayer traffic = map.getMapTrafficLayer();
// set the minimum displayed traffic level
                        traffic.setDisplayFilter(TrafficEvent.Severity.VERY_HIGH);
                        currentloclat = Double.parseDouble(String.valueOf(Positioning.latitude));
                        currentloclang = Double.parseDouble(String.valueOf(Positioning.longitude));

                        if(currentloclat == 0.0 || currentloclang ==0.0){
                            currentloclat = 13.043228;
                            currentloclang = 77.609438;
                        }
                        Log.e("Current Location: ",currentloclat+","+currentloclang);
                        getAlert();



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
                                                AdvancedNavigation.isMarkerClicked=true;
                                                MapMarker window_marker = ((MapMarker) mapObject);
                                                //AdvancedNavigation advancedNavigation=new AdvancedNavigation(MapActivity.this);
                                               // advancedNavigation.getEta(AdvancedNavigation.currentposition,window_marker.getCoordinate());
                                                String k=advancedNavigation.getEtaforBubble(AdvancedNavigation.currentposition,window_marker.getCoordinate());
                                                View v = getLayoutInflater().inflate(R.layout.markerpopup,null);
                                                final MapOverlay mapOverlay = new MapOverlay(v,window_marker.getCoordinate());
                                                TextView info = v.findViewById(R.id.info);
                                                v.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        map.removeMapOverlay(mapOverlay);
                                                    }
                                                });
                                               String[] split=k.split(":");
                                                info.setText("Time\t:"+split[0]+"mins"+"\nDistance\t:"+split[1]);
                                                map.addMapOverlay(mapOverlay);

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

        m_mapFragmentView.initNaviControlButton(currentloc,adminloc);
        coreRouter=new CoreRouter();
        RoutePlan routePlan=new RoutePlan();
        routePlan.addWaypoint(new RouteWaypoint(adminloc));
        routePlan.addWaypoint(new RouteWaypoint(currentloc));
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
                    RouteTta tt = routeResults.get(0).getRoute().getTta(Route.TrafficPenaltyMode.OPTIMAL,routeResults.get(0).getRoute().getSublegCount()>0&&routeResults.get(0).getRoute().getSublegCount()!=1?1:0);
                    long timeInSeconds = tt.getDuration();
                    long timeInMinutes = timeInSeconds/60;
                    time.append(timeInMinutes+"mins"+"\n");
                    adminLocationRoute = new MapRoute(routeResults.get(0).getRoute());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Utils.serializeRoute(routeResults.get(0).getRoute(),user.getUid());
                    map.addMapObject(adminLocationRoute);
                }
                else {
                    // Display a message indicating route calculation failure
                }
            }
        });
    }

private void drawRouteForOrder(List<GeoCoordinate> arrayList)
{
    if(adminLocationRoute !=null){
        map.removeMapObject(adminLocationRoute);
    }
    orderlist=arrayList;
    coreRouter=new CoreRouter();
     routePlanOrder=new RoutePlan();
    MainView mainView=new MainView(this);
    double lat,lang;

    Log.e("Coordinates::",arrayList.toString());

    for(int i=0;i<arrayList.size();i++) {
        routePlanOrder.addWaypoint(new RouteWaypoint(arrayList.get(i)));
        lat=arrayList.get(i).getLatitude();
        lang=arrayList.get(i).getLongitude();
        mainView.triggerRevGeocodeRequest(lat,lang);
        ordersMarker=new MapMarker(arrayList.get(i)).setTitle(""+i);
        map.addMapObject(ordersMarker);
    }

    map.setCenter(arrayList.get(0), Map.Animation.LINEAR);


    RouteOptions routeOptions = new RouteOptions();
    if(vehicle.equals("bike")) {
        routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlanOrder.setRouteOptions(routeOptions);
    }
    else
    {
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlanOrder.setRouteOptions(routeOptions);
    }
    coreRouter.calculateRoute(routePlanOrder, new Router.Listener<List<RouteResult>, RoutingError>() {
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
                ordersRoute = new MapRoute(routeResults.get(0).getRoute());
                Log.e("orders:",ordersRoute.getRoute().getWaypoints().toString());
                map.addMapObject(ordersRoute);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Utils.serializeRoute(routeResults.get(0).getRoute(),user.getUid());
            }else {
                // Display a message indicatingp route calculation failure
            Log.e("routingerror",""+routingError);
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
            Route route = list.get(0).getRoute();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Utils.serializeRoute(route,user.getUid());
            MapRoute mapRoute = new MapRoute(route);
            map.addMapObject(mapRoute);
        }
    }



}

