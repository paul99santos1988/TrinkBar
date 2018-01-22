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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


import java.io.UnsupportedEncodingException;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

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
