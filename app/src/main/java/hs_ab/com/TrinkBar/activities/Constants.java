package hs_ab.com.TrinkBar.activities;



public class Constants {

    private Constants() {
    }


    static final String PLACES_API_KEY="AIzaSyC2144RCdtuiUP2HF-lMNg3Q9raPDmQy2M";
    static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    static final long GEO_DURATION = 60 * 60 * 1000;
    static final String GEOFENCE_REQ_ID = "My Geofence";
    static final float GEOFENCE_RADIUS = 20.0f; // in meters
    static final int INTENT_REQ_CODE_GEOFENCE = 10;
    static final int INTENT_REQ_CODE_PUSH_NOTIFICATION = 0;
    static final int INTENT_REQ_CODE_NOTIFICATION = 1000;
    static final int PUSH_NOTIFICATION_REQ_CODE = 20;
}
