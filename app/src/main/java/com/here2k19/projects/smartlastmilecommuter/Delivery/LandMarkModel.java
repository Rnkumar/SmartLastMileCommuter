package com.here2k19.projects.smartlastmilecommuter.Delivery;

public class LandMarkModel {

    public String name,address;
    public boolean selected;
    public GeoCoordinates geoCoordinates;
public LandMarkModel()
{

}
    public GeoCoordinates getGeoCoordinates() {
        return geoCoordinates;
    }

    public void setGeoCoordinates(GeoCoordinates geoCoordinates) {
        this.geoCoordinates = geoCoordinates;
    }

    public LandMarkModel(String name, String address, boolean selected, GeoCoordinates geoCoordinates) {
        this.name = name;
        this.address = address;
        this.selected = selected;
        this.geoCoordinates = geoCoordinates;
    }

    public LandMarkModel(String name, String address, boolean selected) {
        this.name = name;
        this.address = address;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public LandMarkModel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}