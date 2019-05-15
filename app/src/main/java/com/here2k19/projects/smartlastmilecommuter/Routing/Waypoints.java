package com.here2k19.projects.smartlastmilecommuter.Routing;
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

import java.util.ArrayList;

public class Waypoints {
//Waypoints waypoints=this;
  private static String response1="df";

    public Waypoints(Context context)
{

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

    public void getWaypoints(ArrayList<GeoCoordinate> latLngList, Context context)
    {
       // Context context = ;
        String app_id=context.getResources().getString(R.string.app_id);
        String app_code=context.getResources().getString(R.string.app_name);
        String url="https://wse.api.here.com/2/findsequence.json\n" +
                "?start=+"+latLngList.get(0)+"\n" +
                "&destination1="+latLngList.get(1)+"\n" +
                "&destination2="+latLngList.get(2)+"\n" +
                "&end="+latLngList.get(((latLngList.size())-1))+"\n" +
                "&mode=fastest;car\n" +
                "&app_id="+app_id +
                "&app_code="+app_code;
        RequestQueue requestQueue= Volley.newRequestQueue(context);
        JsonObjectRequest jsonArrayRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("response",response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("err",""+error);
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
}
