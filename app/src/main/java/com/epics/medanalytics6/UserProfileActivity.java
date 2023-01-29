package com.epics.medanalytics6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {

    TextView tvWelcome, tvFullName, tvEmail, tvDOB, tvGender, tvMobileNumber;
    ProgressBar progressBarUSPA;
    String fullName, email, dOB, gender, mobileNumber;
    ImageView imvUSPA;
    FirebaseAuth mAuthProfileUSPA;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setTitle("Home");

        tvWelcome = findViewById(R.id.textView_show_welcomeUSPA);
        tvFullName = findViewById(R.id.textView_show_full_nameUSPA);
        tvEmail = findViewById(R.id.textView_show_emailUSPA);
        tvDOB = findViewById(R.id.textView_show_dobUSPA);
        tvGender = findViewById(R.id.textView_show_genderUSPA);
        tvMobileNumber = findViewById(R.id.textView_show_mobileUSPA);

        progressBarUSPA = findViewById(R.id.progress_barUSPA);

        mAuthProfileUSPA = FirebaseAuth.getInstance();
        FirebaseUser firebaseUserUSPA = mAuthProfileUSPA.getCurrentUser();

        if (firebaseUserUSPA == null){
            Toast.makeText(UserProfileActivity.this, "Something went wrong. User Details are not available at the moment", Toast.LENGTH_SHORT).show();

        }

        else {

            checkIfEmailIsVerified(firebaseUserUSPA);
            progressBarUSPA.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUserUSPA);
            
        }



    }

    //Users coming to UserProfileActivity after successful registration
    private void checkIfEmailIsVerified(FirebaseUser firebaseUserUSPA) {

        if (!firebaseUserUSPA.isEmailVerified()){
            showAlertDialog();
            
        }


    }



    private void showAlertDialog() {

        //Setup the alert builder
        AlertDialog.Builder builderLA = new AlertDialog.Builder(UserProfileActivity.this);
        builderLA.setTitle("Email not verified");
        builderLA.setMessage("Please verify your email. Unable to login without verification from next time");

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

    private void showUserProfile(FirebaseUser firebaseUserUSPA) {

        String userID = firebaseUserUSPA.getUid();

        //Extract the data from the database by using a reference from the registered node: "Registered Users"
        DatabaseReference referenceProfileUSPA = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfileUSPA.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ReadWriteUserDetails readUserDetailsUSPA = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetailsUSPA != null){
                    fullName = firebaseUserUSPA.getDisplayName();
                    email = firebaseUserUSPA.getEmail();
                    dOB = readUserDetailsUSPA.dOB;
                    gender = readUserDetailsUSPA.gender;
                    mobileNumber = readUserDetailsUSPA.mobileNumber;

                    //Setting up welcome text
                    tvWelcome.setText("Welcome, " + fullName + "!");
                    tvFullName.setText(fullName);
                    tvEmail.setText(email);
                    tvDOB.setText(dOB);
                    tvGender.setText(gender);
                    tvMobileNumber.setText(mobileNumber);


                }

                progressBarUSPA.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBarUSPA.setVisibility(View.GONE);

            }
        });



    }

    //Creating onCreate ActionBar Menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate Menu items through here
        getMenuInflater().inflate(R.menu.common_menu_nirog, menu);
        return super.onCreateOptionsMenu(menu);
    }


    //When any meny item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idUSPA = item.getItemId();

        if (idUSPA == R.id.menuRefresh){
            //Refresh the Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }

        else if (idUSPA == R.id.menuUpdateProfile){

            Intent intent = new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}