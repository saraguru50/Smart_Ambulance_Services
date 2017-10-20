package com.example.sri.smartambulanceservices;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sri on 10/9/2017.
 */

public class getDirectionsData extends AsyncTask<Object,String,String>{

    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String color;
    PolylineOptions polyline[] = new PolylineOptions[1005];
    int count;

    public getDirectionsData() {

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Object[] objects) {

        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        color = (String) objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        String[] directionsList = null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);
        displayDirection(directionsList);
    }

    public void displayDirection(String[] directionsList) {

        count = directionsList.length;
        for(int i=0;i<count;i++) {
            polyline[i] = new PolylineOptions();
            polyline[i] = polyline[i].width(10);
            if(color == "red") polyline[i] = polyline[i].color(Color.RED);
            else polyline[i] = polyline[i].color(Color.GREEN);
            polyline[i].visible(true);
            polyline[i] = polyline[i].addAll(PolyUtil.decode(directionsList[i]));

            //mMap.addPolyline(polyline);
        }


    }
}
