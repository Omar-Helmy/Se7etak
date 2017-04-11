package com.example.omar.healthcare;


import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainFragment extends android.support.v4.app.Fragment {

    private Context context;
    private FloatingActionButton fab;
    private ImageView irImg;
    private BluetoothSocket bluetoothSocket;
    private Button btn;
    private TextView heartRateTxt, bodyTempTxt;

    public void setBluetoothSocket(BluetoothSocket bs ){
        bluetoothSocket = bs;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View fragmentLayout = inflater.inflate(R.layout.fragment_main, container, false);
        irImg = (ImageView) fragmentLayout.findViewById(R.id.ir_remote_img);
        btn = (Button) fragmentLayout.findViewById(R.id.chat_activity);
        heartRateTxt = (TextView) fragmentLayout.findViewById(R.id.heart_rate);
        bodyTempTxt = (TextView) fragmentLayout.findViewById(R.id.body_temp);
        context = getActivity();

        irImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context,IRemoteActivity.class);
                startActivity(intent);
            }});


        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                startActivity(intent);
            }});

        Cursor cursor = context.getContentResolver().query(Se7etakProvider.URI_HEALTH.buildUpon().appendPath("string").build(),null,"ts > 1",null,"ts DESC");
        try {
            cursor.moveToFirst();
            heartRateTxt.setText("Heart Rate: "+cursor.getString(cursor.getColumnIndex("heartrate"))+" BPM");
            bodyTempTxt.setText("Body Temp: "+cursor.getString(cursor.getColumnIndex("bodytemp"))+" C");
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return fragmentLayout;
    }





    /*/////////////////////////AsyncTask////////////////////////////////////////
    private class BluetoothClientTask extends AsyncTask<BluetoothDevice, Integer, Void> {

        private BluetoothSocket mSocket = null;
        private String uuid = "00001101-0000-1000-8000-00805F9B34FB";
        private ProgressDialog progressDialog;
        private android.support.v4.app.FragmentTransaction ft;


        @Override
        protected void onPreExecute() {

            // show circular progress bar till successfully connect
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Connecting ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }


        @Override
        protected Void doInBackground(BluetoothDevice... params) {

            BluetoothDevice device = params[0];
            // loop till open socket to BL device
            while(true) {
                try {
                    // uuid is the app's UUID string, also used by the server device
                    // first, try to open socket with required UUID
                    mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                    // second, try to connect
                    while(true) {
                        try {
                            // Connect the device through the socket. This will block
                            // until it succeeds or throws an exception
                            mSocket.connect();
                            return null;
                        } catch (IOException connectException) {
                            //Log.i("BluetoothClientTask", "IOException: " + connectException);
                        }
                    }
                } catch (IOException e) {
                    //Log.i("BluetoothClientTask", "IOException: " + e);
                }
            }
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            Toast.makeText(context, "Connected to wearable!", Toast.LENGTH_SHORT).show();
            bluetoothButton.setVisibility(View.VISIBLE);

            BluetoothFragment bluetoothFragment = new BluetoothFragment();
            bluetoothFragment.setData(mSocket, context);

            HeartRateSensorFragment heartRateSensorFragment = new HeartRateSensorFragment();
            heartRateSensorFragment.setData(mSocket, context);

            ft = getFragmentManager().beginTransaction();
            ft.replace(frameHolderFragment1.getFragmentHolder(), bluetoothFragment).commit();

            ft = getFragmentManager().beginTransaction();
            ft.replace(frameHolderFragment2.getFragmentHolder(), heartRateSensorFragment).commit();
        }

    }*/

}
