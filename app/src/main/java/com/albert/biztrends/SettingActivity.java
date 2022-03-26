package com.albert.biztrends;

import static com.albert.biztrends.SetupActivity.Gallery_Pick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.ehsanmashhadi.library.model.Country;
import com.ehsanmashhadi.library.view.CountryPicker;
import com.ehsanmashhadi.library.view.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText username, userprofname, userstatus, usergender, userrelation,userdob;
    private TextView usercountry;
    private Button updateaccountinfobtn;

    private CircleImageView userprofimage;

    private ProgressDialog loadingBar;

    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;

    DatePickerDialog.OnDateSetListener setListener;


    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        loadingBar = new ProgressDialog(this);


        username = findViewById(R.id.settings_username);
        userprofname = findViewById(R.id.settings_profile_full_name);
        userstatus = findViewById(R.id.settings_status);
        usercountry = findViewById(R.id.settings_country);
        usergender = findViewById(R.id.settings_gender);
        userrelation = findViewById(R.id.settings_relationship_status);
        userdob = findViewById(R.id.settings_dob);

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        userdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SettingActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        ,setListener,year,month,day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.setTitle("Date of Birth");
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                userdob.setText(date);
                //Toast.makeText(SettingActivity.this, "Selected dob: " + date, Toast.LENGTH_LONG).show();
            }
        };

        updateaccountinfobtn = findViewById(R.id.update_account_settings_buttons);

        userprofimage = findViewById(R.id.settings_profile_image);

        userprofimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        Button button = findViewById(R.id.button_display);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display();
            }
        });



        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String myProfileImage = snapshot.child("img").getValue().toString();
                    String myUsername = snapshot.child("username").getValue().toString();
                    String myUserprofilename = snapshot.child("fullname").getValue().toString();
                    String myProfilestatus = snapshot.child("status").getValue().toString();
                    String myDOB = snapshot.child("dob").getValue().toString();
                    String myCountry = snapshot.child("country").getValue().toString();
                    String mygender = snapshot.child("gender").getValue().toString();
                    String myTrenderStatus = snapshot.child("trenderstatus").getValue().toString();

                    Picasso.with(SettingActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userprofimage);

                    username.setText(myUsername);
                    userprofname.setText(myUserprofilename);
                    userstatus.setText(myProfilestatus);
                    usercountry.setText(myCountry);
                    usergender.setText(mygender);
                    userrelation.setText(myTrenderStatus);
                    userdob.setText(myDOB);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateaccountinfobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
        /** Nav Code Start**/
        //initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_navigation);

        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.nav_bottom_settings);

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
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_bottom_settings:

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
    private void display() {


        CountryPicker countryPicker = new CountryPicker.Builder(this).setCountrySelectionListener(new RecyclerViewAdapter.OnCountryClickListener() {

            @Override
            public void onCountrySelected(Country country) {
                usercountry.setText(country.getName());
                Toast.makeText(SettingActivity.this, "Selected Country: " + country.getName(), Toast.LENGTH_LONG).show();
            }
        }).showingFlag(true).enablingSearch(true).build();

        countryPicker.show(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
                /*filePath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUrl = uri.toString();
                                        // complete the rest of your code
                                        Intent selfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                        startActivity(selfIntent);

                                        Toast.makeText(SetupActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                });

                            }
                        });*/
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                           Toast.makeText(SettingActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                            //final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            final Task<Uri> downloadUrl = Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl();

                            SettingsUserRef.child("img").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SettingActivity.this, SettingActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SettingActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                //loadingBar.dismiss();
            }
        }
    }

    private void ValidateAccountInfo() {
        String userName = username.getText().toString();
        String userProfName = userprofname.getText().toString();
        String userStatus = userstatus.getText().toString();
        String userCountry = usercountry.getText().toString();
        String userGender = usergender.getText().toString();
        String userRelation = userrelation.getText().toString();
        String userDob = userdob.getText().toString();

        if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userDob))
        {
            Toast.makeText(this, "Please write your date of birth...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userRelation))
        {
            Toast.makeText(this, "Please write your relationship status...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userGender))
        {
            Toast.makeText(this, "Please write your gender...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userProfName))
        {
            Toast.makeText(this, "Please write your profile name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userStatus))
        {
            Toast.makeText(this, "Please write your status...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userCountry))
        {
            Toast.makeText(this, "Please write your country...", Toast.LENGTH_SHORT).show();
        }
        else {
            UpdateAccountInformation(userName, userProfName, userStatus, userDob, userCountry, userGender, userRelation );
        }
    }

    private void UpdateAccountInformation(String userName, String userProfName, String userStatus, String userDob, String userCountry, String userGender, String userRelation) {
        HashMap userMap = new HashMap();
        userMap.put("username",userName);
        userMap.put("fullname",userProfName);
        userMap.put("status",userStatus);
        userMap.put("dob",userDob);
        userMap.put("country",userCountry);
        userMap.put("gender",userGender);
        userMap.put("relationshipstatus",userRelation);

        SettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    SendUserToMainactivity();
                    Toast.makeText(SettingActivity.this,"Account settings updated successfully...",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(SettingActivity.this,"Error Occured, while updating account settings information...",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendUserToMainactivity() {
        Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
        super.onBackPressed();
    }
}