package com.here2k19.projects.smartlastmilecommuter.Routing;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.here.android.mpa.common.GeoCoordinate;
import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.activities.MapActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WaypointWakeup {
    private static String response1="df";
    public static List<GeoCoordinate> list;
    private WaypointListener waypointListener;

    public void getWaypointOkhttp(ArrayList<GeoCoordinate> latLngList, Context context, final WaypointListener waypointListener)
    {
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
        if(2==2){
            waypointListener.waypointsError("error");
            return;
        }
        Log.e("waypointWakeup",url);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String responseString=runUrl(url);
                            Log.e("responsestring",responseString);
                            ((MapActivity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject response = new JSONObject(responseString);
                                        JSONArray jsonArray = response.getJSONArray("results").getJSONObject(0).getJSONArray("waypoints");
                                        list = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            Log.e("enter","enter");
                                            JSONObject waypoint = jsonArray.getJSONObject(i);
                                            GeoCoordinate geoCoordinate = new GeoCoordinate(waypoint.getDouble("lat"), waypoint.getDouble("lng"));
                                            list.add(geoCoordinate);
                                        }
                                        waypointListener.waypoints(list);
                                    }catch (JSONException e){
                                        waypointListener.waypointsError(e.getMessage());
                                    }
                                }
                            });
                        }catch (IOException e) {
                            e.printStackTrace();
                            waypointListener.waypointsError(e.getMessage());
                        }
                    }
                }).start();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String runUrl(String url) throws IOException {

        Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        OkHttpClient client=new OkHttpClient();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
