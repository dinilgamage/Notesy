package com.abc.notesy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText mloginemail,mloginpassword;
    private Button mloginbutton;
    private TextView mgotoregister,mforgotpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        mloginemail=findViewById(R.id.emailInput);
        mloginpassword=findViewById(R.id.passwordInput);
        mloginbutton=findViewById(R.id.loginButton);
        mgotoregister=findViewById(R.id.registerPrompt);
        mforgotpassword=findViewById(R.id.forgotPassword);

        mgotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,Register.class);
                startActivity(intent);
            }
        });

        mforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,ForgotPassword.class);
                startActivity(intent);
            }
        });

        mloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mloginemail.getText().toString().trim();
                String password=mloginpassword.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_SHORT).show();

                }else{
                    //todo login user
                }
            }
        });

    }
}