package com.here2k19.projects.smartlastmilecommuter.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import com.here2k19.projects.smartlastmilecommuter.R;

public class NotificationHelper extends ContextWrapper {
    private static final String channel_id="channel1id";
    private static final String channel_name="channel 1";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createchannels();
        }
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createchannels() {
        NotificationChannel channel=new NotificationChannel(channel_id,channel_name,NotificationManager.IMPORTANCE_DEFAULT);
channel.enableLights(true);
channel.enableVibration(true);
channel.setLightColor(Color.GREEN);
channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
    getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
       if(manager==null) {
           manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       }

        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotification(String title, String body)
    {
        return new Notification.Builder(getApplicationContext(),channel_id)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher_gen)
                .setAutoCancel(false);
    }
}
