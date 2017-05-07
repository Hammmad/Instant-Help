package com.example.hammad.instanthelp.sevices;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;

import com.example.hammad.instanthelp.R;
import com.example.hammad.instanthelp.activity.HelpMapActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coder on 21/01/2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = "TransitionService/DEBUG";

    public GeofenceTransitionsIntentService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(geofencingEvent.hasError()){
            String errorMeassage = String.valueOf(geofencingEvent.getErrorCode());
            Log.d(TAG, errorMeassage);

            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );

            createNotification(geofenceTransitionDetails, "click!! to return to App");
        }
    }

    private String getGeofenceTransitionDetails(
            Context context,
            int geofenceTransition,
            List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        // Get the Ids of each geofence that was triggered.
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
    }


    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return getString(R.string.geofence_transition_dwell);
            default:
                return getString(R.string.unknown_geofence_transition);
        }
    }

    private void createNotification(String title, String message) {



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GeofenceTransitionsIntentService.this)
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

        Intent resultintent = new Intent(GeofenceTransitionsIntentService.this, HelpMapActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(GeofenceTransitionsIntentService.this);

        stackBuilder.addParentStack(HelpMapActivity.class);

        stackBuilder.addNextIntent(resultintent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



//        int numMessages = 0;
//
//        mBuilder.setContentText(userName +" required "+ bloodGroup+ " blood at "+address).setNumber(++numMessages);


        notificationManager.notify(0, mBuilder.build());

    }

}
