package com.albert.model_biz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private Button saveInformationButton;
    private EditText userName, fullName, countryName;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserId;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child(currentUserId);
        loadingBar = new ProgressDialog(this);
        userName = findViewById(R.id.setup_user_name);
        fullName = findViewById(R.id.setup_full_name);
        countryName = findViewById(R.id.setup_country);
        saveInformationButton = findViewById(R.id.setup_information_button);
        profileImage = findViewById(R.id.setup_profile_image);

        saveInformationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetupInformation();
            }
        });

    }

    private void saveAccountSetupInformation() {
        String Username = userName.getText().toString();
        String Fullname = fullName.getText().toString();
        String Countryname = countryName.getText().toString();

        if (TextUtils.isEmpty(Username)){
            Toast.makeText(this,"Please Enter UserName",Toast.LENGTH_SHORT).show();
        } if (TextUtils.isEmpty(Fullname)){
            Toast.makeText(this,"Please Enter FullName",Toast.LENGTH_SHORT).show();
        } if (TextUtils.isEmpty(Countryname)){
            Toast.makeText(this,"Please Confirm Your Country",Toast.LENGTH_SHORT).show();
        }else{
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please Wait, While we are Creating Your New Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();
            userMap.put("username", Username);
            userMap.put("fullname", Fullname);
            userMap.put("country", Countryname);
            userMap.put("status", "Hey there, I am using this app developed by albert");
            userMap.put("gender", "none");
            userMap.put("dob", "none");
            userMap.put("relationshipstatus", "none");

            UserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this,"your account is created successfully",Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                    }else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this,"Error Occured: " + message,Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}