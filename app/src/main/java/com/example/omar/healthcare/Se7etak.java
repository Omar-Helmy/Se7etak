package com.example.omar.healthcare;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.cloudinary.Cloudinary;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.client.Firebase;

/**
 * Created by Omar on 01/02/2016.
 */
public class Se7etak extends Application {

    private static Context context;

    //Firebase:
    public static final String FIREBASE_URL = "https://se7etak.firebaseio.com";
    public static Firebase rootRef,userRef;
    public static Firebase userInfoNode,healthNode,chatNode;

    // shared pref
    public static final String SHARED_PREF_FILE = "com.example.omar.healthcare.SharedPreferences";
    // create or get shared preferences unique file:
    public static SharedPreferences sharedPref;

    // cloudinary:
    public static Cloudinary cloudinary;

    public static Boolean logged = true;
    @Override
    public void onCreate() {

        super.onCreate();
        context=this;

        // facebook:
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //firebase:
        Firebase.setAndroidContext(getContext());
        rootRef = new Firebase(FIREBASE_URL);

        // cloudinary:
        cloudinary = new Cloudinary("cloudinary://395362475222538:Qtli1yJWFL6AtqsDavD5bKwCk-A@healthcare");


        // shared pref
        sharedPref = getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        // create keys for loginName and loginPassword, first check if they created already,:
        SharedPreferences.Editor editor = sharedPref.edit(); // request editing shared pref file

        if(sharedPref.getString("userID","null").equals("null")) // create new key
            editor.putString("userID", "null");

        if(sharedPref.getString("provider", "null").equals("null")) // create new key
            editor.putString("provider", "null");

        if(sharedPref.getString("name","null").equals("null")) // create new key
            editor.putString("name", "Please Login First");

        if(sharedPref.getString("email","null").equals("null")) // create new key
            editor.putString("email", "null");

        if(sharedPref.getString("password","null").equals("null")) // create new key
            editor.putString("password", "null");

        if(sharedPref.getString("gender","null").equals("null")) // create new key
            editor.putString("gender", "null");

        if(sharedPref.getString("picture","null").equals("null")) // create new key
            editor.putString("picture", Se7etak.cloudinary.url().generate("user_profile"));

        editor.apply(); // save

    }

    public static Context getContext(){
        return context;
    }
    public static void createUserNode(String uid){
        userRef = Se7etak.rootRef.child("users").child(uid);
        userInfoNode = Se7etak.userRef.child("info");
        healthNode = Se7etak.userRef.child("healthData");
        chatNode = Se7etak.userRef.child("chat");
    }
}
