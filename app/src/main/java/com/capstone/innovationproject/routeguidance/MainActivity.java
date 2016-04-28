package com.capstone.innovationproject.routeguidance;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    ImageButton imgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgButton =(ImageButton)findViewById(R.id.imageButton);
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SelectBus.class);
                startActivity(i);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void onClick(View v){
        Intent i;
        switch (v.getId()){
            case R.id.textView2:
                i = new Intent(MainActivity.this, SelectBus.class);
                startActivity(i);
                break;
            default:
                return;
        }
    }

}
