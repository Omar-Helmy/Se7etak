package com.example.omar.healthcare;


import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class WearableFragment extends android.support.v4.app.Fragment {

    private BluetoothSocket bluetoothSocket;
    private Context context;
    private Handler handler = new Handler();
    private ImageView imageView;
    private TextView heartRateTxt, bodyTempTxt;
    private final boolean login = Se7etak.logged;
    private int count = 0;


    public void runReceiverThread(BluetoothSocket bs){
        bluetoothSocket = bs;
        new Thread(new ReceiveBLData()).start();
        //Toast.makeText(context, "Receiving your heart rate!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.fragment_wearable, container, false);
        imageView = (ImageView) fragmentLayout.findViewById(R.id.heart_image);
        heartRateTxt = (TextView) fragmentLayout.findViewById(R.id.heart_rate_text);
        bodyTempTxt = (TextView) fragmentLayout.findViewById(R.id.body_temp_text);
        context = getActivity();

        return fragmentLayout;
    }

    private class ReceiveBLData implements Runnable{

        private InputStream inStream;
        boolean mHeart=true;

        byte[] buffer = new byte[8];  // buffer store for the stream, 3 bytes for BPM + 5 bytes for temp
        int bytes=0,recBytes=0; // bytes returned from read()

        @Override
        public void run() {

            //bundle.putBoolean("heart",true);
            try {
                inStream = bluetoothSocket.getInputStream();
            }catch (IOException e) {}

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    //bytes = inStream.read(buffer, bytes, 8-bytes);
                    bytes = inStream.read(buffer,recBytes,8-recBytes);
                    recBytes += bytes;
                    //final int data = Character.getNumericValue(buffer[0]);
                    final boolean heart=mHeart;
                    if(recBytes==8) {
                        recBytes=0;
                        count++;
                        final String str = new String(buffer);
                        //Log.i("Rx Data:", "arrayList: " + str);
                        // Send the obtained bytes to the UI activity
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                String heartRate = str.substring(0,3);
                                String bodyTemp = str.substring(3,8);
                                // send to firebase:
                                if (count > 10) {
                                    count = 0;
                                    String time = Long.toString(System.currentTimeMillis());
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("ts",time);
                                    contentValues.put("heartrate",heartRate);
                                    contentValues.put("bodytemp",bodyTemp);
                                    context.getContentResolver().insert(Se7etakProvider.URI_HEALTH,contentValues);
                                    if(login){
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("ts", time);
                                        map.put("heartrate",heartRate);
                                        map.put("bodytemp",bodyTemp);
                                        Se7etak.healthNode.push().setValue(map);
                                    }
                                }
                                // update UI:
                                heartRateTxt.setText("BPM: " + heartRate);
                                bodyTempTxt.setText("Body Temp: " + bodyTemp);
                                //Log.i("HeartRate", "UIthread: " + str);
                                if (heart) {
                                    //imageView.setImageResource(heart_beat);
                                    //imageView.setBackgroundColor(red);
                                    imageView.setPadding(10,10,10,10);
                                    //bundle.putBoolean("heart", false);
                                }
                                else {
                                    //imageView.setImageResource(heart_hold);
                                    //imageView.setBackgroundColor(black);
                                    imageView.setPadding(-10,-10,-10,-10);
                                    //bundle.putBoolean("heart", true);
                                }
                            }
                        });
                        mHeart=!mHeart;
                    }
                } catch (IOException e) { }

            }
        }
    }

    }
