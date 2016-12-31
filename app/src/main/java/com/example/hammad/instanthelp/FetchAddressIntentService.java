package com.example.hammad.instanthelp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by shekh chilli on 11/26/2016.
 */

public class FetchAddressIntentService extends IntentService {

    String userName;
    String bloodGruoup;
    private  static  final String TAG = "DEBUGGING/FetchAdressIS";
    ResultReceiver mResultReciever;

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage;

        mResultReciever = intent.getParcelableExtra(Constants.RECEIVER);

        if(mResultReciever == null){
            errorMessage = "ResultReciever is null";
            Log.e(TAG, errorMessage);
            return;
        }

        Location mLocation = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        userName = intent.getStringExtra(Constants.USERNAME);
        bloodGruoup = intent.getStringExtra(Constants.BLOODGROUP);

        if(mLocation == null){
            errorMessage = "No Location Data provided";
            deliverResultToReceiver(Constants.RESULT_FAILURE, errorMessage);
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
        } catch (IOException e) {
            errorMessage = "No Service Available ";
            Log.e(TAG, errorMessage + e.getLocalizedMessage());
        }
        // Handle case when no address found

        if(addresses == null || addresses.size() == 0){
            errorMessage = "No adresses found";
            Log.e(TAG, errorMessage);
            deliverResultToReceiver(Constants.RESULT_FAILURE, errorMessage);
        }else{
            Address address = addresses.get(0);

            ArrayList<String> addressFragments = new ArrayList<>();

            for(int i = 0; i<address.getMaxAddressLineIndex(); i++){
                addressFragments.add(address.getAddressLine(i));
            }

            deliverResultToReceiver(Constants.RESULT_SUCCESS,
                    TextUtils.join(System.getProperty("line.separator"),addressFragments));
        }

    }

    public void deliverResultToReceiver(int resultCode, String message){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        bundle.putString(Constants.USERNAME, userName );
        bundle.putString(Constants.BLOODGROUP, bloodGruoup );
        mResultReciever.send(resultCode, bundle);
    }
}
