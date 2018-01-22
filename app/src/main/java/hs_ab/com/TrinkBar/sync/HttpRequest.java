package hs_ab.com.TrinkBar.sync;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

import hs_ab.com.TrinkBar.adapters.DBAdapter;
import hs_ab.com.TrinkBar.interfaces.Callback;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Bars;
import hs_ab.com.TrinkBar.models.Image;

/**
 * Created by agrein on 1/22/18.
 */

public class HttpRequest {

    private static HttpRequest mhttpRequest = null;
    private Context mctx;
    RequestQueue queue;
    private int requestCount=0;
    private DBAdapter mDb;
    private List<Bar> barList;
    Callback mcallback;
    private Gson gson;

    //constructor
    protected HttpRequest(Context context){
        mctx= context;
        gson= new Gson();
    }


    public static HttpRequest getInstance(Context context) {
        if(mhttpRequest == null) {
            mhttpRequest = new HttpRequest(context);
        }
        return mhttpRequest;
    }

    public void setCallbacks(Callback callbacks) {
        mcallback = callbacks;
    }

    public void getRequest(){


        mDb = DBAdapter.getInstance(mctx);

        queue = Volley.newRequestQueue(mctx);
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
                            mDb.insertBars(barList.get(i).getId(), barString);

                            String imageUrl = barList.get(i).getImageLink();
                            StringRequest imageRequest = new StringRequest(Request.Method.GET, "https://trinkbar.azurewebsites.net/"+ imageUrl,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            String imageString = response.toString();

                                            Image imageObject = gson.fromJson(imageString, Image.class);
                                            mDb.insertImage(imageObject.getId(), response);
                                            requestCount--;
                                            if(requestCount == 0){
                                                mcallback.callbackCall();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                            queue.add(imageRequest);
                            requestCount ++;
                        }
                        requestCount--;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
        requestCount ++;




    }
}
