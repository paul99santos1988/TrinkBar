package hs_ab.com.TrinkBar;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DBBackgroundService extends IntentService {

    private DBAdapter myDatabaseAdapter;

    private static final String TAG = "DBBackgroundService";

    public DBBackgroundService() {
        super("DBBackgroundService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "DB open");

        //Activity erzeugt Instanz des Adapters!!!! not this!!! getActivity()
        myDatabaseAdapter = new DBAdapter(getApplicationContext());
        myDatabaseAdapter.open();

        myDatabaseAdapter.insertData("hallo" );
        Cursor logOutput = myDatabaseAdapter.getAllData();
        logOutput.moveToFirst();
        String logMessage = logOutput.getString(0);

        Log.i(TAG,logMessage);/**/

    }

    //get database data to compare
    private void getCloudData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
