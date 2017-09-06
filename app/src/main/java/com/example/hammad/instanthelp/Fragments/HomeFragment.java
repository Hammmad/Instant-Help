package com.example.hammad.instanthelp.Fragments;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.activity.FirstAidGuidActivity;
import com.example.hammad.instanthelp.activity.HomeActivity;
import com.example.hammad.instanthelp.models.Constants;
import com.example.hammad.instanthelp.models.User;
import com.example.hammad.instanthelp.sevices.FirebaseBackgroundService;
import com.example.hammad.instanthelp.sevices.GeofenceTransitionsIntentService;
import com.example.hammad.instanthelp.utils.CurrentUser;
import com.example.hammad.instanthelp.utils.LocationTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {


    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private static final String TAG = "HomeFragment/DEBUGGING";

    ArrayList<Geofence> geofenceList;
    LocationTracker locationTracker;
    GoogleApiClient mGoogleApiClient;
    PendingIntent geofencePendingIntent;
    CallbackHomeFragment callbackHomeFragment;


    public HomeFragment() {
        // Required empty public constructor
    }

    public interface CallbackHomeFragment{
		void sendSMSToGuardian();
		void sendNotificationInfoFirebase();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		callbackHomeFragment = (CallbackHomeFragment) context;
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

        rootView.findViewById(R.id.map_help_button).setOnClickListener(this);
        rootView.findViewById(R.id.blood_require_button).setOnClickListener(this);
        rootView.findViewById(R.id.first_aid_button).setOnClickListener(this);




        geofenceList = new ArrayList<>();
//        populateGeofenceList();
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
        buildGoogleApiClient();


        return rootView;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
//                Intent intent = new Intent(getActivity(), HelpMapActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(getContext(), FirstAidGuidActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.blood_require_button: {
//                showBloodGrouplist();
                PostFragment postFragment = new PostFragment();
                this.getFragmentManager().beginTransaction().replace(R.id.content_home, postFragment, null).addToBackStack(null).commit();
                break;
            }
            case R.id.first_aid_button: {
				callbackHomeFragment.sendSMSToGuardian();
				callbackHomeFragment.sendNotificationInfoFirebase();
                break;
            }
            default: {
                break;
            }
        }
    }


	private void addGeofence() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), "Not Connected Api Client", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent()).setResultCallback(this);
    }

    private void populateGeofenceList() {
        locationTracker.getLocation();
        geofenceList.add(new Geofence.Builder().setRequestId("myFence")
                .setCircularRegion(locationTracker.getLatitude(), locationTracker.getLongitude(), Constants.RADIUS_IN_METERS)
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_TIME_IN_MILLIS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }




//    private void userInfoListener(DatabaseReference databaseReference) {
//        databaseReference.child("userinfo").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e(TAG, "userInfo Listener:   " + dataSnapshot.getKey());
//                User currentUser = dataSnapshot.getValue(User.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(@NonNull Status status) {

        if (status.isSuccess()) {
            Toast.makeText(getActivity(), "onResult Success", Toast.LENGTH_SHORT).show();
        } else {

        }
    }
}
