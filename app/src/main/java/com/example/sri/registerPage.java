package com.example.sri.smartambulanceservices;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sri on 9/4/2017.
 */
public class registerPage extends Activity{
    Button b1;
    EditText t1,t2,t3,t4,t5,t6,t7;
    sqliteintegration db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage);
        b1 = (Button) findViewById(R.id.b5);
        t1 = (EditText) findViewById(R.id.et1);
        t2 = (EditText) findViewById(R.id.et2);
        t3 = (EditText) findViewById(R.id.et3);
        t4 = (EditText) findViewById(R.id.et4);
        t5 = (EditText) findViewById(R.id.et5);
        t6 = (EditText) findViewById(R.id.username1);
        t7 = (EditText) findViewById(R.id.password1);
        db = new sqliteintegration(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!db.isonetable()) {
                    boolean isinserted = db.insertdata(t1.getText().toString(), t2.getText().toString(), t3.getText().toString(), t4.getText().toString(), t5.getText().toString(), 13.011248, 80.235214, 0, t6.getText().toString(), t7.getText().toString());
                    new insert_db().execute(t1.getText().toString(), t2.getText().toString(), t3.getText().toString(), t4.getText().toString(), t5.getText().toString(), 13.011248, 80.235214, 0, t6.getText().toString(), t7.getText().toString());
                    if (isinserted == true) {
                        Toast.makeText(registerPage.this, "Your Ambulance Service Successfully Registered", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(registerPage.this, "Registration Failed.....Please Try Again", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(registerPage.this,"Your Ambulance Service is Already Registered",Toast.LENGTH_LONG).show();
                }
                t5.setText(""); t2.setText(""); t3.setText(""); t4.setText(""); t1.setText(""); t6.setText(""); t7.setText("");
            }
        });
    }

    /*@Override
    public void onLocationChanged(Location location) {
        double latt = location.getLatitude();
        double longt = location.getLongitude();
        String s = "Latitude = " + latt + "Longtitude = " + longt;
        Toast.makeText(registerPage.this,s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }*/
}
