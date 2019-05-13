package com.here2k19.projects.smartlastmilecommuter.Adapter;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExampleJobService extends JobService {
    public static final String TAG="ExampleJobService";
    private boolean jobcancelled=false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.e(TAG,"JobStarted");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            int i=0;
           while(true)
           {

                if(jobcancelled)
                {
                    return;
                }
                    try
                {
Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();

                }
            }
       //     Log.e(TAG,"JobFinished");
         //   jobFinished(params,false);
        }
    }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.e(TAG,"Job cancelled before completion");
        jobcancelled=true;
        return true;
    }
}
