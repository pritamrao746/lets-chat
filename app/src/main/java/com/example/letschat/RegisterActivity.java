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

public class RegisterActivity extends AppCompatActivity {



        private EditText name ,pass,email;
        private  Button register;
        private FirebaseAuth mAuth;

        private Toolbar mToolbar;  //for top toolbar

        //Progress Dialog
        private ProgressDialog mRegProgress;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar Set
        mToolbar =(Toolbar) findViewById(R.id.already_have_account_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRegProgress = new ProgressDialog(this);



         //Authentication Firebase
         mAuth = FirebaseAuth.getInstance();



         name = (EditText) findViewById(R.id.nameReg);
         pass = (EditText) findViewById(R.id.passLogin);
         email = (EditText) findViewById(R.id.emailLogin);
         register= (Button) findViewById(R.id.buttonLoginPage);


         register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {


                 String stringName = name.getText().toString();
                 String stringPass = pass.getText().toString();
                 String stringEmail = email.getText().toString();


                 if(!TextUtils.isEmpty(stringName) && !TextUtils.isEmpty(stringPass) && !TextUtils.isEmpty(stringEmail)){

                     //Progress Bar
                     mRegProgress.setTitle("Registering User");
                     mRegProgress.setMessage("Please wait while we create your account");
                     mRegProgress.setCanceledOnTouchOutside(false);
                     mRegProgress.show();

                     registerUser(stringName,stringEmail,stringPass);
                 }else {

                     Toast.makeText(RegisterActivity.this,"Please Enter Valid Input and Credentials !!",Toast.LENGTH_LONG).show();
                 }

             }
         });

    }

    private void registerUser(String name, String email, String password ){

    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {

        if(task.isSuccessful()){

            mRegProgress.dismiss(); //switching progress bar off

            Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);



            //This single line will prevent going back to start page from the main page
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //Workout About this feature and understand the code

            startActivity(mainIntent);
            finish();

        }
        else{

            mRegProgress.hide();

            Toast.makeText(RegisterActivity.this, "Cannot sign in please check the input fields and try again", Toast.LENGTH_LONG).show();
        }


        }
    });

    }
}
