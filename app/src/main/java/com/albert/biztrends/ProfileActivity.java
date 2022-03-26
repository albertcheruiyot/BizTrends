package com.albert.biztrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
private TextView  username, userprofname, userstatus,usercountry, usergender, userrelation,userdob;
private CircleImageView userProfileImage;

private DatabaseReference profileUserRef;
private FirebaseAuth mAuth;
private  String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        username = findViewById(R.id.my_username);
        userprofname = findViewById(R.id.my_profile_full_name);
        userstatus = findViewById(R.id.my_profile_status);
        usercountry = findViewById(R.id.my_country);
        usergender = findViewById(R.id.my_gender);
        userrelation = findViewById(R.id.my_relationship_status);
        userdob = findViewById(R.id.my_dob);

        userProfileImage = findViewById(R.id.my_profile_pic);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()) {
                   String myProfileImage = snapshot.child("img").getValue().toString();
                   String myUsername = snapshot.child("username").getValue().toString();
                   String myUserprofilename = snapshot.child("fullname").getValue().toString();
                   String myProfilestatus = snapshot.child("status").getValue().toString();
                   String myDOB = snapshot.child("dob").getValue().toString();
                   String myCountry = snapshot.child("country").getValue().toString();
                   String mygender = snapshot.child("gender").getValue().toString();
                   String myTrenderStatus = snapshot.child("trenderstatus").getValue().toString();

                   Picasso.with(ProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);

                   username.setText("@"+myUsername);
                   userprofname.setText(myUserprofilename);
                   userstatus.setText(myProfilestatus);
                   usercountry.setText("Country:"+myCountry);
                   usergender.setText("Gender:"+mygender);
                   userrelation.setText("Trender status:"+myTrenderStatus);
                   userdob.setText("DOB:"+myDOB);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /** Nav Code Start**/
        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_profile);

        //perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.bottom_nav_profile:

                        return true;

                    case R.id.nav_bottom_settings:
                        startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bottom_nav_news:
                        startActivity(new Intent(getApplicationContext(),NewsActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return true;
            }

        });
        /** Nav Code end**/
    }
    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(ProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
        super.onBackPressed();
    }
}