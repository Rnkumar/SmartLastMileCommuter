package com.here2k19.projects.smartlastmilecommuter.Delivery;

import java.util.Date;

public class SubOrdersModel {

    private String mobile, Address, ItemName, Location, userId, orderId, driverName, driverMobile, driverId;
    private Date date;
    private boolean published, delivered;
    private int quantity;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public SubOrdersModel(String mobile, String address, String itemName, String location, String userId, String orderId, String driverName, String driverMobile, String driverId, Date date, boolean published, boolean delivered, int quantity) {
        this.mobile = mobile;
        Address = address;
        ItemName = itemName;
        Location = location;
        this.userId = userId;
        this.orderId = orderId;
        this.driverName = driverName;
        this.driverMobile = driverMobile;
        this.driverId = driverId;
        this.date = date;
        this.published = published;
        this.delivered = delivered;
        this.quantity = quantity;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public SubOrdersModel(String mobile, String address, String itemName, String location, String userId, String orderId, String driverName, String driverMobile, Date date, boolean published, boolean delivered, int quantity) {
        this.mobile = mobile;
        Address = address;
        ItemName = itemName;
        Location = location;
        this.userId = userId;
        this.orderId = orderId;
        this.driverName = driverName;
        this.driverMobile = driverMobile;
        this.date = date;
        this.published = published;
        this.delivered = delivered;
        this.quantity = quantity;
    }

    public SubOrdersModel() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public SubOrdersModel(String mobile, String address, String itemName, String location, String userId, boolean published, boolean delivered, int quantity, String orderId, Date date) {
        this.mobile = mobile;
        this.Address = address;
        this.ItemName = itemName;http://localhost:5000/#/CreateItems
        this.Location = location;
        this.userId = userId;
        this.published = published;
        this.delivered = delivered;
        this.quantity = quantity;
        this.orderId = orderId;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return getItemName()+","+getAddress()+","+getDriverId()+","+getDriverMobile()+","+getDriverName()+","+getLocation()+","+getMobile()+","+getOrderId()+","+getUserId()+","+getDate()+","+getQuantity();
    }
}
