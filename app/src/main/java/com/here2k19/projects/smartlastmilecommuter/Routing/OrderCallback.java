package com.here2k19.projects.smartlastmilecommuter.Routing;

import com.here2k19.projects.smartlastmilecommuter.Delivery.GeoCoordinates;

import java.util.List;

public interface OrderCallback {
     void transfer(List<GeoCoordinates> list);
}
