package com.capstone.innovationproject.routeguidance;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

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

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);
        updateData();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ListView lv = (ListView)findViewById(R.id.listView);

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        lv.setAdapter(adapter);
    }

    public void processFinish(String output) {
        String key;
        String busStop;
        ListView listview = (ListView) findViewById(R.id.listView);

        int i=0;

        try {
            stops.clear();
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
        listview.setAdapter(new Adapter(this));
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

    class Adapter extends BaseAdapter {
        private Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return 12;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(context);
                //tv.setLayoutParams(new GridView.LayoutParams(300, 400));
            }
            else {
                tv = (TextView) convertView;
            }
            if(!stops.isEmpty()) {
                tv.setText("" + stops.get(position).getStopName() + "\n");
                //tv.setTextSize(20);
                tv.setTextColor(Color.rgb(255, 255, 255));
                //tv.setBackgroundColor(Color.rgb(234, 160, 0));
                //tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            return tv;
        }
    }
}
