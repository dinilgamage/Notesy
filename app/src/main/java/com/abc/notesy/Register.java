package com.abc.notesy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText mregisteremail,mregisterpassword,mregisterconfirmpassword;
    private Button mregisterbutton;
    private TextView mgotologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_register);

        mregisteremail=findViewById(R.id.emailInput);
        mregisterpassword=findViewById(R.id.passwordInput);
        mregisterconfirmpassword=findViewById(R.id.confirmpasswordInput);
        mregisterbutton=findViewById(R.id.registerButton);
        mgotologin=findViewById(R.id.loginPrompt);

        mgotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Register.this,Login.class);
                startActivity(intent);
            }
        });

        mregisterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mregisteremail.getText().toString().trim();
                String password=mregisterpassword.getText().toString().trim();
                String confirmpassword=mregisterconfirmpassword.getText().toString().trim();
                if(email.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(),"All fields are required", Toast.LENGTH_SHORT).show();
                }else if (password.length()<7){
                    Toast.makeText(getApplicationContext(),"Password must be at least 7 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmpassword)){
                    Toast.makeText(getApplicationContext(),"Passwords do not match", Toast.LENGTH_SHORT).show();
                }else{
                    //todo register user
                }
            }
        });
    }
}