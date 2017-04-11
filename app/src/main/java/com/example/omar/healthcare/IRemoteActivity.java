package com.example.omar.healthcare;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class IRemoteActivity extends AppCompatActivity {

    private Button onOffBtn, tempHbtn, tempLbtn;
    private OutputStream tmpOut;
    private BluetoothSocket bluetoothSocket = MainActivity.mSocket;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iremote);
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Remote Control");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        onOffBtn = (Button) this.findViewById(R.id.on_off_btn);
        tempLbtn = (Button) this.findViewById(R.id.temp_decrease_btn);
        tempHbtn = (Button) this.findViewById(R.id.temp_increase_btn);

        onOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    tmpOut = bluetoothSocket.getOutputStream();
                    //data to be sent
                    byte[] bytes = "A".getBytes();
                    try {
                        tmpOut.write(bytes);
                        //Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(context, "Error sending!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(context, "Error sending!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tempHbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tempLbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
