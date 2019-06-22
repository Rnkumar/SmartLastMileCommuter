package com.here2k19.projects.smartlastmilecommuter.Routing;



import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoiceGuidanceOptions;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.OnMapRenderListener;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteTta;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Utils;
import com.here2k19.projects.smartlastmilecommuter.activities.MapActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Besides the turn-by-turn navigation example app, This app covers 2 other common use cases:
 * - usage of MapUpdateMode#RoadView during navigation and its interactions with user gestures.
 * - using a MapMarker as position indicator and how to make the movements smooth and
 * synchronized with map movements.
 */
public class AdvancedNavigation extends NavigationManager.NewInstructionEventListener{
    private SupportMapFragment m_mapFragment;
    private Map m_map;
    OrderCallback orderCallback;
public static double deslatitude,deslongitude;
    public static MapMarker m_positionIndicatorFixed = null;
    private PointF m_mapTransformCenter;
    private boolean m_returningToRoadViewMode = false;
    private double m_lastZoomLevelInRoadViewMode = 0.0;
    public static boolean isMarkerClicked=false;
    private FragmentActivity m_activity;
public static NavigationManager navigationManager=null;
RoutePlan getEtaRoutePlan;
TextView time,maneur;
public static List<RouteResult> list1;
ArrayList<Double> timeList;
public static int Speed;
CardView cardView;
public static boolean navigationcheck=false;
Button orders,startbtn,nextbtn;
public static GeoCoordinate currentposition;
    public AdvancedNavigation(FragmentActivity activity) {
            m_activity = activity;
            time=activity.findViewById(R.id.tim);

            cardView=m_activity.findViewById(R.id.instructionscard);
      maneur=m_activity.findViewById(R.id.maneveur);
      maneur.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);
            orders=m_activity.findViewById(R.id.orders);
            nextbtn=m_activity.findViewById(R.id.nextOrder);
            startbtn=m_activity.findViewById(R.id.navigation_btn);
            orders.setVisibility(View.INVISIBLE);
            startbtn.setVisibility(View.INVISIBLE);
            getEtaRoutePlan=new RoutePlan();
            initMapFragment();
            navigationManager=NavigationManager.getInstance();
            list1=new ArrayList<RouteResult>();
            timeList=new ArrayList();

    }
    @Override
    public void onNewInstructionEvent() {
        super.onNewInstructionEvent();

        navigationManager=navigationManager;
        Maneuver maneuver = navigationManager.getNextManeuver();
        if (maneuver != null) {
            if (maneuver.getAction() == Maneuver.Action.END) {
                // notify the user that the route is complete
                Toast.makeText(m_activity,"Route is complete",Toast.LENGTH_LONG);
            }
            TextView textView=m_activity.findViewById(R.id.maneveur);
            Maneuver.Turn turn = maneuver.getTurn();
            String turnName=turn.name();
            int distance = maneuver.getDistanceFromPreviousManeuver();
            String nextRoadName = maneuver.getNextRoadName();
            String data = "Take a "+turnName+" in "+distance+"mts"+" to "+ nextRoadName;
            textView.setText(data);
        }
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initMapFragment() {
        m_mapFragment = getMapFragment();
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                m_activity.getExternalFilesDir(null) + File.separator + ".here-maps",
                "com.here2k19.projects.smartlastmilecommuter.MapService");

        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (m_mapFragment != null) {

                /* Initialize the SupportMapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                        if (error == OnEngineInitListener.Error.NONE) {
                            m_mapFragment.getMapGesture().addOnGestureListener(gestureListener, 100, true);
                            // retrieve a reference of the map from the map fragment
                            navigationcheck=true;
                            m_map = m_mapFragment.getMap();
                            m_map.setZoomLevel(19);
                            m_map.addTransformListener(onTransformListener);


                            PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
//                            final RoutePlan routePlan = new RoutePlan();
//
//                            // these two waypoints cover suburban roads
//                            routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(48.98382, 2.50292)));
//                            routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(48.95602, 2.45939)));

                            try {
                                // calculate a route for navigation
                                CoreRouter coreRouter = new CoreRouter();
                                coreRouter.calculateRoute(MapActivity.routePlanOrder, new CoreRouter.Listener() {
                                    @Override
                                    public void onCalculateRouteFinished(List<RouteResult> list,
                                                                         RoutingError routingError) {
                                        if (routingError == RoutingError.NONE) {
                                     list1=list;
                                            Route route = list.get(0).getRoute();
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            Utils.serializeRoute(route,user.getUid());
                                        int k=MapActivity.routePlanOrder.getWaypointCount();
                                        Log.e("count",""+k);
                                        deslatitude=list.get(0).getRoute().getWaypoints().get(k-1).getLatitude();
                                        deslongitude=list.get(0).getRoute().getWaypoints().get(k-1).getLongitude();

                                            m_map.setCenter(MapActivity.routePlanOrder.getWaypoint(0).getNavigablePosition(),
                                                    Map.Animation.NONE);

                                            // setting MapUpdateMode to RoadView will enable automatic map
                                            // movements and zoom level adjustments
                                            navigationManager.setMapUpdateMode
                                                    (NavigationManager.MapUpdateMode.ROADVIEW);

                                            // adjust tilt to show 3D view
                                            m_map.setTilt(80);

                                            // adjust transform center for navigation experience in portrait
                                            // view
                                            m_mapTransformCenter = new PointF(m_map.getTransformCenter().x, (m_map
                                                    .getTransformCenter().y * 85 / 50));
                                            m_map.setTransformCenter(m_mapTransformCenter);

                                            // create a map marker to show current position
                                            Image icon = new Image();
                                            m_positionIndicatorFixed = new MapMarker();
                                            try {
                                                icon.setImageResource(R.drawable.gps_position);
                                                m_positionIndicatorFixed.setIcon(icon);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            m_positionIndicatorFixed.setVisible(true);
                                            m_positionIndicatorFixed.setCoordinate(m_map.getCenter());
                                            m_map.addMapObject(m_positionIndicatorFixed);

                                            m_mapFragment.getPositionIndicator().setVisible(false);

                                            navigationManager.setMap(m_map);
                                            // listen to real position updates. This is used when RoadView is
                                            // not active.
                                            PositioningManager.getInstance().addListener(
                                                    new WeakReference<PositioningManager.OnPositionChangedListener>(
                                                            mapPositionHandler));

                                            // listen to updates from RoadView which tells you where the map
                                            // center should be situated. This is used when RoadView is active.
                                            navigationManager.getRoadView().addListener(new
                                                    WeakReference<NavigationManager.RoadView.Listener>(roadViewListener));
                                            navigationManager.addPositionListener(
                                                    new WeakReference<NavigationManager.PositionListener>(positionListener));
                                            // start navigation simulation travelling at 13 meters per second
                                            navigationManager.simulate(route,60);

                                            getVoice();

                                        } else {
                                            Toast.makeText(m_activity,
                                                    "Error:route calculation returned error code: " + routingError,
                                                    Toast.LENGTH_LONG).show();

                                        }
                                    }

                                    @Override
                                    public void onProgress(int i) {

                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(m_activity,
                                    "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }


        m_mapFragment.addOnMapRenderListener(new OnMapRenderListener() {
            @Override
            public void onPreDraw() {
                if (m_positionIndicatorFixed != null) {
                    if (navigationManager
                            .getMapUpdateMode().equals(NavigationManager.MapUpdateMode.ROADVIEW)) {
                        if (!m_returningToRoadViewMode) {
                            // when road view is active, we set the position indicator to align
                            // with the current map transform center to synchronize map and map
                            // marker movements.
                            m_positionIndicatorFixed.setCoordinate(m_map.pixelToGeo(m_mapTransformCenter));
                        }
                    }
                }
            }

            @Override
            public void onPostDraw(boolean var1, long var2) {
            }

            @Override
            public void onSizeChanged(int var1, int var2) {
            }

            @Override
            public void onGraphicsDetached() {
            }

            @Override
            public void onRenderBufferCreated() {
            }
        });

    }

    private void getVoice() {
        final VoiceCatalog voiceCatalog=VoiceCatalog.getInstance();
        voiceCatalog.downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
if(error== VoiceCatalog.Error.NONE)
{
Log.e("catalog","downloadSuccessful");
    List<VoicePackage> voicePackages =voiceCatalog.getCatalogList();

    long id = -1;

// select
    for (VoicePackage vPackage : voicePackages) {
        if (vPackage.getMarcCode().compareToIgnoreCase("eng") == 0) {
            if (vPackage.isTts()) {
                id = vPackage.getId();
                break;
            }
        }
    }
    if (!voiceCatalog.isLocalVoiceSkin(id))
    {
        final long finalId = id;
        voiceCatalog.downloadVoice(id, new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
Log.e("voiceskin","voiceskinDownloadsuccess");

            }

        });
    }
    else if(voiceCatalog.isLocalVoiceSkin(id))
    {
        VoiceGuidanceOptions voiceGuidanceOptions = navigationManager.getVoiceGuidanceOptions();
        voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(id));
    }



}
            }
        });
    }
    private NavigationManager.PositionListener positionListener
            = new NavigationManager.PositionListener() {

        @Override
        public void onPositionUpdated(GeoPosition loc) {
            // the position we get in this callback can be used
            // to reposition the map and change orientation.
            loc.getCoordinate();
            loc.getHeading();
            loc.getSpeed();
            //double time=navigationManager.getTta(Route.TrafficPenaltyMode.DISABLED,true);
            //getEta(loc.getCoordinate());
            // also remaining time and distance can be
            // fetched from navigation manager
            navigationManager.getTta(Route.TrafficPenaltyMode.DISABLED, true);
            navigationManager.getDestinationDistance();
        }
    };

    // listen for positioning events
    private PositioningManager.OnPositionChangedListener mapPositionHandler = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod method, final GeoPosition position,
                                      boolean isMapMatched) {
            if (navigationManager.getMapUpdateMode().equals(NavigationManager
                    .MapUpdateMode.NONE) && !m_returningToRoadViewMode)
                // use this updated position when map is not updated by RoadView.
               // GeoCoordinate geoCoordinate=new GeoCoordinate(position.getCoordinate().getLatitude(),position.getCoordinate().getLongitude());
                m_positionIndicatorFixed.setCoordinate(position.getCoordinate());


            if(!isMarkerClicked)
            {
                currentposition=position.getCoordinate();
                Speed= (int) position.getSpeed();
                GeoCoordinate coordinate=new GeoCoordinate(deslatitude,deslongitude);
                getEta(currentposition,coordinate);
            }
            // Log.e("time",""+time);
            onNewInstructionEvent();

        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                         PositioningManager.LocationStatus status) {

        }
    };

    public void getEta(GeoCoordinate position1,GeoCoordinate pos2) {
      //  GeoCoordinate position2=new GeoCoordinate(deslatitude,deslongitude);
        double distance=position1.distanceTo(pos2);
        double speed = 3;
        if(Speed!=0) {
           speed= Speed;
       }
        int time1= (int) (distance/speed);
        time.setText(time1+"mins");

    }
    public String getEtaforBubble(GeoCoordinate position1,GeoCoordinate pos2)
    {
        double distance=position1.distanceTo(pos2);
        double speed = 3;
        if(Speed!=0) {
            speed= Speed;
        }
        int time1= (int) (distance/speed);
        return time1+":"+distance;
    }

    private void pauseRoadView() {
        // pause RoadView so that map will stop moving, the map marker will use updates from
        // PositionManager callback to update its position.

        if (navigationManager.getMapUpdateMode().equals(NavigationManager.MapUpdateMode.ROADVIEW)) {
            navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
            navigationManager.getRoadView().removeListener(roadViewListener);
            m_lastZoomLevelInRoadViewMode = m_map.getZoomLevel();
        }
    }

    private void resumeRoadView() {
        // move map back to it's current position.
        m_map.setCenter(PositioningManager.getInstance().getPosition().getCoordinate(), Map
                        .Animation.BOW, m_lastZoomLevelInRoadViewMode, Map.MOVE_PRESERVE_ORIENTATION,
                80);
        // do not start RoadView and its listener until the map movement is complete.
        m_returningToRoadViewMode = true;
    }

    // application design suggestion: pause roadview when user gesture is detected.
    private MapGesture.OnGestureListener gestureListener = new MapGesture.OnGestureListener() {
        @Override
        public void onPanStart() {
            pauseRoadView();
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
        public boolean onMapObjectsSelected(List<ViewObject> objects) {
            return false;
        }

        @Override
        public boolean onTapEvent(PointF p) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF p) {
            return false;
        }

        @Override
        public void onPinchLocked() {
        }

        @Override
        public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
            pauseRoadView();
            return false;
        }

        @Override
        public void onRotateLocked() {
        }

        @Override
        public boolean onRotateEvent(float rotateAngle) {
            return false;
        }

        @Override
        public boolean onTiltEvent(float angle) {
            pauseRoadView();
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF p) {
            return false;
        }

        @Override
        public void onLongPressRelease() {
        }

        @Override
        public boolean onTwoFingerTapEvent(PointF p) {
            return false;
        }
    };

    final private NavigationManager.RoadView.Listener roadViewListener = new NavigationManager.RoadView.Listener() {
        @Override
        public void onPositionChanged(GeoCoordinate geoCoordinate) {
            // an active RoadView provides coordinates that is the map transform center of it's
            // movements.
            m_mapTransformCenter = m_map.projectToPixel
                    (geoCoordinate).getResult();
        }
    };

    final private Map.OnTransformListener onTransformListener = new Map.OnTransformListener() {
        @Override
        public void onMapTransformStart() {
        }

        @Override
        public void onMapTransformEnd(MapState mapsState) {
            // do not start RoadView and its listener until moving map to current position has
            // completed
            if (m_returningToRoadViewMode) {
                navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode
                        .ROADVIEW);
                navigationManager.getRoadView().addListener(new
                        WeakReference<NavigationManager.RoadView.Listener>(roadViewListener));
                m_returningToRoadViewMode = false;
            }
        }

    };

    public void onDestroy() {
        m_map.removeMapObject(m_positionIndicatorFixed);
        navigationManager.stop();
        PositioningManager.getInstance().stop();
    }

    public void onBackPressed() {
        if (navigationManager.getMapUpdateMode().equals(NavigationManager
                .MapUpdateMode.NONE)) {
            resumeRoadView();
        } else {
            m_activity.finish();
        }
    }
}
