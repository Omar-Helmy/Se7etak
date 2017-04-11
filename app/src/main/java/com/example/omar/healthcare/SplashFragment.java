package com.example.omar.healthcare;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {


    private LoginListener loginListener;

    public SplashFragment() {
        // Required empty public constructor
    }

    // Container Activity must implement this interface
    public interface LoginListener {
        void onError();     // failed to login, begin LoginFragment
        void firebasePasswordLogin(String email, String passowrd, Boolean firstTime);
        void firebaseFacebookLogin();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            loginListener = (SplashFragment.LoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // check for user pre login:
        if(!Se7etak.sharedPref.getString("userID","null").equals("null")){
            // logged before, try login now
            if(Se7etak.sharedPref.getString("provider","null").equals("password")){
                // password login, try login first:
                loginListener.firebasePasswordLogin(Se7etak.sharedPref.getString("email", "null"),
                        Se7etak.sharedPref.getString("password", "null"), false);

            }else if(Se7etak.sharedPref.getString("provider","null").equals("facebook")){
                // facebook login:
                loginListener.firebaseFacebookLogin();
            }
        }else{
            // must login or create account:
            loginListener.onError();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


}
