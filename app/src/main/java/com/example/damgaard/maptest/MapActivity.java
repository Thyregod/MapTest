package com.example.damgaard.maptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        final EditText etMap = (EditText) findViewById(R.id.etMap);

        etMap.setText("Welcome to the map place");
    }
}
