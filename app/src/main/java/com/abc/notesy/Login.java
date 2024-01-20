package com.abc.notesy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText mloginemail,mloginpassword;
    private Button mloginbutton;
    private TextView mgotoregister,mforgotpassword;

    private FirebaseAuth firebaseAuth;

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

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

        if(firebaseUser!=null){
            finish();
            Intent intent=new Intent(Login.this,MainActivity.class);
            startActivity(intent);
        }

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
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                checkEmailVerification();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Account doesn't exist",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    private void checkEmailVerification(){
            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
            if(firebaseUser.isEmailVerified()==true){
                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                finish();
                Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),"Verify your email",Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }
        }

}