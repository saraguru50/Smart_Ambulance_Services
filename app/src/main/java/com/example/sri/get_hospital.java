package com.example.sri.smartambulanceservices;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sri on 10/7/2017.
 */

public class get_hospital extends AsyncTask {

    GoogleMap mMap;
    double u_latt,u_longt;

    public get_hospital() {

    }
    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Object doInBackground(Object[] objects) {
        u_latt = (double) objects[0];
        u_longt = (double) objects[1];
        mMap = (GoogleMap) objects[2];

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
        double latt, longt;
        boolean found = true;
        double ans_latt = 0,ans_longt = 0;
        String ans_name = "";
        int ans_dist = 0;
        char c;
        //Toast.makeText(MapsActivity.this,s[0],Toast.LENGTH_LONG).show();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(ans_latt,ans_longt)).title(ans_name));
    }
}
