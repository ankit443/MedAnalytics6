package com.epics.medanalytics6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity { 

    EditText etRegFullName, etRegEmail, etRegDOB, etRegMobileNumber, etRegPwd, etRegConfirmPwd;
    EditText etLoginPwdLA;
    ProgressBar progressBarReg;
    ImageView imvShowHidePwdLA;
    RadioGroup RGRegGender;
    RadioButton RBRegGenderAfterSelectionBtn;
    ImageView imvShowHidePwdRA;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    //Defining the TAG Variable
    private static final String TAG = "RegisterActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set title
        getSupportActionBar().setTitle("Register");
        Toast.makeText(this, "You can register now", Toast.LENGTH_SHORT).show();

        etRegFullName = findViewById(R.id.editText_register_full_name);
        etRegEmail = findViewById(R.id.editText_register_email);
        etRegDOB = findViewById(R.id.editText_register_dob);
        etRegMobileNumber = findViewById(R.id.editText_register_mobile);
        etRegPwd = findViewById(R.id.editText_register_password);
        etRegConfirmPwd = findViewById(R.id.etConfirmPassword);
        etLoginPwdLA = findViewById(R.id.editText_login_pwd_LA);
        imvShowHidePwdLA = findViewById(R.id.imageView_show_hide_pwdLA);



        progressBarReg = findViewById(R.id.progressBar);

        //Radio Button for gender
        RGRegGender = findViewById(R.id.radio_group_register_gender);
        RGRegGender.clearCheck();

        //Show/Hide password
        imvShowHidePwdRA = findViewById(R.id.imageView_show_hide_pwdRA);
        imvShowHidePwdRA.setImageResource(R.drawable.ic_eye_24);
        imvShowHidePwdRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etRegPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){

                    //If password is visible, hide it
                    etRegPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    //Change Icon
                    imvShowHidePwdRA.setImageResource(R.drawable.ic_hide_pwd);

                }

                else {
                    etLoginPwdLA.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imvShowHidePwdLA.setImageResource(R.drawable.ic_show_pwd);

                }
            }
        });









        Button btnRegActual = findViewById(R.id.btnRegister);


        btnRegActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int selectedGenderID = RGRegGender.getCheckedRadioButtonId();
                RBRegGenderAfterSelectionBtn = findViewById(selectedGenderID);

                //Obtain the entered data
                String textFullName = etRegFullName.getText().toString();
                String textEmail = etRegEmail.getText().toString();
                String textDOB = etRegDOB.getText().toString();
                String textMobileNumber = etRegMobileNumber.getText().toString();
                String textPassword = etRegPwd.getText().toString();
                String textConfirmPassword = etRegConfirmPwd.getText().toString();
                String textGender; // Can't obtain the value before verifying if any button is selected or not

                //Validate Mobile Number
                String mobileRegEx = "[6-9][0-9]{9}"; //First number can be anything from 6 to 9and rest 9 numbers can be anything from 0 to 9
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegEx);
                mobileMatcher = mobilePattern.matcher(textMobileNumber);





                if (TextUtils.isEmpty(textFullName)){

                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    etRegFullName.setError("Full name is required");
                    etRegFullName.requestFocus();

                }

                else if (TextUtils.isEmpty(textEmail)){

                    Toast.makeText(RegisterActivity.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                    etRegEmail.setError("Email is required");
                    etRegEmail.requestFocus();
                }

                else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){


                    Toast.makeText(RegisterActivity.this, "Please re-enter your email address", Toast.LENGTH_SHORT).show();
                    etRegEmail.setError("Valid email is required");
                    etRegEmail.requestFocus();

                }

                else if (TextUtils.isEmpty(textDOB)){

                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    etRegDOB.setError("Valid email is required");
                    etRegDOB.requestFocus();

                    
                }

                else if (RGRegGender.getCheckedRadioButtonId() == -1){
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    RBRegGenderAfterSelectionBtn.setError("Gender is required");
                    RBRegGenderAfterSelectionBtn.requestFocus();

                }


                else if (TextUtils.isEmpty(textMobileNumber)){

                    Toast.makeText(RegisterActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    etRegMobileNumber.setError("Mobile number is required");
                    etRegMobileNumber.requestFocus();

                }

                else if (textMobileNumber.length() != 10){

                    Toast.makeText(RegisterActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    etRegMobileNumber.setError("Mobile number should be 10 digits");
                    etRegMobileNumber.requestFocus();

                }


                else if (!mobileMatcher.find()){

                    Toast.makeText(RegisterActivity.this, "Please re-enter your mobile number", Toast.LENGTH_SHORT).show();
                    etRegMobileNumber.setError("Mobile number is not valid");
                    etRegMobileNumber.requestFocus();


                }

                else if (TextUtils.isEmpty(textPassword)){

                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    etRegPwd.setError("Password is required");
                    etRegPwd.requestFocus();


                }

                else if (textPassword.length() < 6){

                    Toast.makeText(RegisterActivity.this, "Password should be at least 6 digits", Toast.LENGTH_SHORT).show();
                    etRegPwd.setError("Password is too weak");
                    etRegPwd.requestFocus();


                }

                else if (TextUtils.isEmpty(textConfirmPassword)){

                    Toast.makeText(RegisterActivity.this, "Password confirm your password", Toast.LENGTH_SHORT).show();
                    etRegConfirmPwd.setError("Password confirmation is required");
                    etRegConfirmPwd.requestFocus();


                }

                else if (!textPassword.equals(textConfirmPassword)){

                    Toast.makeText(RegisterActivity.this, "Confirmation Password and Password aren't the same", Toast.LENGTH_SHORT).show();
                    etRegConfirmPwd.setError("Password Confirmation is required");



                    //Clear the entered passwords
                    etRegPwd.clearComposingText();
                    etRegConfirmPwd.clearComposingText();
                }

                else {
                    textGender = RBRegGenderAfterSelectionBtn.getText().toString();
                    progressBarReg.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDOB, textGender, textMobileNumber, textPassword);

                }




            }
        });




    }


    // Register using Firebase
    private void registerUser
    (String textFullName, String textEmail, String textDOB, String textGender, String textMobileNumber, String textPassword)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    mUser = mAuth.getCurrentUser();

                    //Update display name of the user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    mUser.updateProfile(profileChangeRequest);


                    //Enter user data to the Firebase realtime database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDOB, textGender, textMobileNumber);



                    //Extracting the User Reference from Database for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(mUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                //Send verification mail
                                mUser.sendEmailVerification();

                                Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email", Toast.LENGTH_SHORT).show();

                                //open User Profile after successful registration
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);

                                //To prevent user from returning back to registration on pressing back after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Closing Register Activity

                            }

                            else {

                                Toast.makeText(RegisterActivity.this, "User registration failed. Please try again", Toast.LENGTH_SHORT).show();
                                progressBarReg.setVisibility(View.GONE);


                            }



                        }
                    });







                }

                else {

                    try {
                        throw task.getException();
                    }

                    catch (FirebaseAuthWeakPasswordException e){

                        etRegPwd.setError("Your password is too weak. Kindly use a mix of alphabets, numbers and special characters");
                        etRegPwd.requestFocus();


                    }

                    catch (FirebaseAuthInvalidCredentialsException e){
                        etRegPwd.setError("Your email is invalid");
                        etRegPwd.requestFocus();

                    }

                    catch (FirebaseAuthUserCollisionException e){
                        etRegPwd.setError("User is already regsitered");
                        etRegPwd.requestFocus();

                    }

                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }


                }

                progressBarReg.setVisibility(View.GONE);
            }
        });



    }
}