package com.example.sri.smartambulanceservices;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static android.R.attr.x;

class sorting {


    public static Comparator<Pair<Float,String> > distance = new Comparator<Pair<Float, String>>() {

        @Override
        public int compare(Pair<Float, String> t1, Pair<Float, String> t2) {
            boolean b = (t1.first > t2.first);
            if (b == true) return 1;
            else return 0;
        }
    };
}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static int DEFAULT_ZOOM = 15;
    //private final LatLng mDefaultLocation = new LatLng(13.011248, 80.235214);
    private final LatLng mDefaultLocation = new LatLng(0,0);
    private final static String TAG = "MapsActivity";
    private Handler mhandler = null;
    private Runnable mrunnable;
    private Handler mhandler1 = null;
    private Runnable mrunnable1;
    private Runnable mrunnable2;
    private Handler mhandler2;
    BitmapDescriptor car,person,hospital;
    int final_flag,final_distance;
    String final_ambulance;
    double ans_latt,ans_longt;
    String ans_name;
    int ans_dist;
    double latitude,longtitude;
    LocationTrack locationTrack;
    Button b1;
    double s_longt,s_latt;
    double h_latt,h_longt;
    double a_latt,a_longt;

    getDirectionsData directions1,directions2;

    private Location mLastKnownLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        final_ambulance = "#";
        directions1 = new getDirectionsData();
        directions2 = new getDirectionsData();
        b1 = (Button) findViewById(R.id.ambulance);
        locationTrack = new LocationTrack(MapsActivity.this);
     //   cnt = 0;
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this,"Searching for available ambulances.....",Toast.LENGTH_LONG).show();
                b1.setText("Searching for available ambulances"); b1.setEnabled(false);
                if (locationTrack.canGetLocation()) {
                    longtitude = locationTrack.getLongitude();
                    latitude = locationTrack.getLatitude();
                    LatLng current = new LatLng(latitude,longtitude);
                    final_flag = 0;
                    getfinalambulance();
                    //Toast.makeText(servicePage.this, "Longitude:" + Double.toString(longtitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                    //mMap.addCircle(new CircleOptions().center(current).radius(1000.0).strokeColor(Color.BLUE).fillColor(Color.argb(10,0,0,235)));

                } else {
                    locationTrack.showSettingsAlert();
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void getambulance(final String link) {
        //final String s[] = new String[1];
        class gethospital extends AsyncTask{

            public gethospital() {

            }
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Object doInBackground(Object[] objects) {

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
                //Toast.makeText(MapsActivity.this,content,Toast.LENGTH_LONG).show();
                return content;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {

            }

            @Override
            protected void onPostExecute(Object objects) {
                final String s[] = new String[1];
                s[0] = (String) objects;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                int i, len = 0, j;
                String temp;
                double latt=0, longt=0;
                char c;
                //Toast.makeText(MapsActivity.this,"Length = " + s[0].length(),Toast.LENGTH_LONG).show();
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
                    latt = Double.parseDouble(temp);
                    temp = "";
                    for (j = i + 1; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    longt = Double.parseDouble(temp);
                }
                a_latt = latt; a_longt = longt;
                Object obj[] = new Object[3];
                String url = getDirectionsUrl();
                obj[0] = mMap;
                obj[1] = url;
                obj[2] = "green";
                directions1 = new getDirectionsData();
                directions1.execute(obj);
                for(i=0;i<directions1.count;i++) {
                    mMap.addPolyline(directions1.polyline[i]);
                }
                Object obj1[] = new Object[3];
                String url1 = getDirectionsUrl1();
                obj1[0] = mMap;
                obj1[1] = url1;
                obj1[2] = "red";
                directions2 = new getDirectionsData();
                directions2.execute(obj1);
                for(i=0;i<directions2.count;i++) {
                    mMap.addPolyline(directions2.polyline[i]);
                }
                //mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name));
            }
        }
        gethospital obj = new gethospital();
        obj.execute((Object[]) null);
    }
    public void getHospitalDetails(final double u_latt,final double u_longt) {
        //final String s[] = new String[1];
        class gethospital extends AsyncTask{

            public gethospital() {

            }
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
                //Toast.makeText(MapsActivity.this,content,Toast.LENGTH_LONG).show();
                return content;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {

            }

            @Override
            protected void onPostExecute(Object objects) {
                final String s[] = new String[1];
                s[0] = (String) objects;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                int i, len = 0, j;
                String temp, name;
                double latt=0, longt=0;
                boolean found = true;
                char c;
              //  hosp.visible(false);
                //Toast.makeText(MapsActivity.this,"Length = " + s[0].length(),Toast.LENGTH_LONG).show();
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
             //  mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name).icon(hospital).visible(true));
                //mMap.addMarker(hosp);
                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_ambulance.php?username=" + final_ambulance;
                getambulance(link);
                //Toast.makeText(MapsActivity.this,a_latt + "," + a_longt,Toast.LENGTH_LONG).show();

            }
        }
        gethospital obj = new gethospital();
        obj.execute((Object[]) null);
    }

    private String getDirectionsUrl() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + a_latt + "," + a_longt);
        googleDirectionsUrl.append("&destination=" + ans_latt + "," + ans_longt);
        googleDirectionsUrl.append("&key=" + "AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw");
        //Toast.makeText(MapsActivity.this,googleDirectionsUrl.toString(),Toast.LENGTH_LONG).show();

        return (googleDirectionsUrl.toString());
        //String link = "https://maps.googleapis.com/maps/api/directions/json?origin=13.0012,80.2565&destination=12.9760,80.2212&key=AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw";
        //return link;
    }
    private String getDirectionsUrl1() {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + a_latt + "," + a_longt);
        googleDirectionsUrl.append("&destination=" + h_latt + "," + h_longt);
        googleDirectionsUrl.append("&key=" + "AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw");
        //Toast.makeText(MapsActivity.this,googleDirectionsUrl.toString(),Toast.LENGTH_LONG).show();

        return (googleDirectionsUrl.toString());
        //String link = "https://maps.googleapis.com/maps/api/directions/json?origin=13.0012,80.2565&destination=12.9760,80.2212&key=AIzaSyD4cBudeLjFC-FVwNOtH4Hg87ocj5b_uiw";
        //return link;
    }
    public void updateuserdetails(final double latitude1,final double longtitude1,final String username) {
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
                String link = "http://192.168.43.224/Smart_Ambulance_Service/update_user.php?username=" + username + "&%20latitude=" + latitude1 + "&%20longtitude=" + longtitude1;
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
                int f_flag=0;
                char c;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                if(s[0] != null)
                    len = s[0].length();
                for(i=0;i<len;i++) {
                    temp = "";
                    for (j = i; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    f_flag = Integer.parseInt(temp);
                }
                if(f_flag == 0) final_flag = 0;
                else final_flag = 1;
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void getAmbulanceDetails(final String username,final int dist) {
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
                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_details.php?username=" + username;
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
                String temp,f_name="",l_name="",contact="",a_name="";
                char c;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                if(s[0] != null)
                    len = s[0].length();
                for(i=0;i<len;i++) {
                    temp = "";
                    for (j = i; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    f_name = temp;
                    temp = "";
                    for (j = i + 1; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    l_name = temp;
                    temp = "";
                    for (j = i + 1; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    contact = temp;
                    temp = "";
                    for (j = i + 1; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    a_name = temp;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setCancelable(true);
                builder.setTitle(a_name + " Ambulance Service");
                builder.setMessage("Driver First Name:    " + f_name + "\nDriver Last Name:    " + l_name + "\nDriver ContactNo:    " + contact + "\nDistance in metres:    " + dist);
                builder.show();
                b1.setText("Nearby Ambulance is found\nDriver Name : " + f_name + l_name + "\nContact No: " + contact); b1.setEnabled(false);
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void getfinalack(final Pair<Float,String>p) {
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
                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_service.php?username=" + p.second;
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
                if (rd != null) {
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
                int i, len = 0, j;
                String temp;
                int f_flag;
                char c;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                if (s[0] != null)
                    len = s[0].length();
                for (i = 0; i < len; i++) {
                    temp = "";
                    for (j = i; j < len; j++) {
                        i = j;
                        c = s[0].charAt(j);
                        if (c == '$') break;
                        temp += c;
                    }
                    f_flag = Integer.parseInt(temp);
                    if(f_flag == 1) {
                        final_flag = 1; final_ambulance = p.second; final_distance = Math.round(p.first);
                        mhandler2.removeCallbacksAndMessages(null);
                        if (locationTrack.canGetLocation()) {
                            h_longt = locationTrack.getLongitude();
                            h_latt = locationTrack.getLatitude();
                        }
                        else {
                            locationTrack.showSettingsAlert();
                        }
                        getHospitalDetails(h_latt,h_longt);
                        if(final_flag == 1) {
                            getAmbulanceDetails(final_ambulance,final_distance);
                            mhandler1 = new Handler();
                            mrunnable1 = new Runnable() {
                                @Override
                                public void run() {
                                    if(final_flag == 1) {
                                        double latitude1 = 0, longtitude1 = 0;
                                        if (locationTrack.canGetLocation()) {
                                            longtitude1 = locationTrack.getLongitude();
                                            latitude1 = locationTrack.getLatitude();
                                        }
                                        updateuserdetails(latitude1, longtitude1, final_ambulance);

                                        mhandler1.postDelayed(this,3000);
                                    }
                                    else {
                                        final_ambulance = "#";
                                        a_latt = 0; a_longt = 0;
                                        MediaPlayer ring= MediaPlayer.create(MapsActivity.this,R.raw.ssg_voice1);
                                        ring.start();
                                        mhandler1.removeCallbacksAndMessages(null);
                                        b1.setText("Contact the nearby ambulance"); b1.setEnabled(true);
                                    }
                                }
                            };
                            mhandler1.postDelayed(mrunnable1,0);
                        }
                        else {
                            Toast.makeText(MapsActivity.this,"No Ambulance Services are currently available.....Please try again later.....",Toast.LENGTH_LONG).show();
                            b1.setText("Contact the nearby ambulance"); b1.setEnabled(true);
                        }

                    }
                    else {
                        mhandler2.postDelayed(mrunnable2,0);
                    }
                }
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void getshortest(final Pair<Float,String>p) {
        //Toast.makeText(MapsActivity.this,"Entered the function",Toast.LENGTH_LONG).show();
        class select_db extends AsyncTask {

            public select_db() {

            }
            @Override
            protected void onPreExecute() {

            }
            @Override
            protected String doInBackground(Object[] objects) {
                String link = "http://192.168.43.224/Smart_Ambulance_Service/update_final.php?username=" + p.second;
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
                final Handler mhandler3 = new Handler();
                Runnable mrunnable3 = new Runnable() {
                    @Override
                    public void run() {
                        getfinalack(p);
                        mhandler3.removeCallbacksAndMessages(null);
                    }
                };
                mhandler3.postDelayed(mrunnable3,15000);
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void getfinalambulance() {
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
                String link = "http://192.168.43.224/Smart_Ambulance_Service/select_final.php";
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
                String temp,name;
                double latt,longt;
                int f_flag;
                char c;
                //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
                if(s[0] != null)
                    len = s[0].length();
                final ArrayList<Pair<Float,String> > list = new ArrayList<Pair<Float,String> >();
                for(i=0;i<len;i++) {
                    temp = "";
                    for(j=i;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    name = temp;
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    f_flag = Integer.parseInt(temp);
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
                    //Icon = BitmapDescriptorFactory.fromResource(R.mipmap.ambulance10);
                    double s_latt=0,s_longt=0;
                    float dist[] = new float[2];
                    if (locationTrack.canGetLocation()) {
                        s_longt = locationTrack.getLongitude();
                        s_latt = locationTrack.getLatitude();
                    }
                    else {
                        locationTrack.showSettingsAlert();
                    }
                    Location.distanceBetween(latt,longt,s_latt,s_longt,dist);
                    list.add(new Pair<Float, String>(dist[0],name));
                    /*if(check == "#")
                        mMap.addMarker(new MarkerOptions().position(latlng).title(name).icon(Icon));
                    else {
                        if(name == check) {
                            mMap.addMarker(new MarkerOptions().position(latlng).title(name));
                        }
                    } */
                    //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,DEFAULT_ZOOM));
                }
                Collections.sort(list,sorting.distance);
                final int[] t = {-1};
                /*for(i=0;i<list.size();i++) {
                    getshortest(list.get(i));
                    if(final_flag == 1) break;
                } */
                mhandler2 = new Handler();
                mrunnable2 = new Runnable() {
                    @Override
                    public void run() {
                        t[0] = t[0] + 1;
                        if(t[0] < list.size()) {
                            getshortest(list.get(t[0]));
                            Pair<Float,String> temp = list.get(t[0]);
                        }
                        else {
                            Toast.makeText(MapsActivity.this,"No Ambulance Services are currently available.....Please try again later.....",Toast.LENGTH_LONG).show();
                            b1.setText("Contact the nearby ambulance"); b1.setEnabled(true);
                        }
                    }
                };
                mhandler2.postDelayed(mrunnable2,0);
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);

    }
    public void getAmbulanceLocations(final String check) {
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
                    String link = "http://192.168.43.224/Smart_Ambulance_Service/select.php";
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
                    String temp,name,u_name;
                    double latt,longt;
                    int f_flag,u_flag;
                    char c;
                    if(s[0] != null)
                        len = s[0].length();
                    for(i=0;i<len;i++) {
                        temp = "";
                        for(j=i;j<len;j++) {
                            i = j;
                            c = s[0].charAt(j); if(c == '$') break;
                            temp += c;
                        }
                        name = temp;
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
                        temp = "";
                        for(j=i+1;j<len;j++) {
                            i = j;
                            c = s[0].charAt(j); if(c == '$') break;
                            temp += c;
                        }
                        f_flag = Integer.parseInt(temp);
                        temp = "";
                        for(j=i+1;j<len;j++) {
                            i = j;
                            c = s[0].charAt(j); if(c == '$') break;
                            temp += c;
                        }
                        u_flag = Integer.parseInt(temp);
                        temp = "";
                        for(j=i+1;j<len;j++) {
                            i = j;
                            c = s[0].charAt(j); if(c == '$') break;
                            temp += c;
                        }
                        u_name = temp;
                        LatLng latlng = new LatLng(latt, longt);


                        if(final_ambulance == "#") {
                            if (f_flag == 1 && u_flag == 0) {
                                mMap.addMarker(new MarkerOptions().position(latlng).title(name).icon(car));
                            }
                        }
                        else {
                            if(u_name.equals(final_ambulance)) {
 //                               Log.d("name",name);
                                mMap.addMarker(new MarkerOptions().position(latlng).title(name).icon(car));
                                //mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name).icon(hospital));
                            }
                            //    mMap.addMarker(new MarkerOptions().position(latlng).title(name)/*.icon(Icon)*/);
                        }
                      //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,DEFAULT_ZOOM));
                    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getApplicationContext());
        car = BitmapDescriptorFactory.fromResource(R.mipmap.car);
        person = BitmapDescriptorFactory.fromResource(R.mipmap.person1);
        hospital = BitmapDescriptorFactory.fromResource(R.mipmap.hospital);
        mMap = googleMap;
        mhandler = new Handler();
        mMap.getUiSettings().setCompassEnabled(true);
        mrunnable = new Runnable() {
            @Override
            public void run() {
                mMap.clear();
               // user.visible(false);
                DEFAULT_ZOOM = (int) mMap.getCameraPosition().zoom;
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                if (locationTrack.canGetLocation()) {
                    s_longt = locationTrack.getLongitude();
                    s_latt = locationTrack.getLatitude();
                }
                else {
                    locationTrack.showSettingsAlert();
                }
                LatLng latlng1 = new LatLng(s_latt,s_longt);
                if(final_ambulance != "#") {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name).icon(hospital));
                    if(a_latt != 0 && a_longt != 0 && ans_latt != 0 && ans_longt != 0 && h_latt != 0 && h_longt != 0) {
                        for(int i=0;i<directions1.count;i++) {
                            mMap.addPolyline(directions1.polyline[i]);
                        }
                        for(int i=0;i<directions2.count;i++) {
                            mMap.addPolyline(directions2.polyline[i]);
                        }
                    }
                }
                mMap.addMarker(new MarkerOptions().position(latlng1).title("Current Location").icon(person));
                getAmbulanceLocations(final_ambulance);
                mhandler.postDelayed(this,5000);
            }
        };
        mhandler.postDelayed(mrunnable,0);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mhandler != null) {
            mhandler.removeCallbacksAndMessages(null);
            //mhandler.getLooper().quit();
        }
        if(mhandler1 != null) {
            mhandler1.removeCallbacksAndMessages(null);
           // mhandler1.getLooper().quit();
        }
    }
}
