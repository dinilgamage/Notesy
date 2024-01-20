package com.abc.notesy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPassword extends AppCompatActivity {

    private EditText mforgotpasswordemail;
    private Button mpasswordrecoverbutton;
    private TextView mgobacktologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_forgot_password);


        mpasswordrecoverbutton=findViewById(R.id.recoverButton);
        mforgotpasswordemail=findViewById(R.id.emailInput);
        mgobacktologin=findViewById(R.id.backToLogin);

        mgobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgotPassword.this,Login.class);
                startActivity(intent);
            }
        });

        mpasswordrecoverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mforgotpasswordemail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter your registered email first", Toast.LENGTH_SHORT).show();
                } else {
                    //todo send mail
                }
            }
        });

    }
}