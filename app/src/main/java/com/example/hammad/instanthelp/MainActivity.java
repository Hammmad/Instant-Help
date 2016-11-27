package com.example.hammad.instanthelp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "DEBUGGING";
    GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    Location lastLocation;
    LatLng myLocation;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    AddressResultReceiver mAbbessResultReceiver;
    String addressOutput;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildeGoogleApiClient();


        Log.e(TAG, String.valueOf(myLocation));


    }

    private void buildeGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        map = googleMap;
        Log.e(TAG, String.valueOf(myLocation));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(myLocation);

        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions().position(myLocation)).setTitle("my location");
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setIndoorEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if(lastLocation != null) {
                    startIntentService(lastLocation);
                }
            }
        });
    }

    private void startIntentService(Location requiredLocation) {
        Intent intent  = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mAbbessResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, requiredLocation);
        startService(intent);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, " onConnectionFailed works");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected works");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                myLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            }
            Log.e(TAG, String.valueOf(myLocation));
            MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            mMapFragment.getMapAsync(this);

            createLocationRequest();

            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
            }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder Builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, Builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:{
                        Log.e(TAG, "SUCCESS");
                        break;
                    }
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:{
                        Log.e(TAG, "RESOLUTION_REQUIRED");
                    break;
                    }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:{
                        Log.e(TAG, "CHANGE_UNAVAILABLE");
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.e(TAG, "onConnectionSuspended is working");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        LatLng currentlocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(currentlocation).title("New change loc"));

    }

    class AddressResultReceiver extends ResultReceiver {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Log.e(TAG, addressOutput);

            if(resultCode == Constants.RESULT_SUCCESS){
                Toast.makeText(MainActivity.this, "Address Found", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(MainActivity.this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
    }
}
