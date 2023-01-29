package com.epics.medanalytics6;

public class ReadWriteUserDetails {

    public String dOB, gender, mobileNumber;

    //Constructor
    public ReadWriteUserDetails(){};


    public ReadWriteUserDetails(String textDOB, String textGender, String textMobileNumber){

        this.dOB = textDOB;
        this.gender = textGender;
        this.mobileNumber = textMobileNumber;


    }
}
