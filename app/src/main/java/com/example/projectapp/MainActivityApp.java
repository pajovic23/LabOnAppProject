package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivityApp extends AppCompatActivity {

    // information about user in the moment when he calls emergency
    private UserFormular userFormular=null;
    private UserProfile userProfile;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference profileRef = database.getReference("profiles");

    // emergencyStatus is status of the user, everything is explained in the class Constants
    private int emergencyStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        Intent intent=getIntent();
        userProfile=new UserProfile(intent.getStringExtra("USER_PROFILE_TRANSFER"));
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot user : dataSnapshot.getChildren()) {
                    if(user.getKey().equals(userProfile.getUserID())){
                        userProfile.setUserPassword(user.child("password").getValue(String.class));
                        userProfile.setUserName(user.child("username").getValue(String.class));
                        TextView UserNameText= (TextView) findViewById(R.id.UserNameText);
                        UserNameText.setText(userProfile.getUserName());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        emergencyStatus=Constants.STATE_FINE;

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
                        if(userFormular==null){
                            Toast.makeText(MainActivityApp.this,"Please fill in the formular", Toast.LENGTH_SHORT).show();
                        }else {
                            button.setImageResource(R.drawable.button_pressed);
                            emergencyStatus = Constants.STATE_EMERGENCY;
                            switch_button.setVisibility(View.VISIBLE);
                            progess.setVisibility(View.VISIBLE);
                            progress_info.setText(R.string.state_emergency_msg);
                            progress_info.setVisibility(View.VISIBLE);
                            break;
                        }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.active_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_edit_profle:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
