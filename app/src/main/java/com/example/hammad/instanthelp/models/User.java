package com.example.hammad.instanthelp.models;

/**
 * Created by shekh chilli on 12/10/2016.
 */

public class User {
    public User() {
    }

    public String uId;
    public String emaiAddress;
    public String password;
    public Boolean volunteer;
    public Boolean bloodDonor;
    public String bloodGroup;
    public Boolean firstAider;

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String profileImagePath;

    public User(String uId, String emaiAddress, String password, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider) {
        this.uId = uId;
        this.emaiAddress = emaiAddress;
        this.password = password;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
    }

    public User(String uId, String emaiAddress, String password, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider, String profileImagePath) {
        this.uId = uId;
        this.emaiAddress = emaiAddress;
        this.password = password;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
        this.profileImagePath = profileImagePath;
    }
}
