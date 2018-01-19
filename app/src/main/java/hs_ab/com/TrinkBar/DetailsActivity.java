package hs_ab.com.TrinkBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Bars;
import hs_ab.com.TrinkBar.models.Image;
import hs_ab.com.TrinkBar.models.OpeningHours;

public class DetailsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static DetailsActivity mInstance;


    private Context mCtx;
    private static final String TAG = "LOG";
    private String mTitle;
    ImageView mImg;
    String mLink;
    private String barId;
    TextView mText;
    private String mOpeninghours;
    private Bar barObject;
    private DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mInstance=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mCtx = getApplicationContext();

        barId = getIntent().getStringExtra("EXTRA_DETAILS_TITLE");


        db = DBAdapter.getInstance(mCtx);
        barObject = db.getBarbyId(barId);
        mTitle = barObject.getName();
        setTitle(mTitle);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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




        Image image= db.getImagebyId(barId);
        barObject.setImageData(image.getImage());

        String description = barObject.getDescription();

        //Öffnungszeiten
        mOpeninghours = barObject.getOpeningHours().getMonday();
        mOpeninghours = mOpeninghours + barObject.getOpeningHours().getSunday();

        byte[] descriptionByte = Base64.decode(description, Base64.DEFAULT);
        try {
            String decodedDescription = new String(descriptionByte, "UTF-8");
            mText.setText(decodedDescription);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64String = barObject.getImageData();
        String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        mImg.setImageBitmap(decodedByte);

        /*
        String url="https://trinkbar.azurewebsites.net/files/bars.json";

//

        HttpGetRequest myCustomRequest=new HttpGetRequest(Request.Method.GET, url,Bars.class, new Response.Listener<Bars>() {
        @Override
        public void onResponse(Bars bar) {

            List<Bar> barList = bar.getBars();

            for (int i = 0; i < barList.size(); i++) {
                Bar barObject = barList.get(i);
                if (barObject.getName().equals(mTitle)){
                    Log.d(TAG + "DETAILS", "IN IF");
                    mLink = "https://trinkbar.azurewebsites.net/" + barObject.getImageLink();
                    Log.d(TAG + "IMAGE", mLink);

                    // Initialize a new RequestQueue instance
                    RequestQueue requestQueue = Volley.newRequestQueue(mCtx);

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, mLink, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG +"Response", response.toString());
                                    String base64String= null;
                                    try {
                                        base64String = response.getJSONObject("bars").getString("image");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String base64Image = base64String.split(",")[1];

                                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                    mImg.setImageBitmap(decodedByte);
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub

                                }
                            });

                    requestQueue.add(jsObjRequest);




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
        DetailsActivity.getInstance().addToRequestQueue(myCustomRequest,"tag");*/

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
