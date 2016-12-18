package com.example.hammad.instanthelp;

/**
 * Created by shekh chilli on 12/13/2016.
 */

public class CurrentLocation {

    public String userName;
    public double latitude;
    public double longitude;
    public String bloodGroup;
    public String uId;

    public CurrentLocation() {
    }

    public CurrentLocation(String userName, String bloodGroup, double latitude, double longitude, String uId) {
        this.userName = userName;
        this.bloodGroup = bloodGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uId = uId;
    }
}
