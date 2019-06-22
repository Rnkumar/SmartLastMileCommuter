package com.here2k19.projects.smartlastmilecommuter.Delivery;

public class GeoCoordinates {
    double latitude;
    double longitude;
public GeoCoordinates()
{

}
    public GeoCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
