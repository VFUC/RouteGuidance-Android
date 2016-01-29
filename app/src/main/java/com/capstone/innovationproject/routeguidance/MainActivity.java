package com.capstone.innovationproject.routeguidance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    //comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v){
        Intent i;
        switch (v.getId()){
            case R.id.button1Main:
                i = new Intent(MainActivity.this, NextStop.class);
                startActivity(i);
                break;
            case  R.id.button2Main:
                //button2Main action here
                break;
            case  R.id.button3Main:
                //button3Main action here
                break;
            case  R.id.button4Main:
                //button4Main action here
                break;
            default:
                return;
        }
    }

}

// testing
// testing 2