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

        //receive Cursor object with all DB data
        Cursor logOutput_table_details = myDatabaseAdapter.getAllDataTableBars();
        Cursor logOutput_table_images = myDatabaseAdapter.getAllDataTableBarImages();
        String logMessage_id;
        String logMessage_details;

        int numbDBrows_table_details = logOutput_table_details.getCount();
        int numbDBrows_table_images = logOutput_table_images.getCount();

        if(numbDBrows_table_details == 0) {
            Log.i(TAG, "insert into table details, if not exists");
            //test data 1
            myDatabaseAdapter.insertBars("testbar1", "firstDetails");
            myDatabaseAdapter.insertBars("testbar2", "secondDetails");
            myDatabaseAdapter.insertBars("testbar3", "thirdDetails");
        }

        if(numbDBrows_table_images == 0) {
            Log.i(TAG, "insert into table images, if not exists");
            //test data 2
            myDatabaseAdapter.insertImage("testbar1", "firstImage");
            myDatabaseAdapter.insertImage("testbar2", "secondImage");
            myDatabaseAdapter.insertImage("testbar3", "thirdImage");
        }/**/
        /**/

        logOutput_table_details = myDatabaseAdapter.getAllDataTableBars();
        logOutput_table_images = myDatabaseAdapter.getAllDataTableBarImages();

        numbDBrows_table_details = logOutput_table_details.getCount();
        numbDBrows_table_images = logOutput_table_images.getCount();
        logOutput_table_details.moveToLast();

        while(numbDBrows_table_details >= 0){
            logMessage_id = logOutput_table_details.getString(0);
            logMessage_details = logOutput_table_details.getString(1);
            Log.i(TAG,logMessage_id);
            Log.i(TAG,logMessage_details);
            numbDBrows_table_details--;
            if((numbDBrows_table_details != 0) && (numbDBrows_table_details > 0) ) {
                logOutput_table_details.moveToPrevious();
            }
        }

        Log.i(TAG, "test getDataTableBarById");
        logOutput_table_details = myDatabaseAdapter.getDataTableBarById("testbar1");
        String inhaltspalteMitIDgedoens;
        int inhaltVorhanden;

        inhaltVorhanden = logOutput_table_details.getCount();
        logOutput_table_details.moveToFirst();
        inhaltspalteMitIDgedoens = logOutput_table_details.getString(0);

        Log.i(TAG, inhaltspalteMitIDgedoens);
        /*
        while(numbDBrows > 0){
            myDatabaseAdapter.removeData();
            }
        }
        */
        myDatabaseAdapter.close();
    }

    //get database data to compare
    private void getCloudData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
