package com.example.sri.smartambulanceservices;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;

import java.util.List;

/**
 * Created by sri on 9/4/2017.
 */
public class sqliteintegration extends SQLiteOpenHelper {

        public static final String db_name = "service_db.db";
        public static final String table_name = "service_table";
        public static final String col_1 = "FirstName";
        public static final String col_2 = "LastName";
        public static final String col_3 = "ContactNo";
        public static final String col_4 = "EmailID";
        public static final String col_5 = "AmbulanceName";
        public static final String col_6 = "Latitude";
        public static final String col_7 = "Longtitude";
        public static final String col_8 = "Flag";
        public static final String col_9 = "Username";
        public static final String col_10 = "Password";


        public sqliteintegration(Context context) {
            super(context, db_name, null, 9 );
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table " + table_name + " (" + col_1 + " TEXT," + col_2 + " TEXT," + col_3 + " TEXT," + col_4 + " TEXT," + col_5 + " TEXT," + col_6 + " REAL," + col_7 + " REAL," + col_8 + " INTEGER," + col_9 + " TEXT," + col_10 + " TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table_name);
            onCreate(sqLiteDatabase);
        }
        public boolean insertdata(String first,String last,String contact,String mail,String ambulance,double latitude,double longtitude,int flag,String username,String password) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(col_1,first);
            contentValues.put(col_2,last);
            contentValues.put(col_3,contact);
            contentValues.put(col_4,mail);
            contentValues.put(col_5,ambulance);
            contentValues.put(col_6,latitude);
            contentValues.put(col_7,longtitude);
            contentValues.put(col_8,flag);
            contentValues.put(col_9,username);
            contentValues.put(col_10,password);
            long result = db.insert(table_name,null,contentValues);
            if(result == -1) return false;
            else return true;
        }
        public Cursor isData(String username,String password) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor;
            cursor = db.rawQuery("select * from " + table_name + " where username=? and password=?",new String[] {username,password});
            return cursor;
        }
        public Cursor getuser() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor;
            cursor = db.rawQuery("select * from " + table_name,null);
            return cursor;
        }
        public boolean isonetable() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor resultset = db.rawQuery("Select * from service_table",null);
            if(resultset.getCount() == 1) {
                return true;
            }
            else {
                return false;
            }
        }
    }

