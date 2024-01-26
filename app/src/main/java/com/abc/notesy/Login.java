package com.abc.notesy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private EditText mloginemail, mloginpassword;
    private Button mloginbutton, mgooglesigninbutton;
    private TextView mgotoregister, mforgotpassword;
    private Dialog loadingDialog;


    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 9001;

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


        setContentView(R.layout.activity_login);

        mloginemail = findViewById(R.id.emailInput);
        mloginpassword = findViewById(R.id.passwordInput);
        mloginbutton = findViewById(R.id.loginButton);
        mgotoregister = findViewById(R.id.registerPrompt);
        mforgotpassword = findViewById(R.id.forgotPassword);
        mgooglesigninbutton = findViewById(R.id.googleSignInButton);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mgooglesigninbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        if (firebaseUser != null) {
            finish();
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        }

        mgotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        mforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        mloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mloginemail.getText().toString().trim();
                String password = mloginpassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                } else {
                    showLoadingDialog();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dismissLoadingDialog();
                            if (task.isSuccessful()) {
                                checkEmailVerification();
                            } else {
                                Toast.makeText(getApplicationContext(), "Account doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Toast.makeText(Login.this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(Login.this, "Firebase Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // User is signed out
            Toast.makeText(Login.this, "Please sign in to continue.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.isEmailVerified()) {
            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Verify your email", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
    private void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
