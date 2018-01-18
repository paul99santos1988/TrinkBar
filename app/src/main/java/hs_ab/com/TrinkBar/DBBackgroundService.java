package hs_ab.com.TrinkBar;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Bars;
import hs_ab.com.TrinkBar.models.Image;


public class DBBackgroundService extends IntentService {

    private DBAdapter myDatabaseAdapter;
    private List<Bar> barList;
    private static DBBackgroundService mInstance;

    private static final String TAG = "DBBackgroundService";

    public DBBackgroundService() {
        super("DBBackgroundService");
    }
    private Gson gson= new Gson();
    RequestQueue queue;


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "DB open");

        myDatabaseAdapter = new DBAdapter(getApplicationContext());
        myDatabaseAdapter.open();

        queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://trinkbar.azurewebsites.net/files/bars.json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String barsString = response.toString();
                        Bars barObject = gson.fromJson(barsString, Bars.class);
                        barList = barObject.getBars();

                            for(int i=0; i<barList.size(); i++){


                                String barString = gson.toJson(barList.get(i));
                                myDatabaseAdapter.insertBars(barList.get(i).getId(), barString);

                                String imageUrl = barList.get(i).getImageLink();
                                StringRequest imageRequest = new StringRequest(Request.Method.GET, "https://trinkbar.azurewebsites.net/"+ imageUrl,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String imageString = response.toString();

                                        Image imageObject = gson.fromJson(imageString, Image.class);
                                        // key = imageObject.getId()
                                        myDatabaseAdapter.insertImage(imageObject.getId(), response);
                                        //database entry for images
                                        // data = response

                                        String logMessage_id;
                                        String logMessage_details;

                                        Cursor logOutput_table_details = myDatabaseAdapter.getAllDataTableBars();

                                        int numbDBrows_table_details = logOutput_table_details.getCount();
                                        logOutput_table_details.moveToLast();

                                        while(numbDBrows_table_details >= 0) {
                                            logMessage_id = logOutput_table_details.getString(0);
                                            logMessage_details = logOutput_table_details.getString(1);
                                            Log.i(TAG, logMessage_id);
                                            Log.i(TAG, logMessage_details);
                                            numbDBrows_table_details--;
                                            if ((numbDBrows_table_details != 0) && (numbDBrows_table_details > 0)) {
                                                logOutput_table_details.moveToPrevious();
                                            }
                                        }




        /*//receive Cursor object with all DB data
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
        }


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

        myDatabaseAdapter.close();
        */

                                    }
                                }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                                });
                        queue.add(imageRequest);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);

    }

    //get database data to compare
    private void getCloudData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void getData(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
