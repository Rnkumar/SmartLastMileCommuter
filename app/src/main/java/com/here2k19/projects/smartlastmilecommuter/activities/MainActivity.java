package com.here2k19.projects.smartlastmilecommuter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.here2k19.projects.smartlastmilecommuter.R;
import com.here2k19.projects.smartlastmilecommuter.Routing.Waypoints;

public class MainActivity extends AppCompatActivity {
Waypoints waypoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
