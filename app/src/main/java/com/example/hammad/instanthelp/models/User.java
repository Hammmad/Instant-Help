package com.example.hammad.instanthelp.models;

/**
 * Created by shekh chilli on 12/10/2016.
 */

public class User {
    public User() {
    }

    public String uId;
    public String fName;
    public String lName;
    public String emailAddress;
    public String contact;
    public String country;
    public String city;
    public String password;
    public Boolean volunteer;
    public Boolean bloodDonor;
    public String bloodGroup;
    public Boolean firstAider;
    public Boolean ambulance;

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String profileImagePath;


    public User(String uId, String fName, String lName, String emailAddress, String contact,
                String country, String city, String password, Boolean volunteer,
                Boolean bloodDonor, String bloodGroup, Boolean firstAider,
                Boolean ambulance) {
        this.uId = uId;
        this.fName = fName;
        this.lName = lName;
        this.emailAddress = emailAddress;
        this.contact = contact;
        this.country = country;
        this.city = city;
        this.password = password;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
        this.ambulance = ambulance;
    }

    public User(String uId, String fName, String lName,
                String emailAddress, String contact, String country,
                String city, String password, Boolean volunteer,
                Boolean bloodDonor, String bloodGroup, Boolean firstAider,
                Boolean ambulance, String profileImagePath) {
        this.uId = uId;
        this.fName = fName;
        this.lName = lName;
        this.emailAddress = emailAddress;
        this.contact = contact;
        this.country = country;
        this.city = city;
        this.password = password;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
        this.ambulance = ambulance;
        this.profileImagePath = profileImagePath;
    }
//
//    public User(String uId, String emailAddress, String password, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider) {
//        this.uId = uId;
//        this.emailAddress = emailAddress;
//        this.password = password;
//        this.volunteer = volunteer;
//        this.bloodDonor = bloodDonor;
//        this.bloodGroup = bloodGroup;
//        this.firstAider = firstAider;
//    }
//
//
//    public User(String uId, String emailAddress, String password, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider, String profileImagePath) {
//        this.uId = uId;
//        this.emailAddress = emailAddress;
//        this.password = password;
//        this.volunteer = volunteer;
//        this.bloodDonor = bloodDonor;
//        this.bloodGroup = bloodGroup;
//        this.firstAider = firstAider;
//        this.profileImagePath = profileImagePath;
//    }
}
