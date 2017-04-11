package com.example.omar.healthcare;


import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;


public class BluetoothFragment extends android.support.v4.app.Fragment {

    private EditText editText;
    private BluetoothSocket bluetoothSocket;
    private Context context;
    private OutputStream tmpOut;
    //private BluetoothClientThread bluetoothClientThread;

    public void setData(BluetoothSocket mBS, Context c){
        bluetoothSocket=mBS;
        context=c;
        //bluetoothClientThread = new BluetoothClientThread(mArduino);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View fragmentLayout = inflater.inflate(R.layout.bluetooth_fragment, container, false);
        Button sendButton = (Button) fragmentLayout.findViewById(R.id.send_button);
        final Button cancelButton = (Button) fragmentLayout.findViewById(R.id.cancel_button);
        editText = (EditText) fragmentLayout.findViewById(R.id.input_text);

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                //editText.getText().clear();
                cancelButton.setVisibility(View.VISIBLE);
                try {
                    tmpOut = bluetoothSocket.getOutputStream();
                    //data to be sent
                    byte[] bytes = editText.getText().toString().getBytes();
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                try {
                    tmpOut.close();
                } catch (IOException e) { }
            }
        });


        return fragmentLayout;
    }
}



/*
//discover the B devices
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                ArrayList listOfDiscovered = new ArrayList();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    listOfDiscovered.add(device.getName() + "\n" + device.getAddress());

                    ArrayAdapter adapterOfDiscovered = new ArrayAdapter(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1, listOfPaired);
                    listView.setAdapter(adapterOfDiscovered);
                }
            }
        };
// Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().getApplication().registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        cancelDiscovery();

 */
