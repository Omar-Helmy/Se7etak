package com.example.omar.healthcare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Created by Omar on 28/04/2016.
 */
public class NewAccountDialogFragment extends DialogFragment {
    private Context context;
    private EditText email, password,username;
    private RadioButton male,female;
    private String gender;

    public void setData(Context context) {
        this.context = context;
    }

    /* The activity that creates an instance of this dialog fragment must
    * implement this interface in order to receive event callbacks.
    * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NewAccountDialogInterface {
        void onNewDialogPositiveClick(DialogFragment dialog, String email, String password, String username, String gender);
        void onNewDialogNegativeClick(DialogFragment dialog);
    }
    // Use this instance of the interface to deliver action events
    NewAccountDialogInterface newAccountDialogInterface;

    // Override the Fragment.onAttach() method to instantiate the LoginDialogInterface
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the LoginDialogInterface so we can send events to the host
            newAccountDialogInterface = (NewAccountDialogInterface) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LoginDialogInterface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View newAccountDialog = inflater.inflate(R.layout.newaccount_dialog, null);
        email = (EditText) newAccountDialog.findViewById(R.id.new_email);
        password = (EditText) newAccountDialog.findViewById(R.id.new_password);
        username = (EditText) newAccountDialog.findViewById(R.id.new_username);
        male = (RadioButton) newAccountDialog.findViewById(R.id.new_male);
        female = (RadioButton) newAccountDialog.findViewById(R.id.new_female);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(newAccountDialog)
                .setTitle("Create New Account")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if(male.isChecked())
                            gender=male.getText().toString();
                        else  if (female.isChecked())
                            gender=female.getText().toString();

                        newAccountDialogInterface.onNewDialogPositiveClick(NewAccountDialogFragment.this, email.getText().toString(),
                                password.getText().toString(), username.getText().toString(), gender);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        newAccountDialogInterface.onNewDialogNegativeClick(NewAccountDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
