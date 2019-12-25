package com.example.projectapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivityApp extends AppCompatActivity {

    // information about user in the moment when he calls emergency
    private UserFormular userFormular;
    private UserProfile userProfile;

    // emergencyStatus is status of the user, everything is explained in the class Constants
    private int emergencyStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        emergencyStatus=Constants.STATE_FINE;
        userProfile=new UserProfile("1");

        TextView UserNameText= (TextView) findViewById(R.id.UserNameText);
        UserNameText.setText(userProfile.getUserName());

        //title of the action bar
        setTitle(Html.fromHtml("<font color='#FFFFFF'>Home </font>"));

        final ImageButton button= (ImageButton) findViewById(R.id.button_emergency);
        final Switch switch_button=(Switch) findViewById(R.id.switch_emergency_done);
        final ProgressBar progess=(ProgressBar) findViewById(R.id.progress_drone);
        progess.setVisibility(View.INVISIBLE);
        switch_button.setVisibility(View.INVISIBLE);
        final TextView progress_info= (TextView) findViewById(R.id.progress_info);
        progress_info.setText(R.string.state_fine_msg);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (emergencyStatus){
                    case Constants.STATE_FINE:
                        button.setImageResource(R.drawable.button_pressed);
                        emergencyStatus=Constants.STATE_EMERGENCY;
                        switch_button.setVisibility(View.VISIBLE);
                        progess.setVisibility(View.VISIBLE);
                        progress_info.setText(R.string.state_emergency_msg);
                        progress_info.setVisibility(View.VISIBLE);
                        break;
                    case Constants.STATE_EMERGENCY:
                        button.setImageResource(R.drawable.button_default);
                        switch_button.setVisibility(View.INVISIBLE);
                        progess.setVisibility(View.INVISIBLE);
                        progress_info.setText(R.string.state_fine_msg);
                        switch_button.setChecked(false);
                        emergencyStatus=Constants.STATE_FINE;
                        break;
                }
            }
        });

        switch_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (emergencyStatus==Constants.STATE_EMERGENCY && switch_button.isChecked()){
                    emergencyStatus=Constants.STATE_PATIENT_OBSERVED;
                    button.setImageResource(R.drawable.button_disabled);
                    progress_info.setText(R.string.state_patient_observed_msg);
                    progess.setVisibility(View.INVISIBLE);
                }
                if (emergencyStatus==Constants.STATE_PATIENT_OBSERVED && (!switch_button.isChecked())){
                    button.setImageResource(R.drawable.button_default);
                    progess.setVisibility(View.INVISIBLE);
                    emergencyStatus=Constants.STATE_FINE;
                }
            }
        });



    }
}
