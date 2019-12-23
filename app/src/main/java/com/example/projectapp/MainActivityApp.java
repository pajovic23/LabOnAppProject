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

    // emergencyStatus is status of the user, if nothing is happening, it will be 0,
    // if user has called emergency it will be 1, and if the problem is resolved and the user
    // could see a drone it is 2.
    private int emergencyStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        emergencyStatus=0;
        userProfile=new UserProfile(1);

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
        progress_info.setText("Press a button to ask for help");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (emergencyStatus){
                    case 0:
                        button.setImageResource(R.drawable.button_pressed);
                        emergencyStatus=1;
                        switch_button.setVisibility(View.VISIBLE);
                        progess.setVisibility(View.VISIBLE);
                        progress_info.setText("Request sent, help is on the way");
                        progress_info.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        button.setImageResource(R.drawable.button_default);
                        switch_button.setVisibility(View.INVISIBLE);
                        progess.setVisibility(View.INVISIBLE);
                        progress_info.setText("Press a button to ask for help");
                        switch_button.setChecked(false);
                        emergencyStatus=0;
                        break;
                }
            }
        });
        switch_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (emergencyStatus==1 && switch_button.isChecked()){
                    emergencyStatus=2;
                    button.setImageResource(R.drawable.button_disabled);
                    progress_info.setText("Glad that we could help!");
                    progess.setVisibility(View.INVISIBLE);
                }
                if (emergencyStatus==2 && (!switch_button.isChecked())){
                    button.setImageResource(R.drawable.button_default);
                    progess.setVisibility(View.INVISIBLE);
                    emergencyStatus=0;
                }
            }
        });



    }
}
