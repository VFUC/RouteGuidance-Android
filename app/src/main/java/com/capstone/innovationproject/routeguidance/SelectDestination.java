package com.capstone.innovationproject.routeguidance;

import android.location.Location;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SelectDestination extends AppCompatActivity implements AsyncResponse {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);
        updateData();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


    }

    public void processFinish(String output) {

        //Tänne koodi millä luetaan data (output)

    }

    public void updateData() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    GetData data = new GetData(new URL("http://data.foli.fi/doc/siri/v0/sm"));
                    data.delegate = SelectDestination.this;
                    data.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, 10000);
    }

}
