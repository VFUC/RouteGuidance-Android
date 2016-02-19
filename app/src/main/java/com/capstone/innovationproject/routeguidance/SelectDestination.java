package com.capstone.innovationproject.routeguidance;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SelectDestination extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }
}
