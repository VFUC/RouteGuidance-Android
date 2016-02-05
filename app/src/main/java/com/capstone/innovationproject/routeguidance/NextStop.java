package com.capstone.innovationproject.routeguidance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private TextView latituteField;
    private TextView longitudeField;
    private TextView testiTeksti;
    private LocationManager locationManager;
    private String provider;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    Location sijainti;
    String vehicles = "";
    float Longitude = 0;
    float Latitude = 0;
    String name = "";
    String key = "";
    String BusNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);

        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        testiTeksti = (TextView) findViewById(R.id.TextView05);
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
        } else {
            latituteField.setText("-");
            longitudeField.setText("-");
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
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        sijainti = location;
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));
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
        try {
            JSONObject jsonRootObject = new JSONObject(output);
            JSONObject jsonArray = jsonRootObject.optJSONObject("result");
            JSONObject jsonVehicles = jsonArray.optJSONObject("vehicles");
            Location temp = new Location("temp");
            double distance = 10000, distancetemp;

            Iterator<String> iter = jsonVehicles.keys();

            while (iter.hasNext()) {

                key = iter.next();
                //String Vehicle = Integer.toString(550011);
                JSONObject jsonNode = jsonVehicles.getJSONObject(key);

                BusNumber = jsonNode.optString("publishedlinename");
                name = jsonNode.optString("next_stoppointname");
                Longitude = Float.parseFloat(jsonNode.optString("longitude"));
                Latitude = Float.parseFloat(jsonNode.optString("latitude"));
                temp.setLatitude(Latitude);
                temp.setLongitude(Longitude);

                distancetemp = sijainti.distanceTo(temp);
                if(distancetemp < distance && distancetemp > 0) {
                    distance = distancetemp;
                    vehicles = "Bus number= "+ BusNumber + " : \n longitude= " + Longitude + " \n latitude= " + Latitude + " \n Name= " + name + " \n Distance= " + distance;
                }
            }
            testiTeksti.setText(vehicles);

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
                } catch (Exception e) {

                }

            }
        }, 0, 5000);
    }
}