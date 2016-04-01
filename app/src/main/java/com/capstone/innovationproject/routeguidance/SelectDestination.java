package com.capstone.innovationproject.routeguidance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SelectDestination extends AppCompatActivity implements AsyncResponse {
    private static final String TAG = SelectDestination.class.getSimpleName();
    ArrayList<Row> stops = new ArrayList<>();
    ArrayList<String> stopsS = new ArrayList<>();

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private String stoppi;
    private String busId = "", blockref="", busNumber="";
    public int stopcount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            busId = bundle.getString("id");
            blockref = bundle.getString("blockref");
            busNumber = bundle.getString("busNumber");
        }
        //getRoute();

        //updateData();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        ListView lv = (ListView)findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //Toast.makeText(SelectBus.this, "" + position,
                //        Toast.LENGTH_SHORT).show();
                stoppi = stops.get(position).toString();
                Intent i = new Intent(SelectDestination.this, NextStop.class);
                Bundle bundle = new Bundle();
                bundle.putString("stopname", stoppi);
                bundle.putString("id", busId);
                bundle.putString("busNumber", busNumber);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
        //getRoute();
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        lv.setAdapter(adapter);
        getRoute();
    }

    public void processFinish(String output) {
        String key;
        String busStop;
        ListView listview = (ListView) findViewById(R.id.listView);

        int i=0;

        try {
            stops.clear();
            stopsS.clear();
            JSONObject jsonRootObject = new JSONObject(output);

            Iterator<String> iter = jsonRootObject.keys();
            while (iter.hasNext()) {
                key = iter.next();
                JSONObject jsonBusStop = jsonRootObject.optJSONObject(key);
                busStop = jsonBusStop.optString("stop_name");
                stops.add   (new Row(key, busStop));
                stopsS.add(busStop);
            }
        } catch (JSONException e) {
            e.printStackTrace();}
        Collections.sort(stops, new Comparator<Row>() {
            public int compare(Row v1, Row v2) {
                return v1.getStopName().compareTo(v2.getStopName());
            }
        });
        Collections.sort(stops, new Comparator<Row>() {
            public int compare(Row v1, Row v2) {
                return v1.getStopName().compareTo(v2.getStopName());
            }
        });

        Set<String> hs = new HashSet<>();
        hs.addAll(stopsS);
        stopsS.clear();
        stopsS.addAll(hs);
        Collections.sort(stopsS);
        stopcount = stopsS.size();
        listview.setAdapter(new Adapter(this));
    }

    public String getRoute() {
        URL url;
        ListView listview = (ListView) findViewById(R.id.listView);
        String latest, route_id="", trip_id="", stopsxml="";
        StringBuilder sb = new StringBuilder();
        String result = new String();

        try {
            //latest version
            url = new URL("http://data.foli.fi/gtfs");
            Log.d(TAG, url.toString());
            result = new loadXml(url).execute().get();
            Log.d(TAG, "XML downloaded");
            Log.d(TAG, result);
            JSONObject jsonRootObject = new JSONObject(result.toString());
            latest = jsonRootObject.getString("latest");
            Log.d(TAG, "latest = " + latest);

            //get route_id with short_name (bus number)
            sb.append("http://data.foli.fi/gtfs/v0/"); sb.append(latest); sb.append("/routes");
            url = new URL(sb.toString());
            Log.d(TAG, url.toString());
            result = new loadXml(url).execute().get();
            Log.d(TAG, "XML downloaded");
            Log.d(TAG, result);

            Log.d(TAG, "Id = " + busId);
            Log.d(TAG, "busNumber = " + busNumber);

            JSONArray jsonArray = new JSONArray(result.toString());
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject e = jsonArray.getJSONObject(i);
                if(e.getString("route_short_name").equals(busNumber)) route_id=e.getString("route_id");
            }
            Log.d(TAG, "route_id = " + route_id);

            sb.setLength(0); //clearing stringbuilder so we can use it again
            sb.append("http://data.foli.fi/gtfs/v0/"); sb.append(latest); sb.append("/trips/route/"); sb.append(route_id);
            url = new URL(sb.toString());
            Log.d(TAG, url.toString());
            result = new loadXml(url).execute().get();
            Log.d(TAG, "XML downloaded");
            Log.d(TAG, result);

            //search blockid and save tripid
            Log.d(TAG, "block_id = " + blockref);
            jsonArray = new JSONArray(result.toString());
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject e = jsonArray.getJSONObject(i);
                if(e.getString("block_id").equals(blockref)) trip_id=e.getString("trip_id");
            }
            Log.d(TAG, "trip_id = " + trip_id);

            sb.setLength(0);
            sb.append("http://data.foli.fi/gtfs/v0/"); sb.append(latest); sb.append("/stop_times/trip/"); sb.append(trip_id);
            url = new URL(sb.toString());
            Log.d(TAG, url.toString());
            result = new loadXml(url).execute().get();
            Log.d(TAG, "XML downloaded");
            Log.d(TAG, result);

            url = new URL("http://data.foli.fi/siri/sm");
            Log.d(TAG, url.toString());
            stopsxml = new loadXml(url).execute().get();
            Log.d(TAG, "XML downloaded");
            Log.d(TAG, stopsxml);
            jsonArray = new JSONArray(result.toString());

            JSONObject jsonObject = new JSONObject(stopsxml);

            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject e = jsonArray.getJSONObject(i);
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    JSONObject jsonNode = jsonObject.getJSONObject(key);
                    if(key.toString().equals(e.getString("stop_id"))) stopsS.add(jsonNode.optString("stop_name"));
                }

                Set<String> hs = new HashSet<>();
                hs.addAll(stopsS);
                stopsS.clear();
                stopsS.addAll(hs);

                //stops.add(new Row(e.getString("stop_id"), e.getString("stop_id")));
                //stopsS.add(e.getString("stop_id"));
                Log.d(TAG, e.getString("stop_id"));
            }
            stopcount = stopsS.size();
            listview.setAdapter(new Adapter(this));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return "result";
    }

    public class loadXml extends AsyncTask<String, String, String> {
        public String result = null;
        public AsyncResponse delegate = null;
        HttpURLConnection urlConnection;
        public URL url;

        public loadXml(URL urli) {
            url = urli;
        }

        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return result.toString();
        }
        protected void onPostExecute(String result) {
            //delegate.processFinish(result);

        }
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
    }

    public void updateData() {
                try {
                    GetData data = new GetData(new URL("http://data.foli.fi/siri/sm"));
                    data.delegate = SelectDestination.this;
                    data.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
    }

    class Adapter extends BaseAdapter {
        private Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return stopcount-1;
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
            if(!stopsS.isEmpty()) {
                tv.setText(stopsS.get(position));
                tv.setTextSize(20);
                tv.setTextColor(Color.rgb(255, 255, 255));
                //tv.setBackgroundColor(Color.rgb(234, 160, 0));
                //tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            return tv;
        }
    }
}
