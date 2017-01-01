package com.example.hammad.instanthelp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

public class HelpMapActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "MapActivity/DEBUGGING";
    private static final float VOLUNTEER_ALPHA = 0.5f;
    private static final float NEEDER_ALPHA = 0.7f;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    Location myLocation;
    LatLng myLatLng;
    LocationRequest mLocationRequest;
    Location mCurrentLocation;
    LinearLayout messagebox;
    EditText messageEditText;
    ArrayList<Needer> neederList;
    ArrayList<Volunteer> volunteerList;
    LocationTracker locationTracker;
    int i = 0;
    int j = 0;
    Marker conversationMarker;

    IconGenerator iconFactory;
    ArrayList<Marker> neederMarkers;
    ArrayList<Marker> volunteerMarkers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_map);

//        Intent intent = getIntent();
//        Log.e(TAG, String.valueOf(intent.hasExtra(Constants.NEEDER)));
//        if(intent.hasExtra(Constants.NEEDER)) {
//            Bundle extra = intent.getBundleExtra(Constants.NEEDER);
//            neederList = extra.getParcelableArrayList(Constants.NEEDER);
//            Log.e(TAG, String.valueOf(neederList.get(0).latitude));
//        }
//
//        LocationTracker locationTracker = new LocationTracker(this);
//        if(locationTracker.canGetLocation()){
//            myLocation = locationTracker.getLocation();
//            myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//        }else{
//            Log.e(TAG, "No provider");
//        }
        locationTracker = new LocationTracker(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        messagebox = (LinearLayout) findViewById(R.id.messagebox);
        messageEditText = (EditText) findViewById(R.id.message_editText);

        iconFactory = new IconGenerator(this);
        neederMarkers = new ArrayList<>();
        volunteerMarkers = new ArrayList<>();

        neederList = new ArrayList<>();
        volunteerList = new ArrayList<>();

        messagebox.setVisibility(View.GONE);
        buildGoogleApiClient();
        Log.e(TAG, String.valueOf(myLatLng));

//        MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//
//        mMapFragment.getMapAsync(this);

    }

    private void onSendButtonClickListener(){
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Uid = mAuth.getCurrentUser().getUid();
                String userName = mAuth.getCurrentUser().getEmail();
                userName = userName.replace("@instanthelp.com", "");


                final Needer needer = new Needer
                        (userName, messageEditText.getText().toString(),myLatLng.latitude,myLatLng.longitude, mAuth.getCurrentUser().getUid());
                databaseReference.child("userCurrentLocation").child(Uid).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        databaseReference.setValue(needer);
                    }
                });
                messageEditText.setText("");

            }
        });
    }
    private void onSendButtonClickListener(final int markerIndex) {
        Button sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Volunteer volunteer = new Volunteer(myLatLng.latitude, myLatLng.longitude,
                        messageEditText.getText().toString(), mAuth.getCurrentUser().getUid()
                        ,neederList.get(markerIndex).uId, mAuth.getCurrentUser().getEmail().replace("@instanthelp.com",""));

                databaseReference.child("Volunteer").child(mAuth.getCurrentUser().getUid()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        databaseReference.setValue(volunteer);
                    }
                });

                messageEditText.setText("");
            }
        });
    }

    private void buildGoogleApiClient() {
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
        FirebaseBackgroundService.isActivityStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        FirebaseBackgroundService.isActivityStarted = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseBackgroundService.isActivityStarted = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        String FirstAid;
        map = googleMap;
        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(myLatLng);




        databaseReference.child("userCurrentLocation").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded ");
                    createNeederMarkers(dataSnapshot);

            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeNeederMarker(dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });


        databaseReference.child("Volunteer").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                createVolunteerMarkers(dataSnapshot);
                }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//                for(int i = 0; i<volunteerList.size(); i++) {
