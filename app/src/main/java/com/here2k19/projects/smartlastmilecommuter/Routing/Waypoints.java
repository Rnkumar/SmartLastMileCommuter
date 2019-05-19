package com.here2k19.projects.smartlastmilecommuter.Routing;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.here.android.mpa.common.GeoCoordinate;
import com.here2k19.projects.smartlastmilecommuter.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Waypoints {
  private static String response1="df";
   public static List<GeoCoordinate> list;
  private WaypointListener waypointListener;

    public Waypoints(Context context) {
        String app_id=context.getResources().getString(R.string.app_id);
        String app_code=context.getResources().getString(R.string.app_code);
        String url="https://wse.api.here.com/2/findsequence.json\n" +
                "?start=Berlin-Main-Station;52.52282,13.37011\n" +
                "&destination1=East-Side-Gallery;52.50341,13.44429\n" +
                "&destination2=Olympiastadion;52.51293,13.24021\n" +
                "&end=HERE-Berlin-Campus;52.53066,13.38511\n" +
                "&mode=fastest;car\n" +
                "&app_id="+app_id +
                "&app_code="+app_code;
        Log.e("WaypointUrl:",url);
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        JsonObjectRequest jsonArrayRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            response1=response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Log.e("err",error.getMessage());
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public Waypoints(){

    }

    public void getWaypoints(ArrayList<GeoCoordinate> latLngList, Context context, final WaypointListener waypointListener) {
        this.waypointListener = waypointListener;
        String app_id=context.getResources().getString(R.string.app_id);
        String app_code=context.getResources().getString(R.string.app_code);

        String baseUrl = "https://wse.api.here.com/2/findsequence.json";
        String startUrl = "?start="+latLngList.get(0).getLatitude()+","+latLngList.get(0).getLongitude()+"&";
        StringBuilder coordinates= new StringBuilder();
        for(int i=1;i<latLngList.size()-1;i++){
            GeoCoordinate coordinate = latLngList.get(i);
            coordinates.append("destination").append(i).append("=").append(coordinate.getLatitude()).append(",").append(coordinate.getLongitude()).append("&");
        }
        GeoCoordinate endCoordinate = latLngList.get(latLngList.size()-1);
        String endUrl = "end="+endCoordinate.getLatitude()+","+endCoordinate.getLongitude();
        String extras="&mode=fastest;car&app_id="+app_id+"&app_code="+app_code;
        String url = baseUrl+startUrl+coordinates+endUrl+extras;
        Log.e("WaypointUrl:",url);
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        JsonObjectRequest jsonArrayRequest=new JsonObjectRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response",response.toString());
                try {
                    JSONArray jsonArray = response.getJSONArray("results").getJSONObject(0).getJSONArray("waypoints");
                    list= new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject waypoint = jsonArray.getJSONObject(i);
                        GeoCoordinate geoCoordinate = new GeoCoordinate(waypoint.getDouble("lat"),waypoint.getDouble("lng"));
                        list.add(geoCoordinate);
                    }
                    waypointListener.waypoints(list);
                } catch (JSONException e) {
                    waypointListener.waypointsError(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("err",""+error);
                waypointListener.waypointsError(error+"");
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(10000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }
}
