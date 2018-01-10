package hs_ab.com.TrinkBar;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by agrein on 1/10/18.
 */

public class HttpGetRequest extends Request {


    private Response.Listener listener;
    private Gson gson;
    private Class responseClass;


    public HttpGetRequest(int method, String url,Class responseClass, Response.Listener listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        gson = new Gson();
        this.listener=listener;
        this.responseClass=responseClass;
    }


    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        try {


            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
            Object test = gson.fromJson(jsonString, responseClass);

            return Response.success(gson.fromJson(jsonString,responseClass), HttpHeaderParser.parseCacheHeaders(response));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    /**
     This is called on the main thread with the object you returned in
     parseNetworkResponse(). You should be invoking your callback interface
     from here
     **/
    @Override
    protected void deliverResponse(Object response) {

        listener.onResponse(response);

    }
}
