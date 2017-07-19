package com.example.hammad.instanthelp.models;

/**
 * Created by shekh chilli on 12/10/2016.
 */

public class User {
    public User() {
    }

    public String uId;
    public String fname;
    public String lname;
    public String emaiAddress;
    public String contact;
    public String country;
    public String city;
    public String password;
    public Boolean gender;
    public Boolean volunteer;
    public Boolean bloodDonor;
    public String bloodGroup;
    public Boolean firstAider;
    public Boolean ambulance;
    public int donatedUnits;

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public String profileImagePath;

    public User(String uId, String fname, String lname, String emaiAddress, String contact, int donatedUnits, String profileImagePath) {
        this.uId = uId;
        this.fname = fname;
        this.lname = lname;
        this.emaiAddress = emaiAddress;
        this.contact = contact;
        this.donatedUnits = donatedUnits;
        this.profileImagePath = profileImagePath;
    }

    public int getDonatedUnits() {
        return donatedUnits;
    }

    public void setDonatedUnits(int donatedUnits) {
        this.donatedUnits = donatedUnits;
    }

    public User(String uId, String fname, String lname, String emaiAddress, String contact, String country, String city, String password, Boolean gender, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider, Boolean ambulance, String profileImagePath) {
        this.uId = uId;
        this.fname = fname;
        this.lname = lname;
        this.emaiAddress = emaiAddress;
        this.contact = contact;
        this.country = country;
        this.city = city;
        this.password = password;
        this.gender = gender;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
        this.ambulance = ambulance;
        this.profileImagePath = profileImagePath;
    }

    public User(String uId, String fname, String lname, String emaiAddress, String country, String city, String password, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider, Boolean ambulance) {
        this.uId = uId;
        this.fname = fname;
        this.lname = lname;
        this.emaiAddress = emaiAddress;
        this.country = country;
        this.city = city;
        this.password = password;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
        this.ambulance = ambulance;
    }

    public User(String uId, String fname, String lname, String emaiAddress, String contact, String country, String city, String password, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider, Boolean ambulance, String profileImagePath) {
        this.uId = uId;
        this.fname = fname;
        this.lname = lname;
        this.emaiAddress = emaiAddress;
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

    public User(String uId, String fname, String lname, String emaiAddress, String contact, String country, String city, String password, Boolean gender, Boolean volunteer, Boolean bloodDonor, String bloodGroup, Boolean firstAider, Boolean ambulance) {
        this.uId = uId;
        this.fname = fname;
        this.lname = lname;
        this.emaiAddress = emaiAddress;
        this.contact = contact;
        this.country = country;
        this.city = city;
        this.password = password;
        this.gender = gender;
        this.volunteer = volunteer;
        this.bloodDonor = bloodDonor;
        this.bloodGroup = bloodGroup;
        this.firstAider = firstAider;
        this.ambulance = ambulance;
    }

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
