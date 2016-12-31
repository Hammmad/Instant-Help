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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.hammad.instanthelp.Fragments.HelpFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by shekh chilli on 12/17/2016.
 */

public class FirebaseBackgroundService extends Service {

    private static final String TAG = "BgService/DEBUGGING";
    protected static Boolean isActivityStarted = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private HelpFragment helpFragment;
    private AddressResultReceiver addressResultReceiver;
    private double latitude;
    private double longitude;
    private String neederUid;
    private ArrayList<Needer> needersList;
    int notifyId = 1;
    int i = 0;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        addressResultReceiver = new AddressResultReceiver(new Handler());
        needersList = new ArrayList<>();

        databaseReference.child("userCurrentLocation").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded ");

                    isCurrentUser(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

        databaseReference.child("Volunteer").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded:   "+dataSnapshot.getKey());

                Volunteer volunteer = dataSnapshot.getValue(Volunteer.class);
                if(isActivityStarted){
                    if(volunteer.neederUId.equals(mAuth.getCurrentUser().getUid())){

                        createNotification(volunteer.volunteerName+": "+volunteer.message);
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "pnChildChange: "+ dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void isCurrentUser(DataSnapshot dataSnapshot) {


        Log.e(TAG,"method is working");

//        for (DataSnapshot requiredSnapshot: dataSnapshot.getChildren()){
            needersList.add(dataSnapshot.getValue(Needer.class));
            Location location = new Location("");
            latitude = needersList.get(i).latitude;
            longitude =  needersList.get(i).longitude;
            location.setLatitude(latitude);
            location.setLongitude(longitude);
                if (! needersList.get(i).uId.equals(mAuth.getCurrentUser().getUid())) {

                    startIntentService(location,  needersList.get(i).userName,  needersList.get(i).bloodGroup);

                }else{
                        Log.e(TAG, "User is same");
                }
            i++;
//        }
    }
    private void createNotification(String message,Bundle bundle) {



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FirebaseBackgroundService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Help !!!")
                .setContentText(message)
                .setAutoCancel(true).setPriority(2)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

//        String[] events = new String[6];
//        inboxStyle.setBigContentTitle("Blood Required");
//
//        for(int i=0; i<events.length; i++){
//            inboxStyle.addLine(events[i]);
//        }
//        mBuilder.setStyle(inboxStyle);

        Intent resultintent = new Intent(FirebaseBackgroundService.this, HelpMapActivity.class);
        resultintent.putExtra(Constants.NEEDER, bundle);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(FirebaseBackgroundService.this);

        stackBuilder.addParentStack(HelpMapActivity.class);

        stackBuilder.addNextIntent(resultintent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



//        int numMessages = 0;
//
//        mBuilder.setContentText(userName +" required "+ bloodGroup+ " blood at "+address).setNumber(++numMessages);


        notificationManager.notify(notifyId++, mBuilder.build());

    }
    private void createNotification(String message) {



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FirebaseBackgroundService.this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Help !!!")
                .setContentText(message)
                .setAutoCancel(true).setPriority(2)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

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



//        int numMessages = 0;
//
//        mBuilder.setContentText(userName +" required "+ bloodGroup+ " blood at "+address).setNumber(++numMessages);


        notificationManager.notify(notifyId++, mBuilder.build());

    }


    private void startIntentService(Location requiredLocation,String userName, String bloodGroup) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, requiredLocation);
        intent.putExtra(Constants.USERNAME,userName);
        intent.putExtra(Constants.BLOODGROUP, bloodGroup);

        startService(intent);
    }




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


                String message;
                if(bloodGroup != null)          {message = userName +" required "+ bloodGroup+ " blood at "+addressOutput; }
                else                            {message = userName +" required First Aid at " + addressOutput; }
                if(isActivityStarted) {
                    createNotification(message);
                }

            }else{
                    Log.e(TAG, "Failed result code");
            }
        }


    }
}
