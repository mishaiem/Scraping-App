package com.example.scrapping_app.Screen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scrapping_app.R;
import com.example.scrapping_app.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    EditText nameInput, emailInput, passInput, confirmInput;
    TextView loginInput;
    Button signupbtn;
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.name,

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        confirmInput = findViewById(R.id.confirmInput);
        signupbtn = findViewById(R.id.signupbtn);
        loginInput = findViewById(R.id.loginInput);

        loginInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
            }
        });
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        nameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            passValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            confirmValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean nameValidation () {
        String input = nameInput.getText().toString().trim();
        if (input.equals(" ")){
            nameInput.setError("Name is required");
            return false;
        }else if(input.length()<3){
            nameInput.setError("Name at least 3 characters");
            return false;

        }else {
            nameInput.setError(null);
            return true;
        }
}
    public boolean emailValidation () {
        String input = emailInput.getText().toString().trim();
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (input.equals(" ")){
            emailInput.setError("Email Address is required");
            return false;
        }else if(!input.matches(pattern)){
            emailInput.setError("Enter Valid email address");
            return false;

        }else {
            emailInput.setError(null);
            return true;
        }
    }
    public boolean passValidation() {
        String input = passInput.getText().toString().trim();
        if (input.equals(" ")){
            passInput.setError("Password is required");
            return false;
        }else if(input.length()<8){
            passInput.setError("Password should be of at least 8 characters");
            return false;

        }else {
            passInput.setError(null);
            return true;
        }
    }
    public boolean confirmValidation() {
        String input = confirmInput.getText().toString().trim();
        String input2 = passInput.getText().toString().trim();
        if (input.equals(" ")){
            confirmInput.setError("confirm your Password!!!");
            return false;
        }else if(input.length()<8){
            confirmInput.setError("Password should be of at least 8 characters");
            return false;

        }else if(!input.equals(input2)){
            confirmInput.setError("password is not matched");
            return false;

        }else {
            confirmInput.setError(null);
            return true;
        }
    }
    public void validation(){
        boolean nameErr=false,emailErr = false,confirmErr= false ,passErr=false;
        nameErr=nameValidation();
        emailErr=emailValidation();
        passErr=passValidation();
        confirmErr=confirmValidation();

        if((nameErr&& emailErr &&passErr && confirmErr)==true){
            Toast.makeText(SignupActivity.this, "Signup", Toast.LENGTH_SHORT).show();
        }

    }

}
