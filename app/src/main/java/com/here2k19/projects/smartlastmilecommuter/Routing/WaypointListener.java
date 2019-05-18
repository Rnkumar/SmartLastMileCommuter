package com.here2k19.projects.smartlastmilecommuter.Routing;



import com.here.android.mpa.common.GeoCoordinate;

import java.util.List;

public interface WaypointListener {
    void waypoints(List<GeoCoordinate> waypoints);
    void waypointsError(String error);
}
