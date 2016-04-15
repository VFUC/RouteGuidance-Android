package com.capstone.innovationproject.routeguidance;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NextStop extends AppCompatActivity implements AsyncResponse {
    private static final String TAG = SelectDestination.class.getSimpleName();
    private TextView stopnameField;
    private TextView busnumberField;
    private TextView alarmField;
    private TextView busDestinationField;
    //private TextView distanceField;
    //private LocationManager locationManager;
    //private String provider;
    //private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    //private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    private String busNumberText = "";
    private String stopNameText = "";
    private String busDestination;
    private String distanceText = "";
    private String busId = "";
    private String busNumber = "";
    private String blockref = "";
    private String directionref = "";
    private String alarmStop = "";
    private boolean alarmset=false;

    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageButton imgButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_stop);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xffeaab00));
        actionBar.hide();

        alarmset=false;
        alarmStop = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            busId = bundle.getString("id");
            busNumber = bundle.getString("busnumber");
            busDestination = bundle.getString("busDestination");
            blockref = bundle.getString("blockref");
            directionref = bundle.getString("directionref");
            if(bundle.getString("stopname")==null) {
                findViewById(R.id.textView4).setVisibility(View.GONE);
                findViewById(R.id.alarm_for).setVisibility(View.GONE);
            }
            else {

                    alarmStop = bundle.getString("stopname");
                    alarmField = (TextView) findViewById(R.id.alarm_for);
                    alarmField.setText(alarmStop);
            
            }
        }

        imgButton =(ImageButton)findViewById(R.id.select_destination_image);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NextStop.this, SelectDestination.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", busId);
                bundle.putString("busnumber", busNumber);
                bundle.putString("blockref", blockref);
                bundle.putString("directionref", directionref);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

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
        //vibe.vibrate(100);

        // play notification sound
        /*try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        stopnameField = (TextView) findViewById(R.id.stopname);
        busnumberField = (TextView) findViewById(R.id.busnumber);
        busDestinationField = (TextView) findViewById(R.id.busdestination);
        //distanceField = (TextView) findViewById(R.id.distance);

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
                bundle.putString("busnumber", busNumber);
                bundle.putString("blockref", blockref);
                bundle.putString("directionref", directionref);
                i.putExtras(bundle);
                startActivity(i);
                break;
            default:
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
        String stopAfter;
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

            JSONArray json = new JSONArray();
            if(jsonNode.has("onwardcalls")) json = jsonNode.getJSONArray("onwardcalls");
            for(int i=0; i<json.length(); i++) {
                JSONObject e = json.getJSONObject(i);
                stopAfter = e.getString("stoppointname");
                TextView stopafterField = (TextView) findViewById(R.id.stopafter);
                stopafterField.setText(stopAfter);
            }


            busLocation.setLatitude(Latitude);
            busLocation.setLongitude(Longitude);

            busNumberText = busNumber;
            stopNameText = stopName;
            //distanceText = String.format("");

            stopnameField.setText(stopNameText);
            busnumberField.setText(busNumberText);
            busDestinationField.setText(busDestination);
            //distanceField.setText(distanceText);

            if(!alarmStop.isEmpty()) {
                if (stopName.equals(alarmStop)) {
                    Log.d(TAG, "stopname = alarmstop, alarm if alarmset=false");
                    if (alarmset == false) {
                        Log.d(TAG, "Vibration");
                        final Vibrator vibe = (Vibrator) NextStop.this.getSystemService(Context.VIBRATOR_SERVICE);
                        vibe.vibrate(2000);
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        alarmset = true;
                    } else { //reset alarm
                        alarmset = false;
                        alarmStop = "";
                        findViewById(R.id.textView4).setVisibility(View.GONE);
                        findViewById(R.id.alarm_for).setVisibility(View.GONE);
                    }
                } else alarmset = false;

            }
        } catch (JSONException e) {e.printStackTrace();}
    }

    public void alarmCheck() {
        if(alarmField!=null)
            if(findViewById(R.id.stopname).equals(alarmStop)) {
                Log.d(TAG, "stopname = alarmstop, alarm if alarmset=false");
                if(alarmset==false) {
                    Log.d(TAG, "Vibration");
                    final Vibrator vibe = (Vibrator) NextStop.this.getSystemService(Context.VIBRATOR_SERVICE);
                    vibe.vibrate(2000);
                    alarmset=true;
                }
                else { //reset alarm
                    alarmset=false;
                    alarmStop = "";
                    findViewById(R.id.textView4).setVisibility(View.GONE);
                    findViewById(R.id.alarm_for).setVisibility(View.GONE);
                }
            }
            else alarmset=false;
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