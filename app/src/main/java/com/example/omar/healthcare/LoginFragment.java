package com.example.omar.healthcare;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    private LoginListener loginListener;
    private Button fbBtn, newaccountBtn, loginBtn;
    private EditText emailBox, passwordBox;
    private View fragmentLayout;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            loginListener = (LoginFragment.LoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

    public interface LoginListener {
        void firebasePasswordLogin(String email, String passowrd, Boolean firstTime);
        void firebaseFacebookLogin();
        void showNewAccountDialog();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLayout = inflater.inflate(R.layout.fragment_login, container, false);
        fbBtn = (Button) fragmentLayout.findViewById(R.id.facebook_button);
        newaccountBtn = (Button) fragmentLayout.findViewById(R.id.createnew_button);
        loginBtn = (Button) fragmentLayout.findViewById(R.id.login_button);
        emailBox = (EditText) fragmentLayout.findViewById(R.id.login_email);
        passwordBox = (EditText) fragmentLayout.findViewById(R.id.login_password);

        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginListener.firebaseFacebookLogin();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginListener.firebasePasswordLogin(emailBox.getText().toString(), passwordBox.getText().toString(), false);
            }
        });
        newaccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginListener.showNewAccountDialog();
            }
        });

        return fragmentLayout;
    }


}
