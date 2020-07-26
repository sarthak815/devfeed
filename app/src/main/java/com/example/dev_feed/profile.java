package com.example.dev_feed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profile extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button logout;

    TextView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile = (TextView)findViewById(R.id.username_profile);
        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUserStatus();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        checkUserStatus();
    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){
            profile.setText(user.getEmail());

        }

        else{
            startActivity(new Intent(profile.this, MainActivity.class));
            finish();
        }
    }

    protected void OnStart(){
        checkUserStatus();
        super.onStart();
    }
}