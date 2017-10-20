package com.example.sri.smartambulanceservices;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.logging.Logger;

/**
 * Created by sri on 9/4/2017.
 */

class getUserFlag {

    int flag;
    double latitude,longtitude;
    public getUserFlag() {
        this.flag = 0;
        this.latitude = 1.0; this.longtitude = 1.0;
    }
    public void getdata(final String username) {
        final String[] s = new String[1];
        final int[] flag = new int[1];
        final double[] latt = new double[1],longt = new double[1];
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
                    flag[0] = Integer.parseInt(temp);
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    latt[0] = Double.parseDouble(temp);
                    temp = "";
                    for(j=i+1;j<len;j++) {
                        i = j;
                        c = s[0].charAt(j); if(c == '$') break;
                        temp += c;
                    }
                    longt[0] = Double.parseDouble(temp);
                }
                getUserFlag.this.flag = flag[0];
                getUserFlag.this.latitude = latt[0];
                getUserFlag.this.longtitude = longt[0];
            }
        }
        select_db obj = new select_db();
        obj.execute((Object[]) null);
    }


}

public class servicePage extends Activity {
    sqliteintegration db;
    Button b2,b3,b4;
    TextView text1;
    LocationTrack locationTrack;
    AlertDialog.Builder builder;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    private Handler mhandler = null;
    private Runnable mrunnable;
    private Handler mhandler1 = null;
    private Runnable mrunnable1;
    String username;
    int flag;
    double latitude;
    double longtitude;
    boolean ifalready;
    boolean ispage;
    getUserFlag obj;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerservice);
        b2 = (Button) findViewById(R.id.b4);
        b3 = (Button) findViewById(R.id.b6);
        //p1 = (ProgressBar) findViewById(R.id.progressBar2);
        t1 = (TextView) findViewById(R.id.requests);
        locationTrack = new LocationTrack(servicePage.this);
        flag = 0;
        //p1.setVisibility(View.GONE);
        ifalready = false;
        builder = new AlertDialog.Builder(servicePage.this);
        obj = new getUserFlag();
        ispage = true;
        db = new sqliteintegration(this);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    t1.setText("Waiting for incoming requests.....");
            //        p1.setVisibility(View.VISIBLE);
                    Cursor cursor = db.getuser();
                    while (cursor.moveToNext()) {
                        username = cursor.getString(8);
                    }
                    Toast.makeText(servicePage.this, username + " has started the Ambulance Service", Toast.LENGTH_LONG).show();
                    flag = 1;
                    String link = "http://192.168.43.224/Smart_Ambulance_Service/update_flag.php?username=" + username + "&%20flag=1";
                    new flagUpdate().execute(link);
                    mhandler = new Handler();
                    mrunnable = new Runnable() {
                        @Override
                        public void run() {
                            obj.getdata(username);
                            if (locationTrack.canGetLocation()) {
                                longtitude = locationTrack.getLongitude();
                                latitude = locationTrack.getLatitude();
                                //Toast.makeText(servicePage.this, "Longitude:" + Double.toString(longtitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                                new update_db().execute(username,1,latitude,longtitude);
                            } else {
                                locationTrack.showSettingsAlert();
                            }
                            //Toast.makeText(servicePage.this,Integer.toString(obj.flag),Toast.LENGTH_LONG).show();
                            if (obj.flag == 1) {
                                if (ifalready == false && ispage == true) {
                                    builder.setMessage("You have an incoming request at Latitude = " + obj.latitude + " and Longtitude = " + obj.longtitude + ". Do you want to accept or decline the request?")
                                            .setCancelable(false)
                                            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    String link1 = "http://192.168.43.224/Smart_Ambulance_Service/update_flag.php?username=" + username + "&%20flag=0";
                                                    new flagUpdate().execute(link1);
                                                    link1 = "http://192.168.43.224/Smart_Ambulance_Service/update_flag_user.php?username=" + username + "&%20flag=1";
                                                    new flagUpdate().execute(link1);
                                                    Intent intent = new Intent("com.example.sri.smartambulanceservices.SERVICEMAP");
                                                    startActivity(intent);
                                                    locationTrack.stopListener();
                                                    ispage = false;
                                                    dialog.cancel();
                                                    mhandler.removeCallbacksAndMessages(null);
                                                    mhandler1.removeCallbacksAndMessages(null);
                                                    ifalready = false;
                                                    finish();
                                                }
                                            })
                                            .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    String link = "http://192.168.43.224/Smart_Ambulance_Service/update_flag_user.php?username=" + username + "&%20flag=0";
                                                    new flagUpdate().execute(link);
                                                    dialog.cancel();
                                                    ifalready = false; obj.flag = 0;
                                                    mhandler.postDelayed(mrunnable, 2000);
                                                }
                                            });
                                    final AlertDialog alert = builder.create();
                                    alert.show();
                                    ifalready = true;
                                    mhandler1 = new Handler();
                                    mrunnable1 = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (ifalready) {
                                                String link = "http://192.168.43.224/Smart_Ambulance_Service/update_flag_user.php?username=" + username + "&%20flag=0";
                                                new flagUpdate().execute(link);
                                                ifalready = false; obj.flag = 0;
                                                alert.dismiss();
                                                mhandler.postDelayed(mrunnable, 2000);
                                            }
                                        }
                                    };
                                    mhandler1.postDelayed(mrunnable1, 10000);


                                }
                            }
                            else {
                                if (ispage) {
                                    mhandler.postDelayed(this, 1000);
                                }
                            }
                        }
                    };
                    mhandler.postDelayed(mrunnable,0);
                 }
                 else {
                    Toast.makeText(servicePage.this,username + " service is already started",Toast.LENGTH_LONG).show();
                }
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(servicePage.this,username + " service has been stopped",Toast.LENGTH_LONG).show();
                String link = "http://192.168.43.224/Smart_Ambulance_Service/update_flag.php?username=" + username + "&%20flag=0";
                new flagUpdate().execute(link);
                link = "http://192.168.43.224/Smart_Ambulance_Service/update_flag_user.php?username=" + username + "&%20flag=0";
                new flagUpdate().execute(link);
                Intent intent = new Intent("com.example.sri.smartambulanceservices.LOGINPAGE");
                startActivity(intent);
                t1.setText("");
                locationTrack.stopListener(); ispage = false;
                if(mhandler != null) mhandler.removeCallbacksAndMessages(null);
                if(mhandler1 != null) mhandler1.removeCallbacksAndMessages(null);
                finish();
            }
        });
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String)perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String)perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String)permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(servicePage.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
        if(mhandler1 != null) mhandler1.removeCallbacksAndMessages(null);
        if(mhandler != null) mhandler.removeCallbacksAndMessages(null);
    }
}
