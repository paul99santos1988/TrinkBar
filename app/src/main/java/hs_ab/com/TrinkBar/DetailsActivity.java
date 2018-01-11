package hs_ab.com.TrinkBar;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static DetailsActivity mInstance;


    private Context mCtx;
    private static final String TAG = "LOG";
    private String mTitle;
    ImageView mImg;
    String mLink;
    TextView mText;
    private List<OpeningHour> mOpeninghours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mInstance=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mCtx = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTitle = getIntent().getStringExtra("EXTRA_DETAILS_TITLE");
        setTitle(mTitle);
        mImg = (ImageView) findViewById(R.id.imageview_details);
        mText = (TextView) findViewById(R.id.textview_details);

        // FAB fromDetails Activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_bottom);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Öffnungszeiten: " + mOpeninghours, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Instantiate the RequestQueue.
        /*RequestQueue queue = Volley.newRequestQueue(mCtx);
        String url = "http://sightseeing-fhws.azurewebsites.net/";

        // Request a data from server and pass it to the variables from the layout
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String data = response;
                        try {
                            JSONObject obj = new JSONObject(data.toString());
                            JSONArray places = obj.getJSONArray("placesOfInterest");
                            Log.d(TAG + "DETAILS", places.toString());

                            for (int i = 0; i < places.length(); i++) {
                                String name = places.getJSONObject(i).getString("name");
                                Log.d(TAG + "DETAILS", name);
                                if (name.equals(mTitle)) {
                                    Log.d(TAG + "DETAILS", "IN IF");
                                    mLink = places.getJSONObject(i).getString("image_link");
                                    Log.d(TAG + "IMAGE", mLink);
                                    Picasso.with(mCtx).load(mLink).into(mImg);
                                    String description = places.getJSONObject(i).getString("description");
                                    mOpeninghours = places.getJSONObject(i).getString("opening_hours");
                                    byte[] descriptionByte = Base64.decode(description, Base64.DEFAULT);
                                    try {
                                        String decodedDescription = new String(descriptionByte, "UTF-8");
                                        mText.setText(decodedDescription);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG + " DETAILS", description);
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);*/




    String url="https://trinkbar.azurewebsites.net/";

//

        HttpGetRequest myCustomRequest=new HttpGetRequest(Request.Method.GET, url,Bars.class, new Response.Listener<Bars>() {
        @Override
        public void onResponse(Bars bar) {

            List<Bar> barList = bar.getBars();

            for (int i = 0; i < barList.size(); i++) {
                Bar barObject = barList.get(i);
                if (barObject.getName().equals(mTitle)){
                    Log.d(TAG + "DETAILS", "IN IF");
                    mLink = barObject.getImageLink();
                    Log.d(TAG + "IMAGE", mLink);
                    //Picasso.with(mCtx).load(mLink).into(mImg);
                    String description = barObject.getDescription();
                    mOpeninghours = barObject.getOpeningHours();
                    byte[] descriptionByte = Base64.decode(description, Base64.DEFAULT);
                    try {
                        String decodedDescription = new String(descriptionByte, "UTF-8");
                        mText.setText(decodedDescription);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG + " DETAILS", description);

                }


            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError response) {

        //Failure callback
            Toast.makeText(DetailsActivity.this,"Error Encountered",Toast.LENGTH_SHORT).show();

        }
    });

        //Adding the request to a request queue
        DetailsActivity.getInstance().addToRequestQueue(myCustomRequest,"tag");

    }

    public RequestQueue getRequestQueue()
    {
        if (requestQueue==null)
            requestQueue= Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }

    public void addToRequestQueue(Request request,String tag)
    {
        request.setTag(tag);
        getRequestQueue().add(request);

    }
    public void cancelAllRequests(String tag)
    {
        getRequestQueue().cancelAll(tag);
    }

    public static synchronized DetailsActivity getInstance()
    {
        return mInstance;
    }
}
