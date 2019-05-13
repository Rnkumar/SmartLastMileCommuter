package com.here2k19.projects.smartlastmilecommuter;

public class Utils {

    private static final String SHARED_PREFERENCE_NAME = "smartlastmiledriver";
    private static final String PROFILE_KEY = "profile";
    private static final String USER_KEY = "drivers";
    private static final String NAME_KEY = "name";
    private static final String EMAIL_KEY = "email";
    private static final String MOBILE_KEY = "mobile";
    private static final String ADDRESS_KEY = "address";
    private static final String LOCATION_KEY = "location";
    private static final String ORDERS_KEY = "orders";

    public static String getSharedPreferenceName() {
        return SHARED_PREFERENCE_NAME;
    }

    public static String getProfileKey() {
        return PROFILE_KEY;
    }

    public static String getUserKey() {
        return USER_KEY;
    }

    public static String getNameKey() {
        return NAME_KEY;
    }

    public static String getEmailKey() {
        return EMAIL_KEY;
    }

    public static String getMobileKey() {
        return MOBILE_KEY;
    }

    public static String getAddressKey() {
        return ADDRESS_KEY;
    }

    public static String getLocationKey() {
        return LOCATION_KEY;
    }

    public static String getOrdersKey() {
        return ORDERS_KEY;
    }
}
