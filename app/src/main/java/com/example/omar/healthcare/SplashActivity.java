package com.example.omar.healthcare;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.omar.healthcare.util.SystemUiHider;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity
        implements SplashFragment.LoginListener, LoginFragment.LoginListener,
        NewAccountDialogFragment.NewAccountDialogInterface{

    /** Duration of wait **/
    private final static int SPLASH_DISPLAY_LENGTH = 500;
    private Context context = this;
    private CallbackManager callbackManager;
    private FragmentTransaction fragmentTransaction;
    private SplashFragment splashFragment;
    private LoginFragment loginFragment;


    /**onCreate Activity here**/
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnected())) {
            Toast.makeText(getApplicationContext(), "No Network Connection", Toast.LENGTH_SHORT).show();
            Se7etak.logged = false;
            startMainActivity(0);
        }

        // fragment manager and transaction init:
        fragmentTransaction = getFragmentManager().beginTransaction();
        splashFragment = new SplashFragment();
        loginFragment = new LoginFragment();

        callbackManager = CallbackManager.Factory.create();
        // check for user pre login:
        if(!Se7etak.sharedPref.getString("userID","null").equals("null")){
            // logged before, try login now
            fragmentTransaction.replace(R.id.fragment_holder,splashFragment);
        }else{
            // must login or create account:
            Toast.makeText(context, "Hello, Please login or create account!", Toast.LENGTH_SHORT).show();
            fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivity(int SPLASH_DISPLAY_LENGTH){
        // New Handler to start the MainActivity and close this SplashScreen after some seconds.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                        /* Create an Intent that will start the MainActivity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                //To prevent the user from using the back button to go back to the Login activity
                // you have to finish() the activity after starting a new one.
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /***********************************************************************************************
     *  Methods declaration *
     *  Methods overridden from Interface *
     **********************************************************************************************/

    @Override
    public void onError() {
        fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
        fragmentTransaction.commit();
    }



    @Override
    public void showNewAccountDialog() {
        NewAccountDialogFragment newAccountDialogFragment = new NewAccountDialogFragment();
        newAccountDialogFragment.setData(context);
        newAccountDialogFragment.show(getFragmentManager(), "newAccount");
    }

    @Override
    public void onNewDialogPositiveClick(DialogFragment dialog, String email, String password, String username, String gender) {

        firebaseCreateNewAccount(email, password, username, gender);
    }

    @Override
    public void onNewDialogNegativeClick(DialogFragment dialog) {

    }
    @Override
    public void firebasePasswordLogin(final String email, final String password, final Boolean isFirstTime){
        Se7etak.rootRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                //System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                // update shared pref:
                // store new username and value in shared pref:
                SharedPreferences.Editor editor = Se7etak.sharedPref.edit(); // request editing shared pref file
                editor.putString("userID", authData.getUid());
                editor.putString("provider", authData.getProvider());
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();
                // create user node
                // create user nodes
                Se7etak.createUserNode(authData.getUid());
                if (isFirstTime)
                    Se7etak.userInfoNode.updateChildren((HashMap<String, Object>) Se7etak.sharedPref.getAll());
                setUsernameListener();
                setPictureListener();
                //Toast.makeText(context, "Login Succeed!", Toast.LENGTH_SHORT).show();
                startMainActivity(SPLASH_DISPLAY_LENGTH);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error, start LoginActivity
                Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                onError();
            }
        });
    }

    @Override
    public void firebaseFacebookLogin(){
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        AccessToken accessToken = loginResult.getAccessToken();
                        if (accessToken != null) {
                            Se7etak.rootRef.authWithOAuthToken("facebook", accessToken.getToken(), new Firebase.AuthResultHandler() {
                                @Override
                                public void onAuthenticated(AuthData authData) {
                                    // The Facebook user is now authenticated with your Firebase app
                                    // create user nodes:
                                    Se7etak.createUserNode(authData.getUid());
                                    // update shared pref:
                                    // store new username and value in shared pref:
                                    SharedPreferences.Editor editor = Se7etak.sharedPref.edit(); // request editing shared pref file
                                    editor.putString("email", authData.getProviderData().get("email").toString());
                                    editor.putString("userID", authData.getUid());
                                    editor.putString("name", authData.getProviderData().get("displayName").toString());
                                    editor.putString("provider", authData.getProvider());
                                    editor.apply();
                                    // each time, update server data:
                                    Se7etak.userInfoNode.updateChildren((HashMap<String, Object>) Se7etak.sharedPref.getAll());
                                    setUsernameListener();
                                    setPictureListener();
                                    //Toast.makeText(context, "Login Succeed!", Toast.LENGTH_SHORT).show();
                                    startMainActivity(SPLASH_DISPLAY_LENGTH);
                                }

                                @Override
                                public void onAuthenticationError(FirebaseError firebaseError) {
                                    // there was an error
                                    Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
                                }
                            });
                        } else {/* Logged out of Facebook so do a logout from the Firebase app */
                            Se7etak.rootRef.unauth();
                            fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        fragmentTransaction.replace(R.id.fragment_holder, loginFragment);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
                        fragmentTransaction.replace(R.id.fragment_holder, loginFragment);

                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void setUsernameListener(){
        // listener on number of requests online:
        Se7etak.userInfoNode.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                SharedPreferences.Editor editor = Se7etak.sharedPref.edit(); // request editing shared pref file
                editor.putString("name", snapshot.getValue().toString());
                editor.apply();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private void setPictureListener(){
        // listener on number of requests online:
        Se7etak.userInfoNode.child("picture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                SharedPreferences.Editor editor = Se7etak.sharedPref.edit(); // request editing shared pref file
                editor.putString("picture", snapshot.getValue().toString());
                editor.apply();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void firebaseCreateNewAccount(final String email, final String password, final String username, final String gender){

        Se7etak.rootRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                // store new username and value in shared pref:
                SharedPreferences.Editor editor = Se7etak.sharedPref.edit(); // request editing shared pref file
                editor.putString("name", username);
                editor.putString("gender",gender);
                editor.apply();
                //Creating an account will not log that new account in, so I must call LoginActivity
                firebasePasswordLogin(email,password, true);
                Toast.makeText(context, "Creating new account Succeed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                Toast.makeText(context, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
