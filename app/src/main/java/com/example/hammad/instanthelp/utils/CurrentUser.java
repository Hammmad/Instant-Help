package com.example.hammad.instanthelp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hammad.instanthelp.models.Constants;
import com.example.hammad.instanthelp.models.User;

/**
 * Created by Coder on 03/03/2017.
 */

public class CurrentUser {

    Context mContext;

    //    private String Username, password, bloodGroup;
//    private Boolean volunteer, bloodDonor, firstAider;
//
    public CurrentUser(Context mContext) {
        this.mContext = mContext;
    }

    //Todo: Update currentUser data
    public void setCurrentUser(User user) {
        SharedPreferences currentUser = mContext.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentUser.edit();
        editor.putString(Constants.UID, user.uId);
        editor.putString(Constants.FNAME, user.fname);
        editor.putString(Constants.LNAME, user.lname);
        editor.putString(Constants.EMAIL, user.emaiAddress);
        editor.putString(Constants.CONTACT, user.contact);
        editor.putString(Constants.COUNTRY, user.country);
        editor.putString(Constants.CITY, user.city);
        editor.putString(Constants.PASSWORD, user.password);
        editor.putBoolean(Constants.GENDER, user.gender);
        editor.putString(Constants.BLOODGROUP, user.bloodGroup);
        editor.putString(Constants.IMAGE_PATH, user.profileImagePath);
        editor.putBoolean(Constants.VOLUNTEER, user.volunteer);
        editor.putBoolean(Constants.BLOOD_DONOR, user.bloodDonor);
        editor.putBoolean(Constants.FIRST_AIDER, user.firstAider);
        editor.putBoolean(Constants.AMBULANCE, user.ambulance);
        editor.commit();
    }

    public User getCurrentUser() {


        SharedPreferences currentUser = mContext.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        String uId = currentUser.getString(Constants.UID, null);
        String fname = currentUser.getString(Constants.FNAME, null);
        String lname = currentUser.getString(Constants.LNAME, null);
        String email = currentUser.getString(Constants.EMAIL, null);
        String contact = currentUser.getString(Constants.CONTACT, null);
        String country = currentUser.getString(Constants.COUNTRY, null);
        String city = currentUser.getString(Constants.CITY, null);
        String password = currentUser.getString(Constants.PASSWORD, null);
        Boolean gender = currentUser.getBoolean(Constants.GENDER, true);
        String bloodGroup = currentUser.getString(Constants.BLOODGROUP, null);
        String imagePath = currentUser.getString(Constants.IMAGE_PATH, null);
        Boolean ambulance = currentUser.getBoolean(Constants.AMBULANCE, true);
        Boolean bloodDonor = currentUser.getBoolean(Constants.BLOOD_DONOR, true);
        Boolean firstAider = currentUser.getBoolean(Constants.FIRST_AIDER, true);
        Boolean volunteer = currentUser.getBoolean(Constants.VOLUNTEER, true);


        return new User(uId, fname, lname, email, contact, country, city, password, gender, volunteer, bloodDonor, bloodGroup, firstAider, ambulance, imagePath);


    }

    public void setNoImageCurrentUser(User user) {
        SharedPreferences currentUser = mContext.getSharedPreferences(Constants.NO_IMAGE_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentUser.edit();
        editor.putString(Constants.UID, user.uId);
        editor.putString(Constants.FNAME, user.fname);
        editor.putString(Constants.LNAME, user.lname);
        editor.putString(Constants.EMAIL, user.emaiAddress);
        editor.putString(Constants.CONTACT, user.contact);
        editor.putString(Constants.COUNTRY, user.country);
        editor.putString(Constants.CITY, user.city);
        editor.putString(Constants.PASSWORD, user.password);
        editor.putBoolean(Constants.GENDER, user.gender);
        editor.putString(Constants.BLOODGROUP, user.bloodGroup);
//        editor.putString(Constants.IMAGE_PATH, user.profileImagePath);
        editor.putBoolean(Constants.VOLUNTEER, user.volunteer);
        editor.putBoolean(Constants.AMBULANCE, user.ambulance);
        editor.putBoolean(Constants.BLOOD_DONOR, user.bloodDonor);
        editor.putBoolean(Constants.FIRST_AIDER, user.firstAider);
        editor.commit();
    }

    public User getNoImageCurrentUser() {


        SharedPreferences currentUser = mContext.getSharedPreferences("NO_IMAGE_CURRENT_USER", Context.MODE_PRIVATE);
        String uId = currentUser.getString(Constants.UID, null);
        String fname = currentUser.getString(Constants.FNAME, null);
        String lname = currentUser.getString(Constants.LNAME, null);
        String email = currentUser.getString(Constants.EMAIL, null);
        String contact = currentUser.getString(Constants.CONTACT, null);
        String country = currentUser.getString(Constants.COUNTRY, null);
        String city = currentUser.getString(Constants.CITY, null);
        String password = currentUser.getString(Constants.PASSWORD, null);
        Boolean gender = currentUser.getBoolean(Constants.GENDER, true);
        String bloodGroup = currentUser.getString(Constants.BLOODGROUP, null);
//        String imagePath = currentUser.getString(Constants.IMAGE_PATH, null);
        Boolean ambulance = currentUser.getBoolean(Constants.AMBULANCE, true);
        Boolean bloodDonor = currentUser.getBoolean(Constants.BLOOD_DONOR, true);
        Boolean firstAider = currentUser.getBoolean(Constants.FIRST_AIDER, true);
        Boolean volunteer = currentUser.getBoolean(Constants.VOLUNTEER, true);

        return new User(uId, fname, lname, email, contact, country, city,  password, gender,  volunteer, bloodDonor, bloodGroup, firstAider, ambulance);


    }
}
