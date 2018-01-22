package hs_ab.com.TrinkBar;

import android.app.IntentService;
import android.content.Intent;
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

        myDatabaseAdapter = DBAdapter.getInstance(getApplicationContext());

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
                                        myDatabaseAdapter.insertImage(imageObject.getId(), response);

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
