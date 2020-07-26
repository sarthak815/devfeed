package com.example.dev_feed;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    Button register;
    EditText userid, password;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userid = (EditText)findViewById(R.id.username_register);
        password = (EditText)findViewById(R.id.password_register);
        register = (Button)findViewById(R.id.register_register);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Create Acccount");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userid.getText().toString().trim();
                String password1 = password.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    userid.setError("Invalid Email");
                    userid.setFocusable(true);
                }
                else if(password.length()<6){
                    password.setError("Password length must be greater than 6");
                    password.setFocusable(true);
                }
                else{
                    registerUser(email, password1);
                }
            }
        });

    }

    private void registerUser(String email, String password1) {

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();

                            // Sign in success, update UI with the signed-in user's informatio
                            FirebaseUser user = mAuth.getCurrentUser();

                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", email);
                            hashMap.put("phone", email);
                            hashMap.put("image", email);
                            //getting database

                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            DatabaseReference reference = database.getReference("Users");

                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(Register.this, "Registering"+ user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, feed.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.show();
                Toast.makeText(Register.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}