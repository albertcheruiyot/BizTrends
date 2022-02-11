package com.albert.model_biz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private Button saveInformationButton;
    private EditText userName, fullName, countryName;
    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        userName = findViewById(R.id.setup_user_name);
        fullName = findViewById(R.id.setup_full_name);
        countryName = findViewById(R.id.setup_country);
        saveInformationButton = findViewById(R.id.setup_information_button);
        profileImage = findViewById(R.id.setup_profile_image);

    }
}