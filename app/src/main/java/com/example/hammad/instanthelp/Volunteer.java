package com.example.hammad.instanthelp;

/**
 * Created by Hammad on 12/22/2016.
 */

public class Volunteer {
    public Volunteer() {
    }

    public double latitude;
    public double longitude;
    public String volunteerUId;
    public String neederUId;
    public String message;
    public String volunteerName;

    public Volunteer(double latitude, double longitude, String message, String volunteerUId, String neederUId, String volunteerName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
        this.volunteerUId = volunteerUId;
        this.neederUId = neederUId;
        this.volunteerName = volunteerName;
    }
}
