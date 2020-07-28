package com.example.dev_feed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.icu.text.DateFormat.*;
import static android.text.format.DateFormat.format;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder>{

    Context context;
    List<model_post> postList;

    String myUid;

    private DatabaseReference likesRef;
    private DatabaseReference postsRef;

    Boolean mProcessLike = false;


    public AdapterPosts(Context context, List<model_post> postList){
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int i) {

        final String uid = postList.get(i).getUid();
        String uEmail= postList.get(i).getuEmail();
        String uName = postList.get(i).getuName();
        String uDp = postList.get(i).getuDp();
        final String pId = postList.get(i).getpId();
        String pTitle = postList.get(i).getpTitle();
        String pDescription = postList.get(i).getpDescr();
        String pImage = postList.get(i).getpImage();
        String pTimeStamp = postList.get(i).getpTime();
        String pLikes = postList.get(i).getpLikes();


        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = format("dd/MM/yyyy hh:mm aa", calendar).toString();


        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.pLikesTv.setText(pLikes+ "Likes");

        //setLikes(holder, pId);
        try {
            Picasso.get().load(uDp).placeholder(R.drawable.person).into(holder.uPictureIv);
        }
        catch (Exception e){

        }
        if(pImage.equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }
        try {
            Picasso.get().load(pImage).into(holder.pImageIv);
        }
        catch (Exception e){

        }
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int pLikes = Integer.parseInt(postList.get(i).getpLikes());
                mProcessLike = true;

                final String postIde = postList.get(i).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike){
                            if(snapshot.child(postIde).hasChild(myUid)){

                                postsRef.child(postIde).child("pLikes").setValue("" + (pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else{

                                postsRef.child(postIde).child("pLikes").setValue("" + (pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        holder.commentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        });
        holder.sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });

        holder.profileLayut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,profile_activity_three.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);

            }
        });


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

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pDescriptionTv, pLikesTv, pTitleTv, pCommentsTv;
        ImageButton moreBtn;
        Button likebtn, commentbtn, sharebtn;
        LinearLayout profileLayut;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uPictureIv = itemView.findViewById(R.id.dp_feed);
            pImageIv = itemView.findViewById(R.id.pic_feed);
            uNameTv = itemView.findViewById(R.id.name_feed);
            pTimeTv = itemView.findViewById(R.id.time_stamp_feed);
            pDescriptionTv = itemView.findViewById(R.id.description_feed);
            pLikesTv = itemView.findViewById(R.id.likes_feed);
            moreBtn = itemView.findViewById(R.id.more_options_feed);
            likebtn = itemView.findViewById(R.id.like_button_feed);
            commentbtn = itemView.findViewById(R.id.comments_button_feed);
            sharebtn = itemView.findViewById(R.id.share_feed);
            pTitleTv = itemView.findViewById(R.id.title_feed);
            profileLayut = itemView.findViewById(R.id.profile_layout);
            pCommentsTv = itemView.findViewById(R.id.comment_count);

        }
    }
}
