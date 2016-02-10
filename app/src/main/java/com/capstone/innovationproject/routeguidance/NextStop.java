package com.capstone.innovationproject.routeguidance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class NextStop extends AppCompatActivity implements LocationListener, AsyncResponse {
    private TextView stopnameField;
    private TextView busnumberField;
    private TextView distanceField;
    private LocationManager locationManager;
    private String provider;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private Location sijainti;

    private String busNumberText = "";
    private String stopNameText = "";
    private String distanceText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);

        //latituteField = (TextView) findViewById(R.id.TextView02);
        //longitudeField = (TextView) findViewById(R.id.TextView04);
        stopnameField = (TextView) findViewById(R.id.stopname);
        busnumberField = (TextView) findViewById(R.id.busnumber);
        distanceField = (TextView) findViewById(R.id.distance);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        updateData();

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
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

    public void processFinish(String output){
        float Longitude;
        float Latitude;
        String stopName;
        String key;
        String busNumber;
        try {
            JSONObject jsonRootObject = new JSONObject(output);
            JSONObject jsonArray = jsonRootObject.optJSONObject("result");
            JSONObject jsonVehicles = jsonArray.optJSONObject("vehicles");
            Location busLocation = new Location("busLocation");
            double distance = 10000, distancetemp;

            Iterator<String> iter = jsonVehicles.keys();

            while (iter.hasNext()) {

                key = iter.next();

                JSONObject jsonNode = jsonVehicles.getJSONObject(key);

                //Get bus data
                busNumber = jsonNode.optString("publishedlinename");
                stopName = jsonNode.optString("next_stoppointname");
                Longitude = Float.parseFloat(jsonNode.optString("longitude"));
                Latitude = Float.parseFloat(jsonNode.optString("latitude"));

                busLocation.setLatitude(Latitude);
                busLocation.setLongitude(Longitude);

                if(sijainti != null) { //if we have user location
                    distancetemp = sijainti.distanceTo(busLocation);
                    if(distancetemp < distance && distancetemp > 0){
                        distance = distancetemp;
                        busNumberText = busNumber;
                        stopNameText = stopName;
                        distanceText = String.format("%.0f", distance);
                    }
                } else {
                    busNumberText = "No location!";
                    stopNameText = "";
                }
            }
            stopnameField.setText(stopNameText);
            busnumberField.setText(busNumberText);
            distanceField.setText(distanceText);

        } catch (JSONException e) {e.printStackTrace();}
    }

    public void updateData() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    GetData data = new GetData();
                    data.delegate = NextStop.this;
                    data.execute();
                } catch (Exception e) {e.printStackTrace();}

            }
        }, 0, 5000);
    }
}