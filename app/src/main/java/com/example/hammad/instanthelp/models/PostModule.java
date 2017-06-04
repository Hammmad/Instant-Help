package com.example.hammad.instanthelp.models;

import java.io.Serializable;

/**
 * Created by Qasim Nawaz on 5/15/2017.
 */

public class PostModule implements Serializable {
    private String uuid;
    private String mName;
    private String mGroup;
    private int mNoofUnits;
    private String mCountry;
    private String mCity;
    private String mHospital;
    private String mContact;
    private int donatedUnits;
    private int currentRequirement;
    private int withinDuration;
    private String pushkey;

    public PostModule() {
    }

    public PostModule(String uuid, String mName, String mGroup, int mNoofUnits, String mCountry, String mCity, String mHospital, String mContact, int donatedUnits, int currentRequirement, int withinDuration, String pushkey) {
        this.uuid = uuid;
        this.mName = mName;
        this.mGroup = mGroup;
        this.mNoofUnits = mNoofUnits;
        this.mCountry = mCountry;
        this.mCity = mCity;
        this.mHospital = mHospital;
        this.mContact = mContact;
        this.donatedUnits = donatedUnits;
        this.currentRequirement = currentRequirement;
        this.withinDuration = withinDuration;
        this.pushkey = pushkey;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmGroup() {
        return mGroup;
    }

    public void setmGroup(String mGroup) {
        this.mGroup = mGroup;
    }

    public int getmNoofUnits() {
        return mNoofUnits;
    }

    public void setmNoofUnits(int mNoofUnits) {
        this.mNoofUnits = mNoofUnits;
    }

    public String getmCountry() {
        return mCountry;
    }

    public void setmCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getmCity() {
        return mCity;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public String getmHospital() {
        return mHospital;
    }

    public void setmHospital(String mHospital) {
        this.mHospital = mHospital;
    }

    public String getmContact() {
        return mContact;
    }

    public void setmContact(String mContact) {
        this.mContact = mContact;
    }

    public int getDonatedUnits() {
        return donatedUnits;
    }

    public void setDonatedUnits(int donatedUnits) {
        this.donatedUnits = donatedUnits;
    }

    public int getCurrentRequirement() {
        return currentRequirement;
    }

    public void setCurrentRequirement(int currentRequirement) {
        this.currentRequirement = currentRequirement;
    }

    public int getWithinDuration() {
        return withinDuration;
    }

    public void setWithinDuration(int withinDuration) {
        this.withinDuration = withinDuration;
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }
}
