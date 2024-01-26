package com.abc.notesy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText mforgotpasswordemail;
    private Button mpasswordrecoverbutton;
    private TextView mgobacktologin;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            boolean isDarkTheme = (uiModeManager != null && uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES);
            if (isDarkTheme) {
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_dark));
            } else {
                window.setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_light));
            }
        }
        setContentView(R.layout.activity_forgot_password);


        mpasswordrecoverbutton=findViewById(R.id.recoverButton);
        mforgotpasswordemail=findViewById(R.id.emailInput);
        mgobacktologin=findViewById(R.id.backToLogin);

        firebaseAuth=FirebaseAuth.getInstance();

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
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {


                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Password reset email sent",Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(ForgotPassword.this,Login.class));
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(),"Error in sending password reset email",Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }
            }
        });

    }
}