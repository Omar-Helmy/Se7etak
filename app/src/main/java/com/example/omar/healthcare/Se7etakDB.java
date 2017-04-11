package com.example.omar.healthcare;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Omar on 12/07/2016.
 */
public class Se7etakDB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "se7etak.db";
    public static final String CHAT_TABLE_NAME = "chats";
    public static final String HEALTH_TABLE_NAME = "healthdata";

    //private SQLiteDatabase db;


    public Se7etakDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ts: timestamp, msg: msg body, dir: if from doctor 1 if from me 0
        db.execSQL("CREATE TABLE "+ CHAT_TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, ts BLOB NOT NULL, msg TEXT NOT NULL, dir INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE "+ HEALTH_TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, ts BLOB NOT NULL, heartrate BLOB NOT NULL, bodytemp BLOB NOT NULL);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + CHAT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HEALTH_TABLE_NAME);
        onCreate(db);
    }
}
