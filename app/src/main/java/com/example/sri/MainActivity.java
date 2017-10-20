package com.example.sri.smartambulanceservices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by sri on 9/4/2017.
 */
public class MainActivity extends Activity {
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressBar loader;
        loader = (ProgressBar) findViewById(R.id.loading);
        loader.setVisibility(View.GONE);
        Thread t1 = new Thread() {
            public void run() {
                try {
                    loader.setVisibility(View.VISIBLE);
                    sleep(5000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    loader.setVisibility(View.VISIBLE);
                    Intent startingIntent = new Intent("com.example.sri.smartambulanceservices.STARTINGPAGE");
                    startActivity(startingIntent);
                    finish();
                }
            }
        };
        t1.start();
    }
}
