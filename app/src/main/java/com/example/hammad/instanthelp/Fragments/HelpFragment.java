package com.example.hammad.instanthelp.Fragments;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.CurrentLocation;
import com.example.hammad.instanthelp.FirebaseBackgroundService;
import com.example.hammad.instanthelp.HelpMapActivity;
import com.example.hammad.instanthelp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private static final String TAG = "DEBUGGING";
    Location lastLocation;
    GoogleApiClient mGoogleApiClient;
    private String requiredBloodGroup;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "onConnected working");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e(TAG, "Permission not Grant");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        String longitude = null;
        String latitude = null;


        if(lastLocation != null){
            latitude = String.valueOf(lastLocation.getLatitude());
            longitude = String.valueOf(lastLocation.getLongitude());
        }

        Toast.makeText(getActivity(), "latitude,longitude:   "+ latitude + longitude, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionSuspended(int i) {
            Log.e(TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failed" + connectionResult.getErrorMessage());
    }


    public interface CallbackHelpFragment {

    }

    public HelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_help, container, false);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.e(TAG, "user is signedIn" + user.getUid());
                } else {
                    Log.e(TAG, "user is signedOut");
                }
            }
        };

        TextView userNameTextView = (TextView) rootView.findViewById(R.id.userName_textView);
        userNameTextView.setText(mAuth.getCurrentUser().getEmail().replace("@instanthelp.com", ""));
        rootView.findViewById(R.id.map_help_button).setOnClickListener(this);
        rootView.findViewById(R.id.blood_require_button).setOnClickListener(this);
        rootView.findViewById(R.id.first_aid_button).setOnClickListener(this);
        rootView.findViewById(R.id.signout_textView).setOnClickListener(this);
        rootView.findViewById(R.id.register_volunteer_textview).setOnClickListener(this);

//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot curentLocationSnapshot: dataSnapshot.child("userCurrentLocation").getChildren()){
//                    CurrentLocation currentLocation = curentLocationSnapshot.getValue(CurrentLocation.class);
//                    if(!currentLocation.uId.equals(mAuth.getCurrentUser().getUid())) {
//                        Log.e(TAG, currentLocation.userName);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//        databaseReference.addValueEventListener(valueEventListener);
        buildGoogleApiClient();
        getActivity().startService(new Intent(getActivity(), FirebaseBackgroundService.class));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
            mGoogleApiClient.disconnect();

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.map_help_button: {
                Intent intent = new Intent(getActivity(), HelpMapActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.signout_textView: {

                mAuth.signOut();
                Log.e(TAG, "Signout clicked");

                break;
            }
            case R.id.blood_require_button: {
                showBloodGrouplist();
                break;
            }default:{
                break;
            }
        }
    }



    private void sendMylocationUp() {
        if (lastLocation != null){
            if(mAuth.getCurrentUser() != null) {
                String Uid = mAuth.getCurrentUser().getUid();
                String userName = mAuth.getCurrentUser().getEmail();
                userName = userName.replace("@instanthelp.com", "");

                CurrentLocation currentLocation = new CurrentLocation(userName, requiredBloodGroup, lastLocation.getLatitude()
                        ,lastLocation.getLongitude(), mAuth.getCurrentUser().getUid());
                databaseReference.child("userCurrentLocation").child(Uid).setValue(currentLocation);
            }
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void showBloodGrouplist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] bloodGroups = {"A+ve,", "A-ve","B+ve","AB+ve","O+ve","O-ve"};

        builder.setTitle("Pick Blood Group").setItems(bloodGroups, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                requiredBloodGroup = bloodGroups[i];
                Log.e(TAG, requiredBloodGroup);
                sendMylocationUp();

            }
        });
        builder.create().show();
    }

    public class MyLocation extends AsyncTask<Void, Void, Location> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        GoogleApiClient googleApiClient;
        Location lastLocation;



        @Override
        protected Location doInBackground(Void... voids) {

            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API).build();


            return lastLocation;
        }

        @Override
        protected void onPostExecute(Location location) {
            String longitude = null;
            String latitude = null;


            if(lastLocation != null){
                latitude = String.valueOf(lastLocation.getLatitude());
                longitude = String.valueOf(lastLocation.getLongitude());
            }

            Toast.makeText(getActivity(), "latitude,longitude:   "+ latitude + longitude, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.e(TAG, "onConnected working");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.e(TAG, "onConnectionSuspended working");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.e(TAG, "onConnectionFailed working");
        }
    }
}
