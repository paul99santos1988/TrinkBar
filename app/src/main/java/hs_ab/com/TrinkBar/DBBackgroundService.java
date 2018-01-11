package hs_ab.com.TrinkBar;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DBBackgroundService extends IntentService {

    private static final String TAG = "LOG";

    public DBBackgroundService() {
        super("DBBackgroundService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
//            final String action = intent.getAction();
//            final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//            final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//            handleActionFoo(param1, param2);
              Log.d(TAG, "DBBackgroundService: onHandleIntent started");
        }
    }

    //get database data to compare
    private void getCloudData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
