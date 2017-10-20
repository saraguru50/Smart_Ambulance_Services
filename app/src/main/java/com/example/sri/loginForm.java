package com.example.sri.smartambulanceservices;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by sri on 9/10/2017.
 */

public class loginForm extends Activity {

    Button b1;
    EditText t1,t2;
    sqliteintegration db;
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.login_form);
        b1 = (Button) findViewById(R.id.login);
        t1 = (EditText) findViewById(R.id.username);
        t2 = (EditText) findViewById(R.id.password);
        db = new sqliteintegration(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = false;
                Cursor cursor = db.isData(t1.getText().toString(),t2.getText().toString());
                if(cursor.getCount() > 0) {
                    flag = true;
                }
                if(flag) {
                    Intent intent = new Intent("com.example.sri.smartambulanceservices.SERVICEPAGE");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(loginForm.this,"Invalid Username or Password",Toast.LENGTH_LONG).show();
                    t2.setText(""); t1.setText("");
                }
            }
        });
    }

}
