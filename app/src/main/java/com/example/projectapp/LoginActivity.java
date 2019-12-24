package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private UserProfile userProfile;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference profileRef = database.getReference("profiles");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button Login_button=(Button) findViewById(R.id.button_login);
        Login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().setValue("Hello, World!");
                final String usernameInput = ((EditText) findViewById(R.id.text_username))
                        .getText().toString();
                final String passwordInput = ((EditText) findViewById(R.id.text_password))
                        .getText().toString();
                profileRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean notMember = true;
                        for (final DataSnapshot user : dataSnapshot.getChildren()) {
                            String usernameDatabase = user.child("username")
                                    .getValue(String.class);
                            String passwordDatabase = user.child("password")
                                    .getValue(String.class);
                            if (usernameInput.equals(usernameDatabase)
                                    && passwordInput.equals(passwordDatabase)) {
                                userProfile=new UserProfile(Integer.parseInt(user.getKey()));
                                userProfile.setUserName(usernameDatabase);
                                notMember = false;
                                break;
                            }
                        }
                        if (notMember) {
                            Log.i("nes", String.valueOf(profileRef));
                            TextView Login_msg=(TextView)findViewById(R.id.login_msg);
                            Login_msg.setText(R.string.user_authentication_error);
                            Login_msg.setVisibility(View.VISIBLE);
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivityApp.class);
                            intent.putExtra("USER_PROFILE_TRANSFER", userProfile.getUserID());
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
