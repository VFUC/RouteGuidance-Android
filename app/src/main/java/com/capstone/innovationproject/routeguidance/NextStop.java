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

    private EditText urlText; //Network-testi
    private TextView textView;

    String vehicles = "";
    float Longitude = 0;
    float Latitude = 0;
    String name = "";
    String key = "";
    int BusNumber;

    //GetData getdata = new GetData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);

        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        testiTeksti = (TextView) findViewById(R.id.TextView05);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //getdata.delegate = this;
        //getdata.execute();
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
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method
        //
        //testiTeksti = (TextView) findViewById(R.id.TextView05);

        try {

            JSONObject jsonRootObject = new JSONObject(output);

            JSONObject jsonArray = jsonRootObject.optJSONObject("result");
            JSONObject jsonVehicles = jsonArray.optJSONObject("vehicles");

            Iterator<String> iter = jsonVehicles.keys();

            while (iter.hasNext()) {

                key = iter.next();
                //String Vehicle = Integer.toString(550011);
                JSONObject jsonNode = jsonVehicles.getJSONObject(key);

                BusNumber = Integer.parseInt (jsonNode.optString("publishedlinename"));
                Longitude = Float.parseFloat(jsonNode.optString("longitude"));
                Latitude = Float.parseFloat(jsonNode.optString("latitude"));
                name = jsonNode.optString("next_stoppointname");
            }
            vehicles += "Node"+ key + " : \n longitude= " + Longitude + " \n latitude= " + Latitude + " \n Name= " + name + " \n ";
            testiTeksti.setText(vehicles);

        } catch (JSONException e) {e.printStackTrace();}
    }

    /*public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView output = (TextView) findViewById(R.id.textView1);

        String in;
        in = "{\"sys\":\"VM\",\"status\":\"OK\",\"servertime\":1454485792,\"result\":{\"responsetimestamp\":1454485787,\"producerref\":\"jlt\",\"responsemessageidentifier\":\"western-2743\",\"status\":true,\"moredata\":false,\"vehicles\":{\"550011\":{\"recordedattime\":1454485787,\"validuntiltime\":1454486387,\"linkdistance\":434,\"percentage\":70.28,\"lineref\":\"15\",\"directionref\":\"1\",\"publishedlinename\":\"15\",\"operatorref\":\"55\",\"originref\":\"246\",\"originname\":\"Saram\\u00e4ki\",\"destinationref\":\"1764\",\"destinationname\":\"Kakskerta\",\"originaimeddeparturetime\":1454484300,\"destinationaimedarrivaltime\":1454488320,\"monitored\":true,\"incongestion\":false,\"inpanic\":false,\"longitude\":22.273071,\"latitude\":60.464629,\"delay\":\"PT226S\",\"vehicleref\":\"550011\",\"previouscalls\":[{\"stoppointref\":\"272\",\"visitnumber\":31,\"stoppointname\":\"Saram\\u00e4entie\",\"aimedarrivaltime\":1454485560,\"aimeddeparturetime\":1454485560}],\"vehicleatstop\":false,\"next_stoppointref\":\"273\",\"next_stoppointname\":\"Saramaenpuisto\",\"next_destinationdisplay\":\"Kakskerta Brinkhallin kautta\",\"next_aimedarrivaltime\":1454485560,\"next_expectedarrivaltime\":1454485764,\"next_aimeddeparturetime\":1454485560,\"next_expecteddeparturetime\":1454485764,\"onwardcalls\":[{\"stoppointref\":\"202\",\"visitnumber\":33,\"stoppointname\":\"Oskarinkuja\",\"aimedarrivaltime\":1454485620,\"expectedarrivaltime\":1454485846,\"aimeddeparturetime\":1454485620,\"expecteddeparturetime\":1454485846}],\"bearing\":113},\"550013\":{\"recordedattime\":1454498526,\"validuntiltime\":1454499126,\"linkdistance\":728,\"percentage\":16.35,\"lineref\":\"15\",\"directionref\":\"2\",\"publishedlinename\":\"15\",\"operatorref\":\"55\",\"originref\":\"1764\",\"originname\":\"Kakskerta\",\"destinationref\":\"246\",\"destinationname\":\"Saram\\u00e4ki\",\"originaimeddeparturetime\":1454497800,\"destinationaimedarrivaltime\":1454502060,\"monitored\":true,\"incongestion\":false,\"inpanic\":false,\"longitude\":22.220189,\"latitude\":60.350547,\"delay\":\"PT174S\",\"vehicleref\":\"550013\",\"previouscalls\":[{\"stoppointref\":\"1288\",\"visitnumber\":17,\"stoppointname\":\"Myllykyl\\u00e4ntie\",\"aimedarrivaltime\":1454498340,\"aimeddeparturetime\":1454498340}],\"vehicleatstop\":false,\"next_stoppointref\":\"1289\",\"next_stoppointname\":\"Kierl\\u00e4ntie\",\"next_destinationdisplay\":\"Saram\\u00e4ki\",\"next_aimedarrivaltime\":1454498400,\"next_expectedarrivaltime\":1454498574,\"next_aimeddeparturetime\":1454498400,\"next_expecteddeparturetime\":1454498574,\"onwardcalls\":[{\"stoppointref\":\"1290\",\"visitnumber\":19,\"stoppointname\":\"Kes\\u00e4niementie\",\"aimedarrivaltime\":1454498460,\"expectedarrivaltime\":1454498634,\"aimeddeparturetime\":1454498460,\"expecteddeparturetime\":1454498634}],\"bearing\":115}},\"lastupdated\":1454485787}}";



    }*/

    public void updateData() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    GetData data = new GetData();
                    data.delegate = NextStop.this;
                    data.execute();
                } catch (Exception e) {
                    //testiTeksti.setText("Error. ");
                    // TODO: handle exception
                }

            }
        }, 0, 5000);
    }
}