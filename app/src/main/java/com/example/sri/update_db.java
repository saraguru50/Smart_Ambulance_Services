package com.example.sri.smartambulanceservices;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by sri on 9/13/2017.
 */

public class update_db extends AsyncTask {

    public update_db() {

    }
    @Override
    protected void onPreExecute() {

    }
    @Override
    protected String doInBackground(Object[] object) {
        String username = (String) object[0];
        int flag = (int) object[1];
        double latitude = (double) object[2];
        double longtitude = (double) object[3];
        String link = "http://192.168.43.224/Smart_Ambulance_Service/update.php?username=" + username + "&%20flag=" + flag + "&%20latitude=" + latitude + "&%20longtitude=" + longtitude;
        /*StringBuffer sb = new StringBuffer("");
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        try {
            request.setURI(new URI(link));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();*/
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
                    content += line + "\n";
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
    protected void onPostExecute(Object o) {

    }

}
