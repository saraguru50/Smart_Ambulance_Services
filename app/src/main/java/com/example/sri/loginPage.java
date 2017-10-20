package com.example.sri.smartambulanceservices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by sri on 9/8/2017.
 */

public class loginPage extends Activity {

    Button b1,b2;
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.login_page);
        b1 = (Button) findViewById(R.id.register);
        b2 = (Button) findViewById(R.id.login);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent("com.example.sri.smartambulanceservices.REGISTERPAGE");
                startActivity(intent1);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent("com.example.sri.smartambulanceservices.LOGINFORM");
                startActivity(intent2);
                finish();
            }
        });
    }

}
