package com.albert.biztrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView commentsList;
    private ImageButton postCommentButton;
    private EditText CommentInputEditext;

    private String Post_Key,current_user_id;

    private DatabaseReference UserRef,PostsRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        mAuth = FirebaseAuth.getInstance();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(Post_Key).child("Comments");
        current_user_id = mAuth.getCurrentUser().getUid();


        commentsList = findViewById(R.id.comments_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        CommentInputEditext = findViewById(R.id.comment_input);
        postCommentButton = findViewById(R.id.post_comment_button);

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String username = snapshot.child("username").getValue().toString();

                            validateComment(username);

                            CommentInputEditext.setText("");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>()
                        .setQuery(PostsRef, Comments.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder>(options) {
            @Override
            public CommentsActivity.CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_comments_layout, parent, false);

                return new CommentsActivity.CommentsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentsActivity.CommentsViewHolder holder, int position, @NonNull Comments model) {
                // Bind the image_details object to the BlogViewHolder
                // ...
                /*holder.setFullname(model.getFullname());
                holder.setStatus(model.getStatus());
                holder.setImg(getApplicationContext(),model.getImg());*/

                holder.setUsername(model.getUsername());
                holder.setComment(model.getComment());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());


            }
        };
        firebaseRecyclerAdapter.startListening();
        commentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class  CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setUsername(String username)
        {
            TextView myUserName = mView.findViewById(R.id.comment_username);
            myUserName.setText("@" +username+"   ");
        }
        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }
        public void setDate(String date) {
            TextView myCommentDate = mView.findViewById(R.id.comment_date);
            myCommentDate.setText("Date:"+date);
        }
        public void setTime(String time) {
            TextView myCommentTime = mView.findViewById(R.id.comment_time);
            myCommentTime.setText("Time:"+time);
        }

    }


    private void validateComment(String username) {
        String commentText = CommentInputEditext.getText().toString();

        if (TextUtils.isEmpty(commentText)){
            Toast.makeText(CommentsActivity.this, "please write text to commment...", Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String RandomKey = current_user_id + saveCurrentDate +saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("comment",commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time",saveCurrentTime);
            commentsMap.put("username",username);

            PostsRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(CommentsActivity.this, "you have commented successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(CommentsActivity.this, "Error occured, try again...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}