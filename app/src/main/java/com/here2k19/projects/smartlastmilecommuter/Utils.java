package com.here2k19.projects.smartlastmilecommuter;

import android.util.Base64;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here.android.mpa.routing.Route;

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
    private static final String DRIVER_KEY = "drivers";

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

    public static String getDriverKey() {
        return DRIVER_KEY;
    }

    public static void serializeRoute(Route route, final String uid){
        Route.serializeAsync(route, new Route.SerializationCallback() {
            @Override
            public void onSerializationComplete(Route.SerializationResult serializationResult) {
                byte[] data = serializationResult.data;
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                Log.e("Base: ",base64);
                FirebaseDatabase.getInstance().getReference(getDriverKey()).child(uid).child("currentRoute").setValue(base64);
            }
        });
    }
}
