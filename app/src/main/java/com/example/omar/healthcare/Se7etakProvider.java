package com.example.omar.healthcare;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Omar on 12/07/2016.
 */
public class Se7etakProvider extends ContentProvider {

    public static final String AUTHORITY = "com.example.omar.healthcare.provider";
    public static final String CHAT_TABLE_NAME = Se7etakDB.CHAT_TABLE_NAME;
    public static final String HEALTH_TABLE_NAME = Se7etakDB.HEALTH_TABLE_NAME;
    public static final Uri URI_CHAT = Uri.parse("content://"+AUTHORITY+"/"+CHAT_TABLE_NAME );
    public static final Uri URI_HEALTH= Uri.parse("content://"+AUTHORITY+"/"+HEALTH_TABLE_NAME );
    public static final int CHAT_MATCH = 0, HEALTH_MATCH = 10, CHAT_TS_MATCH = 1, HEALTH_TS_MATCH = 11;


    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private Se7etakDB dbHelper; //database helper object
    private SQLiteDatabase db; //database object


    static {
        uriMatcher.addURI(AUTHORITY, CHAT_TABLE_NAME, CHAT_MATCH);
        uriMatcher.addURI(AUTHORITY, HEALTH_TABLE_NAME, HEALTH_MATCH);
        uriMatcher.addURI(AUTHORITY, CHAT_TABLE_NAME+"/*", CHAT_TS_MATCH);
        uriMatcher.addURI(AUTHORITY, HEALTH_TABLE_NAME+"/*", HEALTH_TS_MATCH);
    }

    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        dbHelper = new Se7etakDB(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor newCursor = null;
        db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)){
            case CHAT_MATCH:{
                newCursor = db.query(CHAT_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                newCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            }
            case HEALTH_MATCH:{
                newCursor = db.query(HEALTH_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                newCursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            }
            case CHAT_TS_MATCH:{
                String[] cols = {"ts"};
                newCursor = db.query(CHAT_TABLE_NAME,cols,"ts > 1",null,null,null,"ts DESC","1");
                break;
            }
            case HEALTH_TS_MATCH:{
                newCursor = db.query(HEALTH_TABLE_NAME,projection,"ts > 1",null,null,null,"ts DESC","1");
                break;
            }
        }
        //db.close();   // causes crash!!
        return newCursor;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        db = dbHelper.getWritableDatabase();
        Uri newUri = null;
        switch (uriMatcher.match(uri)){
            case CHAT_MATCH:{
                long id = db.insert(CHAT_TABLE_NAME ,null,contentValues);
                if(id>0){
                    newUri = ContentUris.withAppendedId(URI_CHAT, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }
            }
            case HEALTH_MATCH:{
                long id = db.insert(HEALTH_TABLE_NAME ,null,contentValues);
                if(id>0){
                    newUri = ContentUris.withAppendedId(URI_HEALTH, id);
                    getContext().getContentResolver().notifyChange(newUri, null);
                    break;
                }
            }
        }
        db.close();
        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
