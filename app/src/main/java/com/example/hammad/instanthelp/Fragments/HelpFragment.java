package com.example.hammad.instanthelp.Fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.LocationTracker;
import com.example.hammad.instanthelp.Needer;
import com.example.hammad.instanthelp.FirebaseBackgroundService;
import com.example.hammad.instanthelp.HelpMapActivity;
import com.example.hammad.instanthelp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class HelpFragment extends Fragment implements View.OnClickListener {


    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private static final String TAG = "HelpFragment/DEBUGGING";
    double currentLongitude;
    double currentLatitude;


    GoogleApiClient mGoogleApiClient;
    private String requiredBloodGroup = null;


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


        LocationTracker locationTracker = new LocationTracker(getActivity());
        if(locationTracker.canGetLocation()){
            locationTracker.getLocation();
            currentLatitude = locationTracker.getLatitude();
            currentLongitude = locationTracker.getLongitude();
            Toast.makeText(getActivity(), "currentLatitude,currentLongitude:   "+ currentLatitude + currentLongitude, Toast.LENGTH_SHORT).show();
            Intent serviceIntent = new Intent(getActivity(), FirebaseBackgroundService.class);
            getActivity().startService(serviceIntent);
        }else{
            Log.e(TAG, "No provider");
        }

//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot curentLocationSnapshot: dataSnapshot.child("userCurrentLocation").getChildren()){
//                    Needer currentLocation = curentLocationSnapshot.getValue(Needer.class);
//                    if(!currentLocation.volunteerUId.equals(mAuth.getCurrentUser().getUid())) {
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
//        buildGoogleApiClient();


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
//        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(authStateListener);
//            mGoogleApiClient.disconnect();

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
            }
            case R.id.first_aid_button:{

                requiredBloodGroup = null;
                sendMylocationUp();
                break;
            }
            default:{
                break;
            }
        }
    }



    private void sendMylocationUp() {
        if (currentLatitude != 0){
            if(mAuth.getCurrentUser() != null) {
                String Uid = mAuth.getCurrentUser().getUid();
                String userName = mAuth.getCurrentUser().getEmail();
                userName = userName.replace("@instanthelp.com", "");


                final Needer needer = new Needer
                        (userName, requiredBloodGroup,currentLatitude,currentLongitude, mAuth.getCurrentUser().getUid());
                databaseReference.child("userCurrentLocation").child(Uid).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        databaseReference.setValue(needer);
                    }
                });
            }
        }
    }

    private void showBloodGrouplist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String[] bloodGroups = {"A +ve", "A -ve","B +ve","AB +ve","O +ve","O -ve"};

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

}
