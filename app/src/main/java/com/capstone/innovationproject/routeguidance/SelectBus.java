package com.capstone.innovationproject.routeguidance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;


public class SelectBus extends AppCompatActivity implements LocationListener, AsyncResponse{
    private LocationManager locationManager;
    private String provider;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private Location sijainti;
    private String busId = "";
    ArrayList<Row> buses = new ArrayList<Row>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bus);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        updateData();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new Adapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(SelectBus.this, "" + position,
                        Toast.LENGTH_SHORT).show();
                busId = buses.get(position).getBusId();
                Intent i = new Intent(SelectBus.this, NextStop.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", busId);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }

        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
        locationManager.removeUpdates(this);
    }
    @Override
    public void onLocationChanged(Location location) {
        sijainti = location;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    public void processFinish(String output) {
        float Longitude;
        float Latitude;
        String busDestination;
        String key;
        String busNumber;
        GridView gridview = (GridView) findViewById(R.id.gridview);


        int i=0;

        try {
            JSONObject jsonRootObject = new JSONObject(output);
            JSONObject jsonArray = jsonRootObject.optJSONObject("result");
            JSONObject jsonVehicles = jsonArray.optJSONObject("vehicles");
            Location busLocation = new Location("busLocation");
            int distance = 0;
            buses.clear();

            Iterator<String> iter = jsonVehicles.keys();
            while (iter.hasNext()) {
                key = iter.next();
                JSONObject jsonNode = jsonVehicles.getJSONObject(key);

                //Get bus data
                busNumber = jsonNode.optString("publishedlinename");
                busDestination = jsonNode.optString("destinationname");
                Longitude = Float.parseFloat(jsonNode.optString("longitude"));
                Latitude = Float.parseFloat(jsonNode.optString("latitude"));


                busLocation.setLatitude(Latitude);
                busLocation.setLongitude(Longitude);

                if(sijainti != null) { //if we have user location
                    distance = Math.round(sijainti.distanceTo(busLocation));
                    buses.add(new Row(distance, key, busNumber, busDestination));
                } else {
                    buses.add(new Row(distance, key, busNumber, busDestination));
                }
            }
        } catch (JSONException e) {e.printStackTrace();}
        Collections.sort(buses);
        gridview.setAdapter(new Adapter(this));             //Updates the gridview
    }

    public class Row implements Comparable<Row>{
        public String busId;
        public String busNumber;
        public String busDestination;
        public int distance;

        public Row(int distance, String id, String busNumber, String busDestination) {
            this.distance = distance;
            this.busId = id;
            this.busNumber = busNumber;
            this.busDestination = busDestination;

        }

        public int getDistance() {
            return distance;
        }

        public String getBusDestination() { return busDestination; }

        public String getBusNumber() {
            return busNumber;
        }

        public String getBusId() {
            return busId;
        }

        @Override
        public int compareTo(Row compareBuses) {
            int compareDistance=((Row)compareBuses).getDistance();
            return this.distance-compareDistance;
        }
    }


    public void updateData() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    GetData data = new GetData();
                    data.delegate = SelectBus.this;
                    data.execute();
                } catch (Exception e) {e.printStackTrace();}

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
                tv.setLayoutParams(new GridView.LayoutParams(300, 400));
            }
            else {
                tv = (TextView) convertView;
            }
            if(!buses.isEmpty()) {
                tv.setText("" + buses.get(position).getBusNumber() + "\n" + buses.get(position).getBusDestination() + "\n(" + buses.get(position).getDistance() + " m)");
                tv.setTextSize(20);
                tv.setTextColor(Color.rgb(255, 255, 255));
                tv.setBackgroundColor(Color.rgb(234, 171, 0));
            }
            return tv;
        }
    }
}
