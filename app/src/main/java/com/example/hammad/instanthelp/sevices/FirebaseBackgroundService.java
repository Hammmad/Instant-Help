package com.example.hammad.instanthelp.sevices;

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


import com.example.hammad.instanthelp.models.Constants;
import com.example.hammad.instanthelp.activity.HelpMapActivity;
import com.example.hammad.instanthelp.utils.CurrentUser;
import com.example.hammad.instanthelp.utils.LocationTracker;
import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.models.Needer;
import com.example.hammad.instanthelp.models.User;
import com.example.hammad.instanthelp.models.Volunteer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by Hammad on 12/17/2016.
 */

public class FirebaseBackgroundService extends Service {

    private static final String TAG = "BgService/DEBUGGING";
    public static Boolean isActivityStarted = false;
    private FirebaseAuth mAuth;
    private AddressResultReceiver addressResultReceiver;
    boolean isBloodDonor;
    Location location;
    boolean isFirstAider;
    int notifyId = 1;
    LocationTracker locationTracker;
    int i = 0;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        locationTracker = new LocationTracker(this);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        addressResultReceiver = new AddressResultReceiver(new Handler());

        CurrentUser currentUser = new CurrentUser(this);
        User user = currentUser.getCurrentUser();
        isBloodDonor  = user.bloodDonor;
        isFirstAider  = user.firstAider;

//        userInfoListener(databaseReference);

        userCurrentLocationListener(databaseReference);

        volunteerListener(databaseReference);




        return START_STICKY;
    }

    private void userInfoListener(DatabaseReference databaseReference) {
        databaseReference.child("userinfo").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG,"userInfo Listener:   "+dataSnapshot.getKey());
                User currentUser = dataSnapshot.getValue(User.class);
                isBloodDonor = currentUser.bloodDonor;
                isFirstAider = currentUser.firstAider;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void volunteerListener(DatabaseReference databaseReference) {
        databaseReference.child("Volunteer").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e(TAG, "onChildAdded:   "+dataSnapshot.getKey());

                Volunteer volunteer = dataSnapshot.getValue(Volunteer.class);
                if(!isActivityStarted  && volunteer.neederUId.equals(mAuth.getCurrentUser().getUid())){
                        createNotification(volunteer.volunteerName, volunteer.message);
                    }
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

            }
        });
    }

    private void userCurrentLocationListener(DatabaseReference databaseReference) {
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
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void isCurrentUser(DataSnapshot dataSnapshot) {


        Log.e(TAG,"method is working");

//        for (DataSnapshot requiredSnapshot: dataSnapshot.getChildren()){
            Needer needer = (dataSnapshot.getValue(Needer.class));
        location = new Location("");
        location.setLatitude(needer.latitude);
            location.setLongitude(needer.longitude);
                if (! needer.uId.equals(mAuth.getCurrentUser().getUid())) {

                    startIntentService(location,  needer.userName,  needer.bloodGroup);

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
//        for(int neederMarkerIndex=0; neederMarkerIndex<events.length; neederMarkerIndex++){
//            inboxStyle.addLine(events[neederMarkerIndex]);
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
    private void createNotification(String title, String message) {



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(FirebaseBackgroundService.this)
                .setSmallIcon(R.drawable.help_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true).setPriority(2)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

//        String[] events = new String[6];
//        inboxStyle.setBigContentTitle("Blood Required");
//
//        for(int neederMarkerIndex=0; neederMarkerIndex<events.length; neederMarkerIndex++){
//            inboxStyle.addLine(events[neederMarkerIndex]);
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
                if(bloodGroup != null ){
                    message = "Required "+ bloodGroup+ " blood at "+addressOutput;
                    if(!isActivityStarted && isBloodDonor) {
                        if (location.getLatitude() < (locationTracker.getLatitude() + 0.02) &&
                                location.getLongitude() < (locationTracker.getLongitude() + 0.02) &&
                                location.getLatitude() > (locationTracker.getLatitude() - 0.02) &&
                                location.getLongitude() > (locationTracker.getLongitude() - 0.02)
                                ) {
                            createNotification(userName, message);
                        }
                    }
                }
                else {
                    message = "Required First Aid at " + addressOutput;
                    if(!isActivityStarted && isFirstAider){
                        if (location.getLatitude() < (locationTracker.getLatitude() + 0.02) &&
                                location.getLongitude() < (locationTracker.getLongitude() + 0.02) &&
                                location.getLatitude() > (locationTracker.getLatitude() - 0.02) &&
                                location.getLongitude() > (locationTracker.getLongitude() - 0.02)
                                ){
                        createNotification(userName, message);
                        }
                    }
                }
            }else{
                    Log.e(TAG, "Failed result code");
            }
        }


    }
}
