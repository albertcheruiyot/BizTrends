package com.albert.biztrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {
//
    private ImageView postImage;
    private TextView PostDescription;
    private Button updatePostbtn, deletePostbtn;

    private  String PostKey, currentUserId, DatabaseUserId,description,image;

    private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        PostKey = getIntent().getExtras().get("PostKey").toString();
        ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();


        postImage = findViewById(R.id.click_post_image);
        PostDescription = findViewById(R.id.click_post_description);
        //DeletePostButton = findViewById(R.id.delete_post_btn);
        //UpdatePostButton = findViewById(R.id.update_post_button);
        updatePostbtn = (Button) findViewById(R.id.edit_post_btn);
        deletePostbtn = (Button) findViewById(R.id.delete_post_btn);

        updatePostbtn.setVisibility(View.INVISIBLE);
        deletePostbtn.setVisibility(View.INVISIBLE);

        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists())
               {
                   description = snapshot.child("description") .getValue().toString();
                   image = snapshot.child("postimage") .getValue().toString();
                   DatabaseUserId = snapshot.child("uid").getValue().toString();
                   PostDescription.setText(description);
                   Picasso.with(ClickPostActivity.this).load(image).placeholder(R.drawable.profile).into(postImage);
                   if (currentUserId.equals(DatabaseUserId)){
                       updatePostbtn.setVisibility(View.VISIBLE);
                       deletePostbtn.setVisibility(View.VISIBLE);
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        deletePostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletePost();
            }
        });
        updatePostbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCurrentPost();
            }
        });
    }

    private void EditCurrentPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");
        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickPostRef.child("description").setValue(inputField.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Post Updated Successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_green_dark);
    }

    private void DeletePost() {
        ClickPostRef.removeValue();
        SendUserToMainActivity();
        Toast.makeText(ClickPostActivity.this, "Post Deleted", Toast.LENGTH_SHORT).show();
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ClickPostActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}