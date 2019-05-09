package com.here2k19.projects.smartlastmilecommuter.activities.Notification;

import android.content.Context;
import android.os.Build;

public class Notification {
    NotificationHelper helper;
    android.app.Notification.Builder builder;
    public void getNotification(String title, String body, Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            helper=new NotificationHelper(context);
        }
        builder=helper.getNotification("Hello","This is notification");
        helper.getManager().notify(1,builder.build());

    }
}