//                    if (volunteerList.get(i).neederUId.equals(mAuth.getCurrentUser().getUid())) {
//
//
//                        break;
//                    }
//                }
//                Volunteer volunteer = (dataSnapshot.getValue(Volunteer.class));
//                Log.e(TAG, String.valueOf(volunteerList.indexOf(dataSnapshot.getValue(Volunteer.class))));
//
//                LatLng volunteerLatLng = new LatLng(volunteer.latitude, volunteer.longitude);
//                iconFactory.setStyle(IconGenerator.STYLE_GREEN);
//                iconFactory.setRotation(0);
//                volunteerMarkers.add(addIcon(iconFactory, volunteer.volunteerName,
//                        volunteerLatLng, volunteer.message,volunteer.volunteerName,VOLUNTEER_ALPHA));
//                volunteerList.add(volunteer);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.e(TAG, "onChiledRemoved:    "+ dataSnapshot.getKey());
//                Log.e(TAG, "index of removing item in volunteer list"+String.valueOf(volunteerList.indexOf(dataSnapshot.getValue(Volunteer.class))));
//                Log.e(TAG, "volunteerList.size" + volunteerList.size());
                removeVolunteerMarker(dataSnapshot);


//                Log.e(TAG, "volunteerList.size" + volunteerList.size());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        createNeederMarkers(iconFactory, neederMarkers);


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(marker.getAlpha() == 0.7f){
                    for (int i = 0; i <= neederMarkers.size(); i++) {
                Log.e(TAG, "needer marker id: " + neederMarkers.get(i).getId());
                    if (marker.getId().equals(neederMarkers.get(i).getId())) {
                        messagebox.setVisibility(View.VISIBLE);
                        onSendButtonClickListener(i);
                        Toast.makeText(HelpMapActivity.this, "marker clicked", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                }else if(marker.getAlpha() == VOLUNTEER_ALPHA){
                    for (int i = 0; i <= volunteerMarkers.size(); i++) {
                        Log.e(TAG, "needer marker id: " + volunteerMarkers.get(i).getId());
                        if (marker.getId().equals(volunteerMarkers.get(i).getId())) {
                            messagebox.setVisibility(View.VISIBLE);
                            onSendButtonClickListener();
                            Toast.makeText(HelpMapActivity.this, "marker clicked", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                }
//

                return false;
            }
        });


        double myLocationLatitude = myLatLng.latitude;
        double myLocationLongitude = myLatLng.longitude;
        LatLng startLatLng = new LatLng(myLocationLatitude - 0.05, myLocationLongitude - 0.05);
        LatLng endLatLng = new LatLng(myLocationLatitude + 0.05, myLocationLongitude + 0.05);
        LatLngBounds viewPortLatLngBounds = new LatLngBounds(startLatLng, endLatLng);
        map.setLatLngBoundsForCameraTarget(viewPortLatLngBounds);
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(viewPortLatLngBounds, 100, 100, 10));

//        map.addMarker(new MarkerOptions().position(myLatLng).title("Me").alpha(0.3f));

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the currentUser grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

    }

    private void removeVolunteerMarker(DataSnapshot dataSnapshot) {
        Volunteer volunteer = dataSnapshot.getValue(Volunteer.class);

        if(volunteer.neederUId.equals(mAuth.getCurrentUser().getUid())) {

            LatLng volunteerLatLng = new LatLng(volunteer.latitude, volunteer.longitude);
            Marker marker = addIcon(iconFactory, volunteer.volunteerName, volunteerLatLng, volunteer.message,
                    volunteer.volunteerName, VOLUNTEER_ALPHA);


            volunteerMarkers.get(volunteerMarkers.indexOf(marker)).remove();
            volunteerMarkers.remove(marker);
//                    volunteerMarkers.contains(marker);
        }
        volunteerList.remove(volunteer);
    }

    private void createVolunteerMarkers(DataSnapshot dataSnapshot) {
        volunteerList.add(dataSnapshot.getValue(Volunteer.class));
        if(volunteerList.get(j).neederUId.equals(mAuth.getCurrentUser().getUid())){
            LatLng volunteerLatLng = new LatLng(volunteerList.get(j).latitude, volunteerList.get(j).longitude);
            iconFactory.setStyle(IconGenerator.STYLE_GREEN);
            iconFactory.setRotation(0);
            volunteerMarkers.add(addIcon(iconFactory, volunteerList.get(j).volunteerName,
                    volunteerLatLng, volunteerList.get(j).message,volunteerList.get(j).volunteerName,VOLUNTEER_ALPHA));
        }
        j++;
    }

    private void removeNeederMarker(DataSnapshot dataSnapshot) {
        Needer needer = dataSnapshot.getValue(Needer.class);

        if(!needer.uId.equals(mAuth.getCurrentUser().getUid())) {

            LatLng neederLatLng = new LatLng(needer.latitude, needer.longitude);
            Marker marker = addIcon(iconFactory, needer.userName, neederLatLng, needer.bloodGroup,
                    needer.userName, NEEDER_ALPHA);


            neederMarkers.get(neederMarkers.indexOf(marker)).remove();
            neederMarkers.remove(marker);
//                    volunteerMarkers.contains(marker);
        }
        neederList.remove(needer);
    }

    private void createNeederMarkers(DataSnapshot dataSnapshot) {

            neederList.add(dataSnapshot.getValue(Needer.class));
            if(!neederList.get(i).uId.equals(mAuth.getCurrentUser().getUid())) {
                LatLng neederLatLng = new LatLng(neederList.get(i).latitude, neederList.get(i).longitude);

                    iconFactory.setStyle(IconGenerator.STYLE_RED);
                    iconFactory.setRotation(0);
                    neederMarkers.add(addIcon(iconFactory, neederList.get(i).userName  , neederLatLng,neederList.get(i).bloodGroup
                            ,neederList.get(i).userName, NEEDER_ALPHA));
            }
            i++;
    }

//    private void createNeederMarkers(IconGenerator iconFactory, ArrayList<Marker> customMarker) {
//        if (neederList.size() != 0) {
//            for (int i = 0; i < neederList.size(); i++) {
//                if (!neederList.get(i).uId.equals(mAuth.getCurrentUser().getUid())) {
//                    LatLng neederLatLng = new LatLng(neederList.get(i).latitude, neederList.get(i).longitude);
//
//                    if (neederList.get(i).bloodGroup == null) {
//                        iconFactory.setStyle(IconGenerator.STYLE_RED);
//                        iconFactory.setRotation(0);
//                        customMarker.add(addIcon(iconFactory, neederList.get(i).userName + "\nneeds First Aid !", neederLatLng, "needer"));
//                    } else {
//                        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
//                        iconFactory.setRotation(270);
//                        customMarker.add(addIcon(iconFactory, neederList.get(i).userName + " needs Blood !", neederLatLng, "needer"));
//                    }
//
//                }
//            }
//        }
//    }

    public Marker addIcon(IconGenerator iconFactory, String title, LatLng position, String message,String userName, float alpha){

        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(title)))
                .position(position).title(userName).snippet(message).alpha(alpha);

        return map.addMarker(markerOptions);


    }

