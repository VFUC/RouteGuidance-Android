package com.capstone.innovationproject.routeguidance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarActivity;
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

import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NextStop extends AppCompatActivity implements AsyncResponse {
    private TextView stopnameField;
    private TextView busnumberField;
    private TextView distanceField;
    private LocationManager locationManager;
    private String provider;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private String busNumberText = "";
    private String stopNameText = "";
    private String distanceText = "";
    private String busId = "";

    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            busId = bundle.getString("id");
        }

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("fi_FI"));
                }
            }
        });

        // vibration
        final Vibrator vibe = (Vibrator) NextStop.this.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);

        // play notification sound
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopnameField = (TextView) findViewById(R.id.stopname);
        busnumberField = (TextView) findViewById(R.id.busnumber);
        distanceField = (TextView) findViewById(R.id.distance);

        updateData();
    }

    public void onClick(View v){
        Intent i;
        //t1.speak("TTS testi", TextToSpeech.QUEUE_FLUSH, null, null);
        switch (v.getId()){
            case R.id.button2:
                i = new Intent(NextStop.this, SelectDestination.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", busId);
                i.putExtras(bundle);
                startActivity(i);
                break;
            default:
                return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void processFinish(String output){
        float Longitude;
        float Latitude;
        String stopName;
        String key = "";
        String busNumber;
        try {
            JSONObject jsonRootObject = new JSONObject(output);
            JSONObject jsonArray = jsonRootObject.optJSONObject("result");
            JSONObject jsonVehicles = jsonArray.optJSONObject("vehicles");
            Location busLocation = new Location("busLocation");
            double distance = 10000, distancetemp;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                key = bundle.getString("id");
                busId = bundle.getString("id");
            }

            JSONObject jsonNode = jsonVehicles.getJSONObject(key);

            //Get bus data
            busNumber = jsonNode.optString("publishedlinename");
            stopName = jsonNode.optString("next_stoppointname");
            Longitude = Float.parseFloat(jsonNode.optString("longitude"));
            Latitude = Float.parseFloat(jsonNode.optString("latitude"));

            busLocation.setLatitude(Latitude);
            busLocation.setLongitude(Longitude);

            busNumberText = busNumber;
            stopNameText = stopName;
            distanceText = String.format("");

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
                    GetData data = new GetData(new URL("http://data.foli.fi/siri/vm"));
                    data.delegate = NextStop.this;
                    data.execute();
                } catch (Exception e) {e.printStackTrace();}

            }
        }, 0, 5000);
    }
}