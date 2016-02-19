package com.capstone.innovationproject.routeguidance;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SelectDestination extends AppCompatActivity implements AsyncResponse {
    ArrayList<Row> stops = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);
        updateData();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


    }

    public void processFinish(String output) {
        String key;
        String busStop;


        int i=0;

        try {
            JSONObject jsonRootObject = new JSONObject(output);

            Iterator<String> iter = jsonRootObject.keys();
            while (iter.hasNext()) {
                key = iter.next();
                JSONObject jsonBusStop = jsonRootObject.optJSONObject(key);
                busStop = jsonBusStop.optString("stop_name");
                stops.add(new Row(key, busStop));
            }
        } catch (JSONException e) {
            e.printStackTrace();}
        Collections.sort(stops, new Comparator<Row>() {
            public int compare(Row v1, Row v2) {
                return v1.getStopName().compareTo(v2.getStopName());
            }
        });
    }

    public class Row {
        public String stopNumber;
        public String stopName;

        public Row(String stopNumber, String stopName) {
            this.stopNumber = stopNumber;
            this.stopName = stopName;

        }

        public String getStopNumber() {
            return stopNumber;
        }

        public String getStopName() { return stopName; }

        /*@Override
        public int compareTo(Row compareStops) {
            String compareStopNames=((Row)compareStops).getStopName();
            return this.stopName-compareStopNames;
            //return o1.get(0).compareTo(o2.get(0));
        }*/
    }

    public void updateData() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    GetData data = new GetData(new URL("http://data.foli.fi/siri/sm"));
                    data.delegate = SelectDestination.this;
                    data.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, 0, 10000);
    }

}
