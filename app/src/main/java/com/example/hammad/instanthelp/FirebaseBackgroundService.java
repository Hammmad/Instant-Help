package com.example.hammad.instanthelp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.example.hammad.instanthelp.Fragments.HelpFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.TwitterAuthCredential;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by shekh chilli on 12/17/2016.
 */

public class FirebaseBackgroundService extends Service {

    private static final String TAG = "BgService/DEBUGGING";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HelpFragment helpFragment;
    private AddressResultReceiver addressResultReceiver;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        double latitude = intent.getDoubleExtra("LATITUDE", 0.0);
//        double longitude = intent.getDoubleExtra("LONGITUDE",0.0);
//
//        currentLocation = new Location("");
//        currentLocation.setLatitude(latitude);
//        currentLocation.setLongitude(longitude);
//
//        Log.e(TAG, latitude+","+longitude);
//        startIntentService(currentLocation);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        addressResultReceiver = new AddressResultReceiver(new Handler());


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded ");
                if(dataSnapshot.getKey().equals("userCurrentLocation")){
                    isCurrentUser(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                isCurrentUser(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });



//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot currentLocationSnapshot: dataSnapshot.child("userCurrentLocation").getChildren()){
//                    CurrentLocation currentLocation = currentLocationSnapshot.getValue(CurrentLocation.class);
//                    String currentUid = mAuth.getCurrentUser().getUid();
//                    String snapshotUid = currentLocation.uId;
//
//                    Log.e(TAG, currentUid + "     "+ snapshotUid);
//
//                    if(currentUid.equals(snapshotUid)) {
//                        Toast.makeText(FirebaseBackgroundService.this, snapshotUid, Toast.LENGTH_SHORT).show();
//                        NotifyVolunteers("he is the currrent user");
//
//                    }else {
//                        String userName = mAuth.getCurrentUser().getEmail().replace("@instanthelp.com", "");
//                        NotifyVolunteers(userName);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };

    }

    private void isCurrentUser(DataSnapshot dataSnapshot) {
        for(DataSnapshot requiredSnapshot: dataSnapshot.getChildren()){
            CurrentLocation currentLocation = requiredSnapshot.getValue(CurrentLocation.class);

            Location location = new Location("");
            location.setLatitude(currentLocation.latitude);
            location.setLongitude(currentLocation.longitude);
                Log.e(TAG, currentLocation.uId+"      "+ mAuth.getCurrentUser().getUid());
                if (!currentLocation.uId.equals(mAuth.getCurrentUser().getUid())) {

                    startIntentService(location,currentLocation.userName, currentLocation.bloodGroup);

                }else{
                    Log.e(TAG, "user is same");
                }
        }
    }

    private void startIntentService(Location requiredLocation,String userName, String bloodGroup) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, requiredLocation);
        intent.putExtra(Constants.USERNAME,userName);
        intent.putExtra(Constants.BLOODGROUP, bloodGroup);
        startService(intent);
    }


//    public void notifyVolunteers(){
//
//        childListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//                Log.e(TAG, "onChildAdded");
//                for (DataSnapshot currentLocationSnapshot: dataSnapshot.child("userCurrentLocation").getChildren()) {
//                        CurrentLocation currentLocation = currentLocationSnapshot.getValue(CurrentLocation.class);
//
//                    if (!currentLocation.uId.equals(mAuth.getCurrentUser().getUid())) {
//
//                            createNotification(currentLocation.userName, currentLocation.bloodGroup);
//
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.e(TAG, "onChildChanged: " + s);
//                for (DataSnapshot currentLocationSnapshot: dataSnapshot.child("userCurrentLocation").getChildren()) {
//                    CurrentLocation currentLocation = currentLocationSnapshot.getValue(CurrentLocation.class);
//
//                    if (!currentLocation.uId.equals(mAuth.getCurrentUser().getUid())) {
//                        createNotification(currentLocation.userName,currentLocation.bloodGroup);
//                    }
//                }
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.e(TAG, "onChildRemoved");
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                Log.e(TAG, "onChildMoved");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e(TAG, "onCancelled");
//            }
//        };
//
//
//    }

    class AddressResultReceiver extends android.support.v4.os.ResultReceiver{

        private static final String TAG = "DEBUGGING";


        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            String userName = resultData.getString(Constants.USERNAME);
            String bloodGroup = resultData.getString(Constants.BLOODGROUP);
            if(resultCode == Constants.RESULT_SUCCESS){


                Log.e(TAG, addressOutput + userName + bloodGroup);
                createNotification(userName, bloodGroup, addressOutput);

            }else{
                    Log.e(TAG, "Failed result code");
            }
        }

        private void createNotification(String userName, String bloodGroup, String address) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FirebaseBackgroundService.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Blood Required !!")
                    .setContentText(userName +" required "+ bloodGroup+ " blood at "+address);

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//
//        String[] events = new String[6];
//        inboxStyle.setBigContentTitle("Blood Required");
//
//        for(int i=0; i<events.length; i++){
//            inboxStyle.addLine(events[i]);
//        }
//        mBuilder.setStyle(inboxStyle);

            Intent resultintent = new Intent(FirebaseBackgroundService.this, HelpMapActivity.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(FirebaseBackgroundService.this);

            stackBuilder.addParentStack(HelpMapActivity.class);

            stackBuilder.addNextIntent(resultintent);

            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int notifyId = 1;
//
//        int numMessages = 0;
//
//        mBuilder.setContentText(userName +" blood required at").setNumber(++numMessages);

            notificationManager.notify(notifyId, mBuilder.build());

        }

    }
}
