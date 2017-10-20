package com.example.sri.smartambulanceservices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by sri on 9/4/2017.
 */
public class StartingPage extends Activity {
    Button b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page);
        b1 = (Button) findViewById(R.id.service);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent("com.example.sri.smartambulanceservices.LOGINPAGE");
                startActivity(next);
            }
        });
        b2 = (Button) findViewById(R.id.request);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent("com.example.sri.smartambulanceservices.MAPSACTIVITY");
                startActivity(map);
            }
        });
    }
}
