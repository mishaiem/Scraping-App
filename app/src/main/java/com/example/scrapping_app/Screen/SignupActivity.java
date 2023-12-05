package com.example.scrapping_app.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.scrapping_app.R;
import com.example.scrapping_app.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.name,



    }
}