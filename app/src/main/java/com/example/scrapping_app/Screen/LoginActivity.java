package com.example.scrapping_app.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.scrapping_app.R;

public class LoginActivity extends AppCompatActivity {
    EditText passInput,emailInput;
    CheckBox rememberme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
    }
}