//    private void startIntentService(Location requiredLocation) {
//        Intent intent  = new Intent(this, FetchAddressIntentService.class);
//        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
//        intent.putExtra(Constants.LOCATION_DATA_EXTRA, requiredLocation);
//        startService(intent);
//    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, " onConnectionFailed works");

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseBackgroundService.isActivityStarted = true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e(TAG, "onConnected works");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            Intent intent = getIntent();
//            Log.e(TAG, "intent has has extra?   "+String.valueOf(intent.hasExtra(Constants.NEEDER)));
//            if(intent.hasExtra(Constants.NEEDER)) {
//                Bundle extra = intent.getBundleExtra(Constants.NEEDER);
//                neederList = extra.getParcelableArrayList(Constants.NEEDER);
//            }


//            myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            if (myLocation != null) {
//                myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
//            }
//            LocationTracker locationTracker = new LocationTracker(this);
        if(locationTracker.canGetLocation()){
            locationTracker.getLocation();
            myLatLng = new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude());
        }else{
            Log.e(TAG, "No provider");
        }
            MapFragment mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            mMapFragment.getMapAsync(this);

//            createLocationRequest();
//
//            startLocationUpdates();
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
        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        map.addMarker(new MarkerOptions().position(currentLatLng).title("New change loc"));

    }


}
