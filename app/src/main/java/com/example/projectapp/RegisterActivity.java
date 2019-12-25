package com.example.projectapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class RegisterActivity extends AppCompatActivity {
    final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    final static DatabaseReference profileGetRef = database.getReference("profiles");
    private static DatabaseReference profileRef = profileGetRef.push();
    UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button button_register=(Button) findViewById(R.id.button_register_user);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String usernameInput = ((EditText) findViewById(R.id.register_username))
                        .getText().toString();
                final String passwordInput = ((EditText) findViewById(R.id.password_register))
                        .getText().toString();
                if(usernameInput.isEmpty() || passwordInput.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill both fields", Toast.LENGTH_SHORT).show();
                }else{
                    userProfile=new UserProfile(profileRef.getKey());
                    userProfile.setUserName(usernameInput);
                    userProfile.setUserPassword(passwordInput);
                    addProfileToFirebaseDB();
                }
            }
        });
    }
    private void addProfileToFirebaseDB() {
        profileRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData
                                                            mutableData) {
                mutableData.child("username").setValue(userProfile.getUserName());
                mutableData.child("password").setValue(userProfile.getUserPassword());
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   boolean b, @Nullable DataSnapshot
                                           dataSnapshot) {
                if (b) {
                    Toast.makeText(RegisterActivity.this,"Registration successful", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,"Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
