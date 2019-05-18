package com.here2k19.projects.smartlastmilecommuter.Notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class SendNotification {

    public static void notify(final Context context,final String userId,final String location){
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();
        Map<String,Object> data = new HashMap<>();
        data.put("userId",userId);
        data.put("location",location);
        mFunctions.getHttpsCallable("addMessage").call(data).continueWith(new Continuation<HttpsCallableResult, Object>() {
            @Override
            public Object then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result;
                if (task.isSuccessful()) {
                    result = task.getResult().getData().toString();
                }

                else {
                    Log.e("Error",task.getException().getMessage());
                    result = "Failed";
                }

                Toast.makeText(context, "Result:"+result, Toast.LENGTH_SHORT).show();
                return result;
            }
        });
    }

}
