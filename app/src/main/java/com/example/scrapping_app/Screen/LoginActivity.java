package com.example.scrapping_app.Screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.scrapping_app.DashboardActivity;
import com.example.scrapping_app.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText passInput,emailInput;
    CheckBox remember;
    TextView forgotpass, signup;
    Button loginBtn;

    FirebaseAuth myAuth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.emailInput);
        passInput = findViewById(R.id.passInput);
        remember = findViewById(R.id.remember);
        forgotpass = findViewById(R.id.forgotpass);
        loginBtn = findViewById(R.id.loginBtn);
        signup = findViewById(R.id.signup);

        sharedPreferences=getSharedPreferences("myData",MODE_PRIVATE);
        editor=sharedPreferences.edit();

        if (sharedPreferences.getString("Login status","").equals("true")){
            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
            finish();
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    public boolean emailValidation () {
        String input = emailInput.getText().toString().trim();
        String pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (input.equals("")){
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
        if (input.equals("")){
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

    public void validation(){
        boolean emailErr = false, passErr=false;
        emailErr=emailValidation();
        passErr=passValidation();

        if((emailErr && passErr)==true){
            Dialog loaddialog=new Dialog(LoginActivity.this);
            loaddialog.setContentView(R.layout.dialog_loading);
            loaddialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            loaddialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loaddialog.getWindow().setGravity(Gravity.CENTER);
            loaddialog.setCancelable(false);
            loaddialog.setCanceledOnTouchOutside(false);
            TextView message = loaddialog.findViewById(R.id.message);
            message.setText("Loading.....");
            loaddialog.show();
            myAuth.signInWithEmailAndPassword(emailInput.getText().toString().trim(),passInput.getText().toString().trim())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            loaddialog.dismiss();
                            Dialog alertdialog=new Dialog(LoginActivity.this);
                            alertdialog.setContentView(R.layout.dialog_success);
                            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertdialog.getWindow().setGravity(Gravity.CENTER);
                            alertdialog.setCancelable(false);
                            alertdialog.setCanceledOnTouchOutside(false);
                            TextView message = alertdialog.findViewById(R.id.message);
                            message.setText("Login Successfully!!!!");
                            alertdialog.show();
                            if (remember.isChecked()){
                                editor.putString("Loginstatus","true");
                                editor.commit();
                            }else {
                                editor.putString("Loginstatus","false");
                                editor.commit();
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertdialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                }
                            },2000);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaddialog.dismiss();
                            Dialog alertdialog=new Dialog(LoginActivity.this);
                            alertdialog.setContentView(R.layout.dialog_success);
                            alertdialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertdialog.getWindow().setGravity(Gravity.CENTER);
                            alertdialog.setCancelable(false);
                            TextView message = alertdialog.findViewById(R.id.message);
                            message.setText("Email Address OR password is wrong!!!");
                            alertdialog.setCanceledOnTouchOutside(false);
                            alertdialog.show();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertdialog.dismiss();
                                }
                            },2000);
                        }
                    });

        }

    }
}