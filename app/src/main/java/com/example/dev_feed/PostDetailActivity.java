package com.example.dev_feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.text.format.DateFormat.format;

public class PostDetailActivity extends AppCompatActivity {


    String myUid, myEmail, myName, myDp, postId, pLikes, hisDp, hisName;
    ImageView uPictureIv, pImageIv;
    TextView unameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
    Button likebtn, sharebtn;


    boolean mProcessComment = false;
    boolean mProcessLike = false;
    ProgressDialog pd;

    LinearLayout profileLayout;

    EditText commentEt;
    ImageButton commentbtn;
    ImageView cAvatarIv;

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");


        pImageIv = findViewById(R.id.dp_comment_post);
        cAvatarIv = findViewById(R.id.pic_comment);
        pTimeTv = findViewById(R.id.time_stamp_comment);
        pTitleTv = findViewById(R.id.title_comment);
        pLikesTv = findViewById(R.id.likes_comment);
        pDescriptionTv = findViewById(R.id.description_comment);
        unameTv = findViewById(R.id.name_comment);

        likebtn = findViewById(R.id.like_button_comment);
        sharebtn = findViewById(R.id.share_comment);
        pCommentsTv = findViewById(R.id.comment_count);
        commentbtn = findViewById(R.id.comment_btn_main);
        commentEt = findViewById(R.id.commentEt);
        uPictureIv = findViewById(R.id.comments_dp);

        profileLayout = findViewById(R.id.profile_layout_comment);
        //recyclerView = findViewById(R.id.recycler_comments);

        loadPostInfo();
        checkUserStatus();

        loadUserInfo();


        commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

    }

//    private void loadComments() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//
//        recyclerView.setLayoutManager(layoutManager);
//
//        commentList = new ArrayList<>();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                commentList.clear();
//                for(DataSnapshot ds: snapshot.getChildren()){
//                    ModelComment modelComment = ds.getValue(ModelComment.class);
//
//                    commentList.add(modelComment);
//
//                    adapterComments = new AdapterComments(getApplicationContext(), commentList);
//
//                    recyclerView.setAdapter(adapterComments);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void likePost() {
        mProcessLike = true;

        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Post");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessLike){
                    if(snapshot.child(postId).hasChild(myUid)){

                        postsRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes)-1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;

                    }
                    else{

                        postsRef.child(postId).child("pLikes").setValue("" + (pLikes+1));
                        likesRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike = false;


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this);
        pd.setMessage("Adding Comment...");

        final String comment = commentEt.getText().toString().trim();

        if(TextUtils.isEmpty(comment)){

            Toast.makeText(this, "Comment is empty..", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child("Comments");

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timeStamp", timeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uName", myName);

        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "Comment Added...", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void updateCommentCount() {
        mProcessComment = true;
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessComment){
                    String comments = ""+ snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadUserInfo() {
        Query myref = FirebaseDatabase.getInstance().getReference("Users");
        myref.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    myName = ""+ds.child("name").getValue();
                    myDp = "" + ds.child("image").getValue();

                    try {
                        Picasso.get().load(myDp).placeholder(R.drawable.profile_pic).into(cAvatarIv);

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.profile_pic).into(cAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String pTitle = ""+ds.child("pTitle").getValue();
                    String pDescr = ""+ds.child("pDescr").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    String pImage = ""+ds.child("pImage").getValue();
                    String uid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    hisDp = ""+ds.child("uDp").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    String commentCount = ""+ds.child("pComments").getValue();

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = format("dd/MM/yyyy hh:mm aa", calendar).toString();

                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText(pLikes + "Likes");
                    pTimeTv.setText(pTime);
                    pCommentsTv.setText(commentCount + " Comments");

                    unameTv.setText(hisName);

                    if(pImage.equals("noImage")){
                        pImageIv.setVisibility(View.GONE);
                    }
                    try {
                        Picasso.get().load(pImage).into(pImageIv);
                    }
                    catch (Exception e){

                    }

                }

                try {
                    Picasso.get().load(hisDp).placeholder(R.drawable.profile_pic).into(uPictureIv);
                }catch (Exception e) {
                    Picasso.get().load(R.drawable.profile_pic).into(uPictureIv);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void checkUserStatus(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){

            myEmail = user.getEmail();
            myUid = user.getUid();


        }

        else{
            startActivity(new Intent(PostDetailActivity.this, MainActivity.class));
            finish();
        }
    }

    //    private void setLikes(MyHolder holder, final String postKey) {
//        likesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(postKey).hasChild(myUid)){
//
//                }
//                else{
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        })
//    }
}