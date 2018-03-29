package hs_ab.com.TrinkBar.activities;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;


public class GeofenceTrasitionService extends IntentService {

    private static final String TAG = GeofenceTrasitionService.class.getSimpleName();

    public static final int GEOFENCE_NOTIFICATION_ID = 0;
    private Bar BarNR;
    private RealtimeDBAdapter mRtDatabase;
    private DatabaseReference mDatabase;
    private Bar mEnteredBar;

    public GeofenceTrasitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        // Handling errors
        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        // Check if the transition type is of interest
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            // Get the geofence that were triggered
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );

            // Send notification details as a String
            sendNotification( geofenceTransitionDetails );
        }
    }



    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        // get the ID of each geofence triggered
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for ( Geofence geofence : triggeringGeofences ) {
            triggeringGeofencesList.add( geofence.getRequestId() );
        }

        //get Firebase database reference
        triggeringGeofences.get(0);
        ListIterator<String> iterator = triggeringGeofencesList.listIterator();
        String barName = iterator.next();


        mDatabase= MapActivity.getDatabaseInstance();
        mRtDatabase = RealtimeDBAdapter.getInstance(getApplicationContext());
        mEnteredBar= mRtDatabase.getBarbyName(barName);

        String barId= mEnteredBar.getId();
        String barVisitors= mEnteredBar.getVisitor();
        String status = null;
        //exception warning
        if(barId == null || barVisitors == null){
            Log.w(TAG, "Can not iterate visitor count, no bars(barId="+barId+") or visitors(barVisitors="+barVisitors+"); failure value = null");
        }
        else {
            //increment or decrement the count of visitors depending on the geoFenceTransition from the catched intent
            if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                status = "Entering ";
                Log.d(TAG, "Enter Geofence");
                int visitors = Integer.valueOf(barVisitors) + 1;
                mEnteredBar.setVisitor(String.valueOf(visitors));
                mDatabase.child("bars").child(barId).child("visitor").setValue(mEnteredBar.getVisitor()); //iterate visitor number
                requestWithSomeHttpHeaders(mEnteredBar.getId(), mEnteredBar.getName(), mEnteredBar.getVisitor());
                FirebaseMessaging.getInstance().subscribeToTopic(mEnteredBar.getId());
            } else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                status = "Exiting ";
                Log.d(TAG, "Exit Geofence");
                int visitors = Integer.valueOf(barVisitors) - 1;
                mEnteredBar.setVisitor(String.valueOf(visitors));
                mDatabase.child("bars").child(barId).child("visitor").setValue(mEnteredBar.getVisitor()); //decrement of visitor number
                FirebaseMessaging.getInstance().unsubscribeFromTopic(mEnteredBar.getId());
                mEnteredBar=null;
            }
        }
        return status + TextUtils.join( ", ", triggeringGeofencesList);
    }


    private void sendNotification( String msg ) {
        Log.i(TAG, "sendNotification: " + msg );

        // Intent to start the main Activity
        Intent notificationIntent = MapActivity.makeNotificationIntent(getApplicationContext(), msg, mEnteredBar);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, Constants.INTENT_REQ_CODE_NOTIFICATION, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Creating and sending Notification
        NotificationManager notificatioMng =
                (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificatioMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));

    }


    // Create notification
    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_sheep)
                .setColor(Color.BLACK)
                .setContentTitle(msg)
                .setContentText("Geofence Notification!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }


    private static String getErrorString(int errorCode) {

        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }

    public void requestWithSomeHttpHeaders(String topic, String title, String body) {
        //String jsonString= "{'message':{'topic' : 'news','notification' : {'body' : 'This is a Firebase Cloud Messaging Topic Message!','title' : 'FCM Message'}}}";
        String jsonString = "{'notification':{'title': '"+title+"', 'body': 'Jemand hat die Bar betreten. Aktuell befinden sich "+body+" Personen hier', 'click_action' : 'https://dummypage.com'},'to' : '/topics/"+topic+"'}";


        JSONObject jsonObj=null;
        try {
            jsonObj = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://fcm.googleapis.com/fcm/send";
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url,jsonObj,
                new Response.Listener<JSONObject >()
                {
                    @Override
                    public void onResponse(JSONObject  response) {
                        // response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "key=AAAA5Z42Xy0:APA91bFVa_btC8OjcoVVqh_NX0E8kbxhZ0ViNEKO-PFB5jt2YQ3xm1dWZSWqGmSbIgfIKQn8X6-PlClwtCqMlpM9bE8p13Vfql-ItvpkC81GMki3-MNskarK0Ac01Ewskj7pKNjYHAwD");

                return params;
            }
        };
        queue.add(postRequest);

    }

}