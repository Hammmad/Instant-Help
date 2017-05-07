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

    public void setCurrentUser(User user){
        SharedPreferences currentUser = mContext.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentUser.edit();
        editor.putString(Constants.UID, user.uId);
        editor.putString(Constants.USERNAME, user.emaiAddress);
        editor.putString(Constants.PASSWORD, user.password);
        editor.putString(Constants.BLOODGROUP, user.bloodGroup);
        editor.putString(Constants.IMAGE_PATH, user.profileImagePath);
        editor.putBoolean(Constants.VOLUNTEER, user.volunteer);
        editor.putBoolean(Constants.BLOOD_DONOR, user.bloodDonor);
        editor.putBoolean(Constants.FIRST_AIDER, user.firstAider);
        editor.commit();
    }

    public User getCurrentUser(){


        SharedPreferences currentUser = mContext.getSharedPreferences("CURRENT_USER", Context.MODE_PRIVATE);
        String uId = currentUser.getString(Constants.UID, null);
        String userName = currentUser.getString(Constants.USERNAME, null);
        String password = currentUser.getString(Constants.PASSWORD, null);
        String bloodGroup = currentUser.getString(Constants.BLOODGROUP, null);
        String imagePath = currentUser.getString(Constants.IMAGE_PATH, null);
        Boolean bloodDonor = currentUser.getBoolean(Constants.BLOOD_DONOR, true);
        Boolean firstAider = currentUser.getBoolean(Constants.FIRST_AIDER, true);
        Boolean volunteer = currentUser.getBoolean(Constants.VOLUNTEER, true);



        return new User(uId, userName, password, volunteer, bloodDonor, bloodGroup, firstAider, imagePath );



    }

    public void setNoImageCurrentUser(User user){
        SharedPreferences currentUser = mContext.getSharedPreferences(Constants.NO_IMAGE_CURRENT_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = currentUser.edit();
        editor.putString(Constants.UID, user.uId);
        editor.putString(Constants.USERNAME, user.emaiAddress);
        editor.putString(Constants.PASSWORD, user.password);
        editor.putString(Constants.BLOODGROUP, user.bloodGroup);
//        editor.putString(Constants.IMAGE_PATH, user.profileImagePath);
        editor.putBoolean(Constants.VOLUNTEER, user.volunteer);
        editor.putBoolean(Constants.BLOOD_DONOR, user.bloodDonor);
        editor.putBoolean(Constants.FIRST_AIDER, user.firstAider);
        editor.commit();
    }

    public User getNoImageCurrentUser(){


        SharedPreferences currentUser = mContext.getSharedPreferences("NO_IMAGE_CURRENT_USER", Context.MODE_PRIVATE);
        String uId = currentUser.getString(Constants.UID, null);
        String userName = currentUser.getString(Constants.USERNAME, null);
        String password = currentUser.getString(Constants.PASSWORD, null);
        String bloodGroup = currentUser.getString(Constants.BLOODGROUP, null);
//        String imagePath = currentUser.getString(Constants.IMAGE_PATH, null);
        Boolean bloodDonor = currentUser.getBoolean(Constants.BLOOD_DONOR, true);
        Boolean firstAider = currentUser.getBoolean(Constants.FIRST_AIDER, true);
        Boolean volunteer = currentUser.getBoolean(Constants.VOLUNTEER, true);

        return new User(uId, userName, password, volunteer, bloodDonor, bloodGroup, firstAider );



    }
}
