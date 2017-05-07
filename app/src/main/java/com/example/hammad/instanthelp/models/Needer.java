package com.example.hammad.instanthelp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shekh chilli on 12/13/2016.
 */

public class Needer implements Parcelable {

    public String userName;
    public double latitude;
    public double longitude;
    public String bloodGroup;
    public String uId;
    public Needer() {
    }

    public static final Parcelable.Creator<Needer> CREATOR = new Creator<Needer>() {
        @Override
        public Needer createFromParcel(Parcel parcel) {
            return new Needer(parcel);
        }

        @Override
        public Needer[] newArray(int i) {
            return new Needer[i];
        }
    };
    public Needer(String userName, String bloodGroup, double latitude, double longitude, String uId) {
        this.userName = userName;
        this.bloodGroup = bloodGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uId = uId;

    }

    public Needer(Parcel parcel){

        String[] strings = new String[3];
        double[] doubles  = new double[2];
        parcel.readDoubleArray(doubles);
        parcel.readStringArray(strings);
        this.userName = strings[0];
        this.bloodGroup = strings[1];
        this.uId = strings[2];


        this.latitude = doubles[0];
        this.longitude = doubles[1];
    }


    @Override
    public int describeContents() {
        return this.hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        String[] strings = new String[]{this.userName,this.bloodGroup, this.uId} ;
        double[] doubles = new double[]{this.latitude, this.longitude};

        parcel.writeDoubleArray(doubles);
        parcel.writeStringArray(strings);


    }
}
