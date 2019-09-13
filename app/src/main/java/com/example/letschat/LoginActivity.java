package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mLoginEmail,mLoginPassword;
    private Button mLoginButton;
    private ProgressDialog mLoginProgress; //For Progress Bar

    private FirebaseAuth mAuth; //For Authentication Purpose




    private Toolbar mToolbar;  //for top toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();  // Firebase Authentication

        mToolbar =(Toolbar) findViewById(R.id.already_have_account_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //Find out why we use this

        //Progress Bar
        mLoginProgress = new ProgressDialog(this);


        mLoginEmail = (EditText) findViewById(R.id.emailLogin);
        mLoginPassword= (EditText) findViewById(R.id.passLogin);
        mLoginButton =(Button) findViewById(R.id.buttonLoginPage);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email,password;

                email = mLoginEmail.getText().toString();
                password = mLoginPassword.getText().toString();

                if( !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we validate your account !!");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email,password);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please Enter Valid Input !!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss(); // switching off login progress

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);


                    //This single line will prevent going back to start page from the main page
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    //Workout About this feature and understand the code

                    startActivity(mainIntent);
                    finish();
                }
                else{

                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Cannot sign in please check the email or password !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
