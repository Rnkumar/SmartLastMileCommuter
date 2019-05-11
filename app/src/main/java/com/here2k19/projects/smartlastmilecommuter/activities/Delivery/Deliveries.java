package com.here2k19.projects.smartlastmilecommuter.activities.Delivery;

public class Deliveries {
    private String item;
    private String Quantity;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;

    public Deliveries(String item, String quantity,String location) {
        this.item = item;
        Quantity = quantity;
    this.location=location;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }



}
