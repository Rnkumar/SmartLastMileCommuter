package com.here2k19.projects.smartlastmilecommuter.Routing;

import android.support.v7.app.AppCompatActivity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.OnMapRenderListener;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.activities.MapActivity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Besides the turn-by-turn navigation example app, This app covers 2 other common use cases:
 * - usage of MapUpdateMode#RoadView during navigation and its interactions with user gestures.
 * - using a MapMarker as position indicator and how to make the movements smooth and
 * synchronized with map movements.
 */
public class MapFragmentView1 {
    private SupportMapFragment m_mapFragment;
    private Map m_map;

    private MapMarker m_positionIndicatorFixed = null;
    private PointF m_mapTransformCenter;
    private boolean m_returningToRoadViewMode = false;
    private double m_lastZoomLevelInRoadViewMode = 0.0;
    private MapActivity m_activity;

    public MapFragmentView1(MapActivity activity) {
        m_activity = activity;
        initMapFragment();
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
                            m_map = m_mapFragment.getMap();
                            m_map.setZoomLevel(19);
                            m_map.addTransformListener(onTransformListener);

                            PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
                            final RoutePlan routePlan = new RoutePlan();

                            // these two waypoints cover suburban roads
                            routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(Positioning.latitude,Positioning.longitude)));
                            routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(MapActivity.adminloc)));

                            try {
                                // calculate a route for navigation
                                CoreRouter coreRouter = new CoreRouter();
                                coreRouter.calculateRoute(routePlan, new CoreRouter.Listener() {
                                    @Override
                                    public void onCalculateRouteFinished(List<RouteResult> list,
                                                                         RoutingError routingError) {
                                        if (routingError == RoutingError.NONE) {
                                            Route route = list.get(0).getRoute();

                                            // move the map to the first waypoint which is starting point of
                                            // the route
                                            m_map.setCenter(routePlan.getWaypoint(0).getNavigablePosition(),
                                                    Map.Animation.NONE);

                                            // setting MapUpdateMode to RoadView will enable automatic map
                                            // movements and zoom level adjustments
                                            NavigationManager.getInstance().setMapUpdateMode
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

                                            NavigationManager.getInstance().setMap(m_map);

                                            // listen to real position updates. This is used when RoadView is
                                            // not active.
                                            PositioningManager.getInstance().addListener(
                                                    new WeakReference<PositioningManager.OnPositionChangedListener>(
                                                            mapPositionHandler));

                                            // listen to updates from RoadView which tells you where the map
                                            // center should be situated. This is used when RoadView is active.
                                            NavigationManager.getInstance().getRoadView().addListener(new
                                                    WeakReference<NavigationManager.RoadView.Listener>(roadViewListener));

                                            // start navigation simulation travelling at 13 meters per second
                                            NavigationManager.getInstance().simulate(route, 13);

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
                    if (NavigationManager.getInstance()
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

    // listen for positioning events
    private PositioningManager.OnPositionChangedListener mapPositionHandler = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position,
                                      boolean isMapMatched) {
            if (NavigationManager.getInstance().getMapUpdateMode().equals(NavigationManager
                    .MapUpdateMode.NONE) && !m_returningToRoadViewMode)
                // use this updated position when map is not updated by RoadView.
                m_positionIndicatorFixed.setCoordinate(position.getCoordinate());
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                         PositioningManager.LocationStatus status) {

        }
    };

    private void pauseRoadView() {
        // pause RoadView so that map will stop moving, the map marker will use updates from
        // PositionManager callback to update its position.

        if (NavigationManager.getInstance().getMapUpdateMode().equals(NavigationManager.MapUpdateMode.ROADVIEW)) {
            NavigationManager.getInstance().setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
            NavigationManager.getInstance().getRoadView().removeListener(roadViewListener);
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
                NavigationManager.getInstance().setMapUpdateMode(NavigationManager.MapUpdateMode
                        .ROADVIEW);
                NavigationManager.getInstance().getRoadView().addListener(new
                        WeakReference<NavigationManager.RoadView.Listener>(roadViewListener));
                m_returningToRoadViewMode = false;
            }
        }

    };

    public void onDestroy() {
        m_map.removeMapObject(m_positionIndicatorFixed);
        NavigationManager.getInstance().stop();
        PositioningManager.getInstance().stop();
    }

    public void onBackPressed() {
        if (NavigationManager.getInstance().getMapUpdateMode().equals(NavigationManager
                .MapUpdateMode.NONE)) {
            resumeRoadView();
        } else {
            m_activity.finish();
        }
    }
}