package com.here2k19.projects.smartlastmilecommuter.activities;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.here.android.mpa.common.GeoCoordinate;
import com.here2k19.projects.smartlastmilecommuter.R;

import org.json.JSONObject;

public class Geocoding {
   // Context context;
    Context ctx;
    public void geocode(String city,Context context)
{
    ctx=context;
    String app_id=context.getResources().getString(R.string.app_id);
    String app_code=context.getResources().getString(R.string.app_code);
String url="https://geocoder.api.here.com/6.2/geocode.json?" +
        "searchtext="+city +
        "&app_id="+app_id +
        "&app_code="+app_code;
    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
Log.e("ree",response.toString());
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    });
    singleton.getInstance(ctx).addtoRequestqueue(jsonObjectRequest);
}
public void ReverseGeocode(GeoCoordinate geoCoordinate, Context context)
{
    ctx=context;
    String app_id=context.getResources().getString(R.string.app_id);
    String app_code=context.getResources().getString(R.string.app_code);
    String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?"+
            "RevGeo="+geoCoordinate+
            "&app_id="+app_id +
            "&app_code="+app_code;
    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            Log.e("reverse",response.toString());
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
Log.e("errres",""+error);
        }
    });
    singleton.getInstance(ctx).addtoRequestqueue(jsonObjectRequest);
}
}

