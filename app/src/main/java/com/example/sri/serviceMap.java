package com.example.sri.smartambulanceservices;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by sri on 9/14/2017.
 */

public class serviceMap extends FragmentActivity implements OnMapReadyCallback {


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Handler mhandler;
    private Runnable mrunnable;
    private GoogleMap mMap;
    private static int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(0,0);
    private final static String TAG = "serviceMap";
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    BitmapDescriptor car,person,hospital;
    String username;
    Button b1;
    double s_longt,s_latt;
    double u_longt,u_latt;
    double ans_latt,ans_longt;
    String ans_name = "";
    int ans_dist = 0;
    LocationTrack locationTrack;
    sqliteintegration db;
    getDirectionsData directions1,directions2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_map);
        locationTrack = new LocationTrack(serviceMap.this);
        s_latt = 0; s_longt = 0;
        u_longt = 0; u_latt = 0;
        ans_latt = 0; ans_longt = 0;
        directions1 = new getDirectionsData();
        directions2 = new getDirectionsData();
        db = new sqliteintegration(this);
        Cursor cursor;
        cursor = db.getuser();
        while(cursor.moveToNext()) {
            username = cursor.getString(8);
        }
        b1 = (Button) findViewById(R.id.finish);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.example.sri.smartambulanceservices.LOGINPAGE");
                MediaPlayer ring= MediaPlayer.create(serviceMap.this,R.raw.ssg_voice);
                ring.start();
                String link1 = "http://192.168.43.224/Smart_Ambulance_Service/update_flag_user.php?username=" + username + "&%20flag=0";
                new flagUpdate().execute(link1);
                startActivity(intent);
                finish();
            }
        });
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

    }
    public void gethospitaldetails() {
        class hospitaldetails extends AsyncTask {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Object doInBackground(Object[] objects) {

                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_hospitals.php";
                URL url = null;
                try {
                    url = new URL(link);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                try {
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String content = "", line;
                if(rd != null) {
                    try {
                        while ((line = rd.readLine()) != null) {
                            content += line;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return content;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {

            }

            @Override
            protected void onPostExecute(Object objects) {
                final String s[] = new String[1];
                s[0] = (String) objects;
                int i, len = 0, j;
                String temp, name;
                double latt=0, longt=0;
                boolean found = true;
                char c;
             //   Toast.makeText(serviceMap.this,s[0],Toast.LENGTH_LONG).show();
                if (s[0] != null)
                    len = s[0].length();
                //final ArrayList<Pair<Float, String>> list = new ArrayList<Pair<Float, String>>();
                for (i = 0; i < len; i++) {
                    temp = "";
                    for (j = i; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    name = temp;
                    temp = "";
                    for (j = i + 1; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    latt = Double.parseDouble(temp);
                    temp = "";
                    for (j = i + 1; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    longt = Double.parseDouble(temp);
                    float t[] = new float[10];
                    Location.distanceBetween(u_latt,u_longt,latt,longt,t);
                    if(found == true) {
                        found = false;
                        ans_latt = latt; ans_longt = longt; ans_dist = Math.round(t[0]); ans_name = name;
                    }
                    else {
                        int dist = Math.round(t[0]);
                        if(dist < ans_dist) {
                            ans_latt = latt; ans_longt = longt; ans_dist = dist; ans_name = name;
                        }
                    }
                }
                //mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name).icon(hospital));
                 if(ans_latt != 0 && ans_longt != 0 && u_latt != 0 && u_longt != 0 && s_latt != 0 && s_longt != 0) {
                     Object obj[] = new Object[3];
                     String url = getDirectionsUrl();
                     directions1 = new getDirectionsData();
                     obj[0] = mMap;
                     obj[1] = url;
                     obj[2] = "green";
                     directions1.execute(obj);
                     for(i=0;i<directions1.count;i++) {
                         mMap.addPolyline(directions1.polyline[i]);
                     }
                     Object obj1[] = new Object[3];
                     String url1 = getDirectionsUrl1();
                     directions2 = new getDirectionsData();
                     obj1[0] = mMap;
                     obj1[1] = url1;
                     obj1[2] = "red";
                     directions2.execute(obj1);
                     for(i=0;i<directions2.count;i++) {
                         mMap.addPolyline(directions2.polyline[i]);
                     }
                 }
            }
        }
        hospitaldetails obj = new hospitaldetails();
        obj.execute((Object[]) null);
    }
    public void getAmbulanceLocations() {
        final String[] s = new String[1];
        //Toast.makeText(MapsActivity.this,"Entered the function",Toast.LENGTH_LONG).show();
        class select_db extends AsyncTask {

            public select_db() {

            }
            @Override
            protected void onPreExecute() {

            }
            @Override
            protected String doInBackground(Object[] objects) {
                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_user_flag.php?username=" + username;
                URL url = null;
                try {
                    url = new URL(link);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                try {
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ans = "", line;
                if(rd != null) {
                    try {
                        while ((line = rd.readLine()) != null) {
                            ans += line;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return ans;
            }
            @Override
            protected void onProgressUpdate(Object[] values) {

            }
            @Override
            protected void onPostExecute(Object objects) {
                s[0] = (String) objects;
                int i,len=0,j;
                String temp;
                int flag;
                double latt,longt;
                char c;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                if(s[0] != null)
                    len = s[0].length();
                for(i=0;i<len;i++) {
                    temp = "";
                    for(j=i;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    flag = Integer.parseInt(temp);
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    latt = Double.parseDouble(temp);
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    longt = Double.parseDouble(temp);
                    LatLng latlng = new LatLng(latt, longt);
                    //mMap.addMarker(new MarkerOptions().position(latlng).title("Requester's Location").icon(person));
                    //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,DEFAULT_ZOOM));
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Current Location").icon(person).visible(true));
                    //mMap.addMarker(user_marker);
                }
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void getuserLocation() {
        //final String[] s = new String[1];
        //Toast.makeText(MapsActivity.this,"Entered the function",Toast.LENGTH_LONG).show();
        class select_db extends AsyncTask {

            public select_db() {

            }
            @Override
            protected void onPreExecute() {

            }
            @Override
            protected String doInBackground(Object[] objects) {
                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_user_flag.php?username=" + username;
                URL url = null;
                try {
                    url = new URL(link);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                try {
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ans = "", line;
                if(rd != null) {
                    try {
                        while ((line = rd.readLine()) != null) {
                            ans += line;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return ans;
            }
            @Override
            protected void onProgressUpdate(Object[] values) {

            }
            @Override
            protected void onPostExecute(Object objects) {
                final String s[] = new String[1];
                s[0] = (String) objects;
                int i,len=0,j;
                String temp;
                int flag;
                //double latt,longt;
                char c;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                if(s[0] != null)
                    len = s[0].length();
                for(i=0;i<len;i++) {
                    temp = "";
                    for(j=i;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    flag = Integer.parseInt(temp);
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    u_latt = Double.parseDouble(temp);
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    u_longt = Double.parseDouble(temp);
                    //new get_hospital().execute(u_latt,u_longt,mMap);
                    //LatLng latlng = new LatLng(latt, longt);
                    //Icon = BitmapDescriptorFactory.fromResource(R.mipmap.ambulance10);
                    //mMap.addMarker(new MarkerOptions().position(latlng).title("Requester's Location")/*.icon(Icon)*/);
                    //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,DEFAULT_ZOOM));
                }
                gethospitaldetails();
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void updatecurrentLocations() {
        final String[] s = new String[1];
        //Toast.makeText(MapsActivity.this,"Entered the function",Toast.LENGTH_LONG).show();
        class select_db extends AsyncTask {

            public select_db() {

            }
            @Override
            protected void onPreExecute() {

            }
            @Override
            protected String doInBackground(Object[] objects) {
                String link = "http://192.168.43.224/Smart_Ambulance_Service/update_current_location.php?username=" + username + "&%20latitude=" + s_latt + "&%20longtitude=" + s_longt;
                URL url = null;
                try {
                    url = new URL(link);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    connection.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                }
                connection.setDoOutput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                try {
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader rd = null;
                try {
                    rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String ans = "", line;
                if(rd != null) {
                    try {
                        while ((line = rd.readLine()) != null) {
                            ans += line;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return ans;
            }
            @Override
            protected void onProgressUpdate(Object[] values) {

            }
            @Override
            protected void onPostExecute(Object objects) {

            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            /*LatLng latlng = new LatLng(13.8975, 80.5435);
            mMap.addMarker(new MarkerOptions().position(latlng).title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM)); */

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + u_latt + "," + u_longt);
        googleDirectionsUrl.append("&destination=" + ans_latt + "," + ans_longt);
        googleDirectionsUrl.append("&key=" + "AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw");

        return (googleDirectionsUrl.toString());
        //String link = "https://maps.googleapis.com/maps/api/directions/json?origin=13.0012,80.2565&destination=12.9760,80.2212&key=AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw";
    }
    private String getDirectionsUrl1() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + s_latt + "," + s_longt);
        googleDirectionsUrl.append("&destination=" + u_latt + "," + u_longt);
        googleDirectionsUrl.append("&key=" + "AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw");
        //Toast.makeText(MapsActivity.this,googleDirectionsUrl.toString(),Toast.LENGTH_LONG).show();

        return (googleDirectionsUrl.toString());
        //String link = "https://maps.googleapis.com/maps/api/directions/json?origin=13.0012,80.2565&destination=12.9760,80.2212&key=AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw";
        //return link;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapsInitializer.initialize(getApplicationContext());
        car = BitmapDescriptorFactory.fromResource(R.mipmap.car);
        person = BitmapDescriptorFactory.fromResource(R.mipmap.person1);
        hospital = BitmapDescriptorFactory.fromResource(R.mipmap.hospital);
        if (locationTrack.canGetLocation()) {
            s_longt = locationTrack.getLongitude();
            s_latt = locationTrack.getLatitude();
        }
        else {
            locationTrack.showSettingsAlert();
        }
        getuserLocation();
        mhandler = new Handler();
        mrunnable = new Runnable() {
            @Override
            public void run() {
                mMap.clear();
                DEFAULT_ZOOM = (int) mMap.getCameraPosition().zoom;
                if (ActivityCompat.checkSelfPermission(serviceMap.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(serviceMap.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                double d_latt = 0,d_longt = 0;
                if (locationTrack.canGetLocation()) {
                    d_longt = locationTrack.getLongitude();
                    d_latt = locationTrack.getLatitude();
                }
                else {
                    locationTrack.showSettingsAlert();
                }
                mMap.addMarker(new MarkerOptions().position(new LatLng(d_latt,d_longt)).title("Current Location").icon(car).visible(true));
                //mMap.addMarker(driver_marker);
                updatecurrentLocations();
                if(ans_latt != 0 && ans_longt != 0)
                    mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name).icon(hospital));
                if(ans_latt != 0 && ans_longt != 0 && u_latt != 0 && u_longt != 0 && s_latt != 0 && s_longt != 0) {
                    for(int i=0;i<directions1.count;i++) {
                        mMap.addPolyline(directions1.polyline[i]);
                    }
                    for(int i=0;i<directions2.count;i++) {
                        mMap.addPolyline(directions2.polyline[i]);
                    }
                }
//                LatLng latlng1 = new LatLng(d_latt,d_longt);
//                mMap.addMarker(new MarkerOptions().position(latlng1).title("Current Location").icon(car));
                getAmbulanceLocations();
                mhandler.postDelayed(this,5000);
            }
        };
        mhandler.postDelayed(mrunnable,0);
    }
}

