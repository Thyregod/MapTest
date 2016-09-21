package com.example.damgaard.maptest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserAreaActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        final String studentID = intent.getStringExtra("studentID");
        int age = intent.getIntExtra("age", -1);
        final String userID = intent.getStringExtra("userID");

        TextView tvWelcomeMsg = (TextView) findViewById(R.id.tvWelcomeMsg);
        TextView tvName = (TextView) findViewById(R.id.tvName);

        // Display user details
        String message = "Welcome";
        tvWelcomeMsg.setText(message);
        tvName.setText(name);


        final Button bMap = (Button) findViewById(R.id.bMap);


        bMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(UserAreaActivity.this, MapsActivity.class);
                mapIntent.putExtra("userID", userID);
                UserAreaActivity.this.startActivity(mapIntent);
                System.out.println("UserIDTest : " + userID);
            }
        });

    }
}
