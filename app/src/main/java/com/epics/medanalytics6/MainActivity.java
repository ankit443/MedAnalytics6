package com.epics.medanalytics6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Button loginButtonMain;
    TextView registerMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting the title
        getSupportActionBar().setTitle("Nirog Application");

        //Open Login Activity

        registerMain = findViewById(R.id.tvRegisterMain);
        loginButtonMain = findViewById(R.id.btnloginMain);


        loginButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        //Open Register Activity

        registerMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });




    }
}