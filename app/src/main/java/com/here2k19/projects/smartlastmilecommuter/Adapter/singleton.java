package com.here2k19.projects.smartlastmilecommuter.Adapter;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by saravanan on 6/12/2018.
 */

public class singleton {
    private static singleton mins;
    private RequestQueue requestQueue;
    private static Context context;

    public singleton(Context ctx) {
        context=ctx;
        requestQueue=getRequestQueue();
    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        }
        return  requestQueue;
    }
    public static synchronized singleton getInstance(Context ctx)
    {
        if(mins==null)
        {
            mins=new singleton(ctx);
        }
        return mins;
    }
    public <T>void addtoRequestqueue(Request<T> request)
    {
        requestQueue.add(request);
    }

}
