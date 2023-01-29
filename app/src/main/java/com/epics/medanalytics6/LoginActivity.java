package com.epics.medanalytics6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etLoginEmailLA, etLoginPwdLA;
    ProgressBar progressBarLA;
    FirebaseAuth mAuthProfileLA;
    ImageView imvShowHidePwdLA;

    private static final String TAG = "LoginActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("niRog App Login");

        etLoginEmailLA = findViewById(R.id.editText_login_email_LA);
        etLoginPwdLA = findViewById(R.id.editText_login_pwd_LA);

        progressBarLA = findViewById(R.id.progressBarLA);

        mAuthProfileLA = FirebaseAuth.getInstance();

        //Show/Hide password
        imvShowHidePwdLA = findViewById(R.id.imageView_show_hide_pwdLA);
        imvShowHidePwdLA.setImageResource(R.drawable.ic_eye_24);
        imvShowHidePwdLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etLoginPwdLA.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){

                    //If password is visible, hide it
                    etLoginPwdLA.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    //Change Icon
                    imvShowHidePwdLA.setImageResource(R.drawable.ic_hide_pwd);

                }

                else {
                    etLoginPwdLA.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imvShowHidePwdLA.setImageResource(R.drawable.ic_show_pwd);

                }
            }
        });


        //Login User
        Button btnLoginLA = findViewById(R.id.button_login_LA);

        btnLoginLA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textEmail = etLoginEmailLA.getText().toString();
                String textPassword = etLoginPwdLA.getText().toString();


                if (TextUtils.isEmpty(textEmail)){

                    Toast.makeText(LoginActivity.this, "Please enter your email ", Toast.LENGTH_SHORT).show();
                    etLoginEmailLA.setError("Email is required");


                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    etLoginEmailLA.setError("Valid email is required");
                    etLoginEmailLA.requestFocus();

                }

                else if (TextUtils.isEmpty(textPassword))
                {
                    Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    etLoginPwdLA.setError("Valid email is required");
                    etLoginPwdLA.requestFocus();

                }

                else {
                    progressBarLA.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPassword);
                }
            }
        });



    }

    private void loginUser(String email, String pwd) {

        mAuthProfileLA.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "You are logged in now.", Toast.LENGTH_SHORT).show();

                    //get instance of the current user
                    FirebaseUser firebaseUser = mAuthProfileLA.getCurrentUser();

                    //Check if email is verified or not
                    if (firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "You are logged in now.", Toast.LENGTH_SHORT).show();

                        //open user profile
                        //Start the UserProAct
                        startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                        finish(); //Close the login activity



                    }

                    else {
                        //Open User Profile
                        firebaseUser.sendEmailVerification();
                        mAuthProfileLA.signOut(); //Sign out the User
                        showAlertDialog();
                        

                    }


                }
                else {

                    try{
                        throw task.getException();
                    }

                    catch (FirebaseAuthInvalidUserException e){
                        etLoginEmailLA.setError("Invalid user. Please try again");
                        etLoginEmailLA.requestFocus();

                    }

                    catch (FirebaseAuthInvalidCredentialsException e){

                        etLoginEmailLA.setError("Invalid credentials. Please try again");
                        etLoginEmailLA.requestFocus();

                    }

                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }

                progressBarLA.setVisibility(View.GONE);


            }
        });
    }

    private void showAlertDialog() {

        //Setup the alert builder
        AlertDialog.Builder builderLA = new AlertDialog.Builder(LoginActivity.this);
        builderLA.setTitle("Email not verified");
        builderLA.setMessage("Please verify your email. Unable to login without verification");

        //Open Email App if User Clicks/Taps continues
        builderLA.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //To email app in a new window and not within our app
                startActivity(intent);


            }
        });

        //Create Alert Dialog Box
        AlertDialog alertDialogLA = builderLA.create();

        //Show the Alert Dialog
        alertDialogLA.show();
    }


    //Check if User is logged in or not, if yes, take the user to the user's profile
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuthProfileLA.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "You are already logged in.", Toast.LENGTH_SHORT).show();

//            Start the UserProfileActivity
//commenting out to test
            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish(); //Close the login activity

        }

        else{
            Toast.makeText(this, "You may log in now.", Toast.LENGTH_SHORT).show();
        }
    }
}