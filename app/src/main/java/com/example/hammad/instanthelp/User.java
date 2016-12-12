package com.example.hammad.instanthelp;

/**
 * Created by shekh chilli on 12/10/2016.
 */

public class User {

    public String emaiAddress;
    public String password;
    public Boolean volunteer;
    public Boolean bloodDonor;
    public String bloodGroup;
    public Boolean firstAider;

    public User(String emaiAddress, String password, Boolean volunteer, Boolean bloodDonor,String bloodGroup, Boolean firstAider) {
        this.emaiAddress = emaiAddress;
        this.password = password;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
    }
}
