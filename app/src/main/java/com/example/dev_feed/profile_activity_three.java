package com.example.dev_feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class profile_activity_three extends AppCompatActivity {

    RecyclerView postsRecyclerView;

    List<model_post> postList;
    AdapterPosts adapterPosts;
    String uid;
    ImageView avatartv;
    TextView nametv, emailtv, phonetv, plusonetv;


    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_three);

        avatartv = findViewById(R.id.profile_pic);
        nametv = findViewById(R.id.name_profile);
        emailtv =findViewById(R.id.email);
        phonetv =findViewById(R.id.contact_number);
        postsRecyclerView = findViewById(R.id.redirect_profile_recycler);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();

                    nametv.setText(name);
                    emailtv.setText(email);
                    phonetv.setText(phone);

                    try{
                        Picasso.get().load(image).into(avatartv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.profile_pic).into(avatartv);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        checkUserStatus();
        loadHistPosts();

    }

    private void loadHistPosts() {
        postList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    model_post myposts = ds.getValue(model_post.class);

                    postList.add(myposts);

                    adapterPosts = new AdapterPosts(profile_activity_three.this, postList);

                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_activity_three.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHistPosts(final String searchQuery){

        LinearLayoutManager layoutManager = new LinearLayoutManager(profile_activity_three.this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postsRecyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        Query query = ref.orderByChild("uid").equalTo(uid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    model_post myposts = ds.getValue(model_post.class);

                    if(myposts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase())|| myposts.getpDescr().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(myposts);
                    }
                    adapterPosts = new AdapterPosts(profile_activity_three.this, postList);

                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_activity_three.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkUserStatus(){

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user != null){

            uid = user.getUid();


        }

        else{
            startActivity(new Intent(profile_activity_three.this, MainActivity.class));
            finish();
        }
    }


}