package hs_ab.com.TrinkBar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import hs_ab.com.TrinkBar.activities.MapActivity;

import static android.graphics.Color.WHITE;

/**
 * Created by agrein on 3/12/18.
 */

public class ProximityAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        final String key = LocationManager.KEY_PROXIMITY_ENTERING;
        final Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            Toast.makeText(context, "entering", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "exiting", Toast.LENGTH_SHORT).show();
        }

        //LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent,  PendingIntent.FLAG_NO_CREATE);
        //locManager.removeProximityAlert(pendingIntent);

    }
}
