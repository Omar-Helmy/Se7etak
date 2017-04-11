package com.example.omar.healthcare;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private Context context;
    private BluetoothDevice mArduino;
    private Boolean isArduinoFound;
    private MainFragment mainFragment = new MainFragment();
    private WearableFragment wearableFragment = new WearableFragment();
    public static BluetoothSocket mSocket = null;

    // icons:
    private final static int L_HOME_ICON = R.drawable.ic_home_white_36dp;
    private final static int HOME_ICON = R.drawable.ic_home_white_24dp;
    private final static int L_WATCH_ICON = R.drawable.ic_watch_white_36dp;
    private final static int WATCH_ICON = R.drawable.ic_watch_white_24dp;
    // tabs and pager:
    private TabLayout.Tab homeTab, bluetoothTab;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrPagerAdapter frPagerAdapter;
    // menu drawer
    private DrawerLayout menuDrawerLayout;
    private ListView menuDrawerListView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    // Btns:
    private FloatingActionButton fab;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        context=this;

        // tool bar:
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.setLogo(HEART);

        // find layout viewPager:
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // tab layout:
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        // menu drawer and list:
        menuDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuDrawerListView = (ListView) findViewById(R.id.list_drawer);

        // add tabs with icons
        homeTab = tabLayout.newTab().setIcon(L_HOME_ICON);
        bluetoothTab = tabLayout.newTab().setIcon(WATCH_ICON);

        tabLayout.addTab(homeTab);
        tabLayout.addTab(bluetoothTab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            bluetoothTab.getIcon().setTint(getResources().getColor(R.color.LightRed));


        // attach adapter to the viewpager:
        frPagerAdapter = new FrPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(frPagerAdapter);

        // Specify that tabs should be displayed in the action bar.
        //actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_TABS);


        // set on tabs click listener:
        // Create a tab listener that is called when the user changes tabs.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
                switch (position) {
                    case 0: {
                        homeTab.setIcon(L_HOME_ICON);
                        bluetoothTab.setIcon(WATCH_ICON);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            homeTab.getIcon().setTint(getResources().getColor(R.color.White));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            bluetoothTab.getIcon().setTint(getResources().getColor(R.color.LightRed));
                        break;
                    }
                    case 1:{
                        homeTab.setIcon(HOME_ICON);
                        bluetoothTab.setIcon(L_WATCH_ICON);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            homeTab.getIcon().setTint(getResources().getColor(R.color.LightRed));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            bluetoothTab.getIcon().setTint(getResources().getColor(R.color.White));
                        break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // menu drawer list adapter
        MenuDrawerListAdapter menuDrawerListAdapter = new MenuDrawerListAdapter();
        menuDrawerListView.setAdapter(menuDrawerListAdapter);
        menuDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                menuDrawerLayout,      /* DrawerLayout object */
                myToolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(R.string.app_name);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Settings");
            }
        };
        // Set the drawer toggle as the DrawerListener
        menuDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        /**Main Code Here!!**/
        // listener on FAB:
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // read Bluetooth status and enable it if disabled
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 0);
                    //Toast.makeText(this, "Turning Bluetooth on", Toast.LENGTH_SHORT).show();
                }
                else {
                    // get paired devices
                    final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    // If there are paired devices
                    if (pairedDevices.size() > 0) {
                        // Loop through paired devices
                        for (BluetoothDevice device : pairedDevices) {
                            //Toast.makeText(this,"Showing Paired Devices",Toast.LENGTH_SHORT).show();
                            String deviceName = device.getName();
                            String deviceAdd = device.getAddress();
                            if (deviceAdd.equals("30:14:11:20:04:11")) {
                                Toast.makeText(context,
                                        "Wearable paired! Name: \"" + deviceName + "\" Address: \"" + deviceAdd + "\"", Toast.LENGTH_LONG).show();
                                // it is better to cancel discovery to save Bluetooth resources before starting any connections:
                                mBluetoothAdapter.cancelDiscovery();
                                BluetoothClientTask bluetoothClientTask = new BluetoothClientTask();
                                bluetoothClientTask.execute(device);
                                break;
                            }
                        }
                    }
                }
            }
        });

        if(ChatService.FIRST_TIME)
            startService(new Intent(this, ChatService.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // to receive intent of image picker from gallery here and upload it to cloudinary:
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            InputStream inputStream;
            try {
                inputStream = getContentResolver().openInputStream(Uri.parse(selectedImage.toString()));
            }catch (Exception e){
                inputStream=null;
            }
            UploadImageTask uploadImageTask = new UploadImageTask();
            uploadImageTask.execute(inputStream);
        }
    }

    /*********************************Bluetooth Connection AsyncTask********************************/
    private class BluetoothClientTask extends AsyncTask<BluetoothDevice, Integer, Void> {

        private final String uuid = "00001101-0000-1000-8000-00805F9B34FB";
        private ProgressDialog progressDialog;

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
            wearableFragment.runReceiverThread(mSocket);
            mainFragment.setBluetoothSocket(mSocket);

        }

    }

    /***********************************Fragment Pager Adapter**************************************/
    private class FrPagerAdapter extends FragmentPagerAdapter{

        public FrPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: {
                    return mainFragment;
                }
                case 1: {
                    return wearableFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /**********************************Menu List Adapter********************************************/
    private class MenuDrawerListAdapter extends BaseAdapter {

        private Context context = Se7etak.getContext();
        private TextView textItem;
        private ImageView imageItem;


        public int getCount() {
            return 3;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                LayoutInflater inflater = LayoutInflater.from(context);
                if(position==0)
                    convertView = inflater.inflate(R.layout.drawer_menu_list_item_portfolio, parent, false);
                else
                    convertView = inflater.inflate(R.layout.drawer_menu_list_item, parent, false);

                textItem = (TextView) convertView.findViewById(R.id.drawer_menu_text);
                imageItem = (ImageView) convertView.findViewById(R.id.drawer_menu_image);

                if(position==0){
                    textItem.setText(Se7etak.sharedPref.getString("name", "null"));
                    String picURL = Se7etak.sharedPref.getString("picture","user_profile");
                    //Picasso.with(context).invalidate(picURL);
                    Picasso.with(context)
                            .load(picURL)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .noFade()
                                    .into(imageItem);
                }else if(position==1){
                    textItem.setText("Account Settings");
                    imageItem.setImageResource(R.drawable.ic_settings_white_36dp);
                } else if(position==2){
                    textItem.setText("Logout");
                    imageItem.setImageResource(R.drawable.ic_power_settings_new_white_36dp);
                }

            } else {
                // recycle the already inflated view
                textItem = (TextView) convertView.findViewById(R.id.drawer_menu_text);
                imageItem = (ImageView) convertView.findViewById(R.id.drawer_menu_image);
            }

            return convertView;
        }
    }

    /**********************************DrawerItemClickListener**************************************/
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            // Highlight the selected item, update the title, and close the drawer
            menuDrawerListView.setItemChecked(position, true);
            if(position==0){
                Intent imagePickIntent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imagePickIntent,1);
            }
            else if(position==2) {
                Se7etak.rootRef.unauth();
                Toast.makeText(context, "logging out...", Toast.LENGTH_SHORT).show();
                finish();
            }
            menuDrawerLayout.closeDrawer(menuDrawerListView);
        }
    }

    /***********************************Upload Profile Image****************************************/
    private class UploadImageTask extends AsyncTask<InputStream,Void,Void>{

        @Override
        protected Void doInBackground(InputStream... inputStreams) {

            Map options = ObjectUtils.asMap(
                    "transformation", new Transformation().width(100).height(100).crop("thumb"),
                    "public_id", Se7etak.sharedPref.getString("userID","null")
            );
            try {
                Se7etak.cloudinary.uploader().upload(inputStreams[0],options);
                SharedPreferences.Editor editor = Se7etak.sharedPref.edit(); // request editing shared pref file
                editor.putString("picture", Se7etak.cloudinary.url().generate(Se7etak.sharedPref.getString("userID", "null")));
                editor.apply();
                Se7etak.userInfoNode.child("picture").setValue(Se7etak.cloudinary.url().generate(Se7etak.sharedPref.getString("userID", "null")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}

/* References:
--------------

** Action Bar Tabs with View Pager:
http://www.androidhive.info/2013/10/android-tab-layout-with-swipeable-views-1/

** Google Icons:
https://design.google.com/icons/

 */