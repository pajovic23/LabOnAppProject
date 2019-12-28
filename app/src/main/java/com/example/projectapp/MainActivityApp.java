package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivityApp extends AppCompatActivity {

    // information about user in the moment when he calls emergency
    private UserProfile userProfile;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference profileRef = database.getReference("profiles");
    final DatabaseReference requestRef = database.getReference("requests");
    private boolean requestSent=false;
    private boolean requestStatusChanged=false;
    private String requestID;

    private ImageButton button;
    private Switch switch_button;
    private ProgressBar progess;
    private TextView progress_info;
    private FusedLocationProviderClient fusedLocationClient;
    private double GPS_logitude;
    private double GPS_latitude;

    // emergencyStatus is status of the user, everything is explained in the class Constants
    private int emergencyStatus;
    private Thread threadStateMachine;
    private Runnable state_machine=new Runnable() {
        @Override
        public void run() {
            switch (emergencyStatus){
                case Constants.STATE_FINE:
                    StateFineLayout();
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(MainActivityApp.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        GPS_latitude=location.getLatitude();
                                        GPS_logitude=location.getLongitude();
                                    }
                                }
                            });
                    if(requestSent){
                        requestSent=false;
                        emergencyStatus=Constants.STATE_EMERGENCY;
                    }
                    break;
                case Constants.STATE_EMERGENCY:
                    StateEmergencyLayout();
                    if(requestStatusChanged){
                        requestStatusChanged=false;
                        emergencyStatus=Constants.STATE_PATIENT_ACKNOWLEDGED;
                    }
                    break;
                case Constants.STATE_PATIENT_ACKNOWLEDGED:
                    StatePatientAcknowledgedLayout();
                    if(switch_button.isChecked()){
                        emergencyStatus=Constants.STATE_FINE;
                        PopUpClass popUpClass = new PopUpClass();
                        popUpClass.showPopupWindow(findViewById(android.R.id.content));
                        requestRef.child(requestID).child("state").setValue(Constants.DATABASE_STATE_DRONESEEN);
                    }
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);

        button= (ImageButton) findViewById(R.id.button_emergency);
        switch_button=(Switch) findViewById(R.id.switch_emergency_done);
        progess=(ProgressBar) findViewById(R.id.progress_drone);
        progress_info= (TextView) findViewById(R.id.progress_info);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        new Thread() {
            public void run() {
                int i=0;
                while (i++ < 1000) {
                    try {
                        runOnUiThread(state_machine);
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        //threadStateMachine=new Thread(state_machine);
        //threadStateMachine.start();
        requestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (emergencyStatus==Constants.STATE_EMERGENCY) {
                    for (final DataSnapshot user : dataSnapshot.getChildren()) {
                        if (user.getKey().equals(requestID) && user.child("state").getValue(Integer.class)==Constants.DATABASE_STATE_ACKNOWLEDGED) {
                            requestStatusChanged=true;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView UserNameText= (TextView) findViewById(R.id.UserNameText);
        UserNameText.setText(userProfile.getUserName());

        //title of the action bar
        setTitle(Html.fromHtml("<font color='#FFFFFF'>Home </font>"));

        final CheckBox check_male=findViewById(R.id.check_button_male);
        final CheckBox check_female=findViewById(R.id.check_button_female);
        final CheckBox check_speak=findViewById(R.id.check_button_speak);
        final CheckBox check_walk=findViewById(R.id.check_button_walk);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch (emergencyStatus){
                    case Constants.STATE_FINE:
                        if(check_male.isChecked() || check_female.isChecked()){
                            userProfile.setmUserFormular(new UserFormular(check_male.isChecked(),check_walk.isChecked(),check_speak.isChecked()));
                            uploadRequesttoDB();
                        }else {
                            Toast.makeText(MainActivityApp.this,"Please fill in the formular", Toast.LENGTH_SHORT).show();
                        }
                        break;
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
    private void uploadRequesttoDB(){
        final DatabaseReference newRequestRef=requestRef.push();
        requestID=newRequestRef.getKey();
        newRequestRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData
                                                            mutableData) {
                mutableData.child("state").setValue(Constants.DATABASE_STATE_NOT_SEEN);
                if (userProfile.getmUserFormular()!=null) {
                    mutableData.child("emergency_data").child("sex").setValue(userProfile.getmUserFormular().isUserSex());
                    mutableData.child("emergency_data").child("walk").setValue(!(userProfile.getmUserFormular().isUserRWalk()));
                    mutableData.child("emergency_data").child("speak").setValue(!(userProfile.getmUserFormular().isUserSpeak()));
                }
                mutableData.child("user").child("ID").setValue(userProfile.getUserID());
                mutableData.child("time").setValue(new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
                mutableData.child("GPS").child("longitude").setValue(GPS_logitude);
                mutableData.child("GPS").child("latitude").setValue(GPS_latitude);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   boolean b, @Nullable DataSnapshot
                                           dataSnapshot) {
                if (b) {
                    Toast.makeText(MainActivityApp.this,"Request sent", Toast.LENGTH_SHORT).show();
                    requestSent=true;
                } else {
                    Toast.makeText(MainActivityApp.this,"Unable to send request", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void StateFineLayout(){
        button.setImageResource(R.drawable.button_default);
        switch_button.setVisibility(View.INVISIBLE);
        progess.setVisibility(View.INVISIBLE);
        progress_info.setText(R.string.state_fine_msg);
        switch_button.setChecked(false);
    }
    private void StateEmergencyLayout(){
        button.setImageResource(R.drawable.button_pressed);
        switch_button.setVisibility(View.INVISIBLE);
        progess.setVisibility(View.VISIBLE);
        progress_info.setText(R.string.state_emergency_msg);
        progress_info.setVisibility(View.VISIBLE);
    }
    private void StatePatientAcknowledgedLayout(){
        button.setImageResource(R.drawable.button_pressed);
        switch_button.setVisibility(View.VISIBLE);
        progess.setVisibility(View.VISIBLE);
        progress_info.setText(R.string.state_patient_acknowledged);
        progress_info.setVisibility(View.VISIBLE);
    }
}
