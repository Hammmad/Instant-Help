package com.example.hammad.instanthelp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//
//            }
//        };

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
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

        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildChanged: " + s);
                CurrentLocation currentLocation = dataSnapshot.child("userCurrentLocation").getValue(CurrentLocation.class);
                Log.e(TAG, "onChildChanged:" + currentLocation.bloodGroup);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled");
            }
        };
        databaseReference.addChildEventListener(childListener);
    }

    private void NotifyVolunteers(String userName) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Blood Required !!")
                .setContentText(userName +" required blood at");

        Intent resultintent = new Intent(this, HelpMapActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addParentStack(HelpMapActivity.class);

        stackBuilder.addNextIntent(resultintent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());

    }
}
