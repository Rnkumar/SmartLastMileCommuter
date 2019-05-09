package com.here2k19.projects.smartlastmilecommuter.activities.Delivery;

public class Deliveries {
    private String item;
    private String Quantity;

    public Deliveries(String item, String quantity) {
        this.item = item;
        Quantity = quantity;
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
