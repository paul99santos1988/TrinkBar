package hs_ab.com.TrinkBar.activities;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;

public class LocationDistance {

    private static final String TAG = "Location";
    private static LocationDistance mLocationDistance = null;
    private Context mctx;
    private List<Bar> mBarList;
    DistanceCallback mcallback;
    private RealtimeDBAdapter mRtDatabase;

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    LocationDistance.LocationListener[] mLocationListeners = new LocationDistance.LocationListener[] {
            new LocationDistance.LocationListener(LocationManager.GPS_PROVIDER),
            new LocationDistance.LocationListener(LocationManager.NETWORK_PROVIDER)
    };


    //constructor
    protected LocationDistance(Context context) {
        mctx = context;
    }


    public static LocationDistance getInstance(Context context) {
        if (mLocationDistance == null) {
            mLocationDistance = new LocationDistance(context);
        }
        return mLocationDistance;
    }

    public void setCallbacks(DistanceCallback callbacks) {
        mcallback = callbacks;
    }

    public void calculateDistance() {

        mRtDatabase = RealtimeDBAdapter.getInstance(mctx);

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mctx.getSystemService(Context.LOCATION_SERVICE);
        }


        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    private double distance(double lat1, double lat2, double lon1,
                            double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public void close(){

        Log.d(TAG, "onDestroy");
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }

    }

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.d(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.d(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            mBarList = mRtDatabase.getBarList();
            if(mBarList!=null){
                for (int i=0; i<mBarList.size();i++){
                    double dis= distance(location.getLatitude(),Double.valueOf(mBarList.get(i).getCoordinates().getLatitude()),location.getLongitude(),Double.valueOf(mBarList.get(i).getCoordinates().getLongitude()),0,0);
                    //if(dis > 1000){
                      //  dis=dis/1000; //convert to km
                        //DecimalFormat twoDForm = new DecimalFormat("#.##");
                        //mBarList.get(i).setDistance(String.valueOf(Double.valueOf(twoDForm.format(dis)))+ " km");
                        mBarList.get(i).setDistance(String.valueOf(dis));
                    //}
                    //else{
                    //    mBarList.get(i).setDistance(String.valueOf((int)dis));
                    //}



                }
                mcallback.callbackCall(mBarList);
            }
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.d(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.d(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.d(TAG, "onStatusChanged: " + provider);
        }
    }



}
