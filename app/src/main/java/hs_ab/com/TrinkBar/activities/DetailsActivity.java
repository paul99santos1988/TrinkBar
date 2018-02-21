package hs_ab.com.TrinkBar.activities;

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

import java.io.UnsupportedEncodingException;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

public class DetailsActivity extends AppCompatActivity {

    private static DetailsActivity mInstance;


    private Context mCtx;
    private static final String TAG = "LOG";
    private String mTitle;
    private ImageView mImg;
    private String mBarId;
    private TextView mText;
    private String mOpeninghours;
    private Bar mBarObject;
    private RealtimeDBAdapter mRtDatabase;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mInstance=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mCtx = getApplicationContext();

        mBarId = getIntent().getStringExtra("EXTRA_DETAILS_TITLE");



        mRtDatabase = RealtimeDBAdapter.getInstance(mCtx);

        mBarObject = mRtDatabase.getBarbyId(mBarId);
        mTitle = mBarObject.getName();
        setTitle(mTitle);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mImg = findViewById(R.id.imageview_details);
        mText = findViewById(R.id.textview_details);


        // FAB fromDetails Activity
        mFab = findViewById(R.id.fab_bottom);
        mFab.setOnClickListener(new View.OnClickListener() {
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


        Image image= mRtDatabase.getImagebyId(mBarId);

       //mBarObject.setImageData(image.getImage());

        String description = mBarObject.getDescription();

        //Öffnungszeiten
        mOpeninghours = mBarObject.getOpeningHours().getMonday();
        mOpeninghours = mOpeninghours + mBarObject.getOpeningHours().getSunday();

        byte[] descriptionByte = Base64.decode(description, Base64.DEFAULT);
        try {
            String decodedDescription = new String(descriptionByte, "UTF-8");
            mText.setText(decodedDescription);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64String = image.getImage();
        String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        mImg.setImageBitmap(decodedByte);

    }


    public static synchronized DetailsActivity getInstance()
    {
        return mInstance;
    }
}
