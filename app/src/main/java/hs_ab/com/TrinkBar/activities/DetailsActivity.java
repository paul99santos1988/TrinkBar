package hs_ab.com.TrinkBar.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

public class DetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private static DetailsActivity mInstance;


    private Context mCtx;
    private static final String TAG = "LOG";
    private String mTitle;
    private ImageView mImg;
    private String mBarId;
    private TextView mDetailsContent,mOpenSun,mOpenMon,mOpenTue,mOpenWens,mOpenThur,mOpenFri,mOpenSat,mFood,mAddress,mPhone;
    private String mOpeninghours;
    private Bar mBarObject;
    private RealtimeDBAdapter mRtDatabase;
    private FloatingActionButton mFab;
    private String mPlacesAPIKey = "AIzaSyC2144RCdtuiUP2HF-lMNg3Q9raPDmQy2M";
    private RequestQueue queue;
    private boolean mIsFav = false;
    private Button mPhoneButton;
    private Menu menu;
    private MenuItem mfavItem;



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


        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        mImg = (ImageView) findViewById(R.id.imageview_details);
        initTextViews();

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);




        // FAB fromDetails Activity
        mFab = (FloatingActionButton) findViewById(R.id.fab_bottom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp, getApplicationContext().getTheme()));
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFavItem();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        mfavItem = menu.findItem(R.id.action_favorite);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            changeFavItem();
            //Toast.makeText(DetailsActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onResume() {
        super.onResume();


        Image image= mRtDatabase.getImagebyId(mBarId);


        String description = mBarObject.getDescription();

        setTextViews();


        byte[] descriptionByte = Base64.decode(description, Base64.DEFAULT);
        try {
            String decodedDescription = new String(descriptionByte, "UTF-8");
            mDetailsContent.setText(decodedDescription);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String base64String = image.getImage();
        String base64Image = base64String.split(",")[1];

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        mImg.setImageBitmap(decodedByte);

        /*queue = Volley.newRequestQueue(this);

                         String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+mBarObject.getName()+"+Aschaffenburg&key="+mPlacesAPIKey;

                         JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                 (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                     @Override
                                     public void onResponse(JSONObject response) {
                                         Log.d("Response: " , response.toString());
                                         try {
                                             JSONArray jsonArray = new JSONArray(response.get("results").toString());
                                             JSONObject object = jsonArray.getJSONObject(0);
                                             String place = object.get("place_id").toString();


                                             String url2 = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+place+"&key="+ mPlacesAPIKey;

                                             JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                                     (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                                                         @Override
                                                         public void onResponse(JSONObject response) {
                                                             Log.d("Response: " , response.toString());
                                                         }
                                                     }, new Response.ErrorListener() {

                                                         @Override
                                                         public void onErrorResponse(VolleyError error) {
                                                             // TODO Auto-generated method stub
                                                             Log.d(TAG, "onErrorResponse: ");

                                                         }
                                                     });
                                             queue.add(jsObjRequest);

                                         } catch (JSONException e) {
                                             e.printStackTrace();
                                         }
                                     }
                                 }, new Response.ErrorListener() {

                                     @Override
                                     public void onErrorResponse(VolleyError error) {
                                         // TODO Auto-generated method stub
                                         Log.d(TAG, "onErrorResponse: ");

                                     }
                                 });

                         // Access the RequestQueue through your singleton class.
                         queue.add(jsObjRequest);*/

    }


    public static synchronized DetailsActivity getInstance()
    {
        return mInstance;
    }

    private void initTextViews(){
        mDetailsContent = (TextView) findViewById(R.id.textview_details_content_description);
        mOpenSun = (TextView) findViewById(R.id.details_table_content_Sunday);
        mOpenMon = (TextView) findViewById(R.id.details_table_content_Monday);
        mOpenTue = (TextView) findViewById(R.id.details_table_content_Tuesday);
        mOpenWens = (TextView) findViewById(R.id.details_table_content_Wednesday);
        mOpenThur = (TextView) findViewById(R.id.details_table_content_Thursday);
        mOpenFri = (TextView) findViewById(R.id.details_table_content_Friday);
        mOpenSat = (TextView) findViewById(R.id.details_table_content_Saturday);
        mFood= (TextView) findViewById(R.id.textView_details_food);
        mAddress= (TextView) findViewById(R.id.textView_details_address);
        mPhoneButton = (Button) findViewById(R.id.button_details_phone);


        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent();
                    callIntent.setAction(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + mBarObject.getPhone()));
                    startActivity(callIntent);
                } catch (Exception e) {
                    Snackbar.make(v, "Keine Telefonapp installiert", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
            }
        });
    }

    private void setTextViews(){
        mOpenSun.setText(mBarObject.getOpeningHours().getSunday());
        mOpenMon.setText(mBarObject.getOpeningHours().getMonday());
        mOpenTue.setText(mBarObject.getOpeningHours().getTuesday());
        mOpenWens.setText(mBarObject.getOpeningHours().getWednesday());
        mOpenThur.setText(mBarObject.getOpeningHours().getThursday());
        mOpenFri.setText(mBarObject.getOpeningHours().getFriday());
        mOpenSat.setText(mBarObject.getOpeningHours().getSaturday());
        mAddress.setText(mBarObject.getAddress());
        mPhoneButton.setText(mBarObject.getPhone());

        if(mBarObject.getFood().equals("true")){
            mFood.setText("Essen verfügbar");
        }
        else{
            mFood.setText("Lieder kein Essen verfügbar");
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            Log.d(TAG, "if"+ verticalOffset);
            if(mfavItem != null) {
                mfavItem.setVisible(false);
            }

        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            Log.d(TAG, " else if"+ verticalOffset);
            if(mfavItem != null) {
                mfavItem.setVisible(true);
                if(mIsFav){
                    mfavItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black_24dp));
                }
                else{
                    mfavItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black_24dp));
                }
            }


        } else {
            Log.d(TAG, "else" + verticalOffset);
            if(mfavItem != null) {
                mfavItem.setVisible(false);
            }


        }
    }

    private void changeFavItem(){
        mIsFav=!mIsFav;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mIsFav) {
                addFavItem();
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp, getApplicationContext().getTheme()));
            } else {
                removeFavItem();
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp, getApplicationContext().getTheme()));
            }
        }
    }

    private void addFavItem(){
        //TODO
    }

    private void removeFavItem(){

        //TODO
    }
}
