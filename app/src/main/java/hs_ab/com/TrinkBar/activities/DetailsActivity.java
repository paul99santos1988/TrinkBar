package hs_ab.com.TrinkBar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

public class DetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private Context mCtx;
    private static final String TAG = "DetailsActivity";
    private String mTitle;
    private ImageView mImg;
    private String mBarId;
    private TextView mDetailsContent,mOpenSun,mOpenMon,mOpenTue,mOpenWens,mOpenThur,mOpenFri,mOpenSat,mFood,mAddress, mRating;
    private Bar mBarObject;
    private RealtimeDBAdapter mRtDatabase;
    private FloatingActionButton mFab;
    private RequestQueue queue;
    private boolean mIsFav = false;
    private Button mPhoneButton;
    private MenuItem mfavItem;
    private SharedPreferences sharedPrefFavorites;
    private SharedPreferences.Editor editor;
    private GeoDataClient mGeoDataClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mCtx = getApplicationContext();
        sharedPrefFavorites = mCtx.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        editor = sharedPrefFavorites.edit();
        mGeoDataClient = Places.getGeoDataClient(this, null);


        getDetailsData();

        initBarRating();
        initFAB();

        initToolBar();
        initViews();

    }


    @Override
    protected void onResume() {
        super.onResume();

        setViews();


        /*queue = Volley.newRequestQueue(this);

                         String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query="+mBarObject.getName()+"+Aschaffenburg&key="+Constants.PLACES_API_KEY;

                         JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                 (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                     @Override
                                     public void onResponse(JSONObject response) {
                                         Log.d("Response: " , response.toString());
                                         try {
                                             JSONArray jsonArray = new JSONArray(response.get("results").toString());
                                             JSONObject object = jsonArray.getJSONObject(0);
                                             String place = object.get("place_id").toString();


                                             String url2 = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+place+"&key="+ Constants.PLACES_API_KEY;

                                             JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                                     (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {

                                                         @Override
                                                         public void onResponse(JSONObject response) {
                                                             Log.d("Response: " , response.toString());
                                                         }
                                                     }, new Response.ErrorListener() {

                                                         @Override
                                                         public void onErrorResponse(VolleyError error) {
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
                                         Log.d(TAG, "onErrorResponse: ");

                                     }
                                 });

                         // Access the RequestQueue through your singleton class.
                         queue.add(jsObjRequest);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        mfavItem = menu.findItem(R.id.action_favorite);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //init of the bar rating from Google
    private void initBarRating(){

        queue = Volley.newRequestQueue(this);
        String encodedBarname; //to avoid spaces of the bar names inside URL
        try {
            encodedBarname = URLEncoder.encode(mBarObject.getName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encodedBarname = mBarObject.getName();
            e.printStackTrace();
        }

        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + encodedBarname + "+Aschaffenburg&key=" + Constants.PLACES_API_KEY;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.get("results").toString());
                            if (jsonArray.isNull(0) == true){
                                mRating = (TextView) findViewById(R.id.textview_details_rating);
                                mRating.setText(getString(R.string.no_rating_possible));//You have exceeded your daily request quota for the Google API

                            } else {
                                JSONObject object = jsonArray.getJSONObject(0);
                                String place = object.get("place_id").toString();
                                //get Place Details
                                mGeoDataClient.getPlaceById(place).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                                        if (task.isSuccessful()) {
                                            PlaceBufferResponse places = task.getResult();
                                            Place myPlace = places.get(0);
                                            mBarObject.setRating(Float.toString(myPlace.getRating()));
                                            Log.i(TAG, "rating: " + myPlace.getRating());
                                            //update as soon as possible
                                            mRating = (TextView) findViewById(R.id.textview_details_rating);

                                            if (mBarObject.getRating() == null) {
                                                mRating.setText(getString(R.string.no_rating_available));
                                            } else {
                                                mRating.setText(mBarObject.getRating());
                                            }

                                            places.release();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Bar-Bewertungen nicht verfügbar",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: ");

                    }
                });

        // Access the RequestQueue.
        queue.add(jsObjRequest);

    }

    private void getDetailsData(){
        mBarId = getIntent().getStringExtra("EXTRA_DETAILS_TITLE");
        mRtDatabase = RealtimeDBAdapter.getInstance(mCtx);
        mBarObject = mRtDatabase.getBarbyId(mBarId);
        mTitle = mBarObject.getName();
    }



    private void initToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    private void initFAB(){
        mFab = (FloatingActionButton) findViewById(R.id.fab_bottom);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String favorite_key = mBarObject.getName();
            String savedBarId = sharedPrefFavorites.getString(favorite_key, getString(R.string.default_favorites_value));
            if(savedBarId.equals(mBarId)){
                mIsFav = true;
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp, getApplicationContext().getTheme()));
            }
            else {
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp, getApplicationContext().getTheme()));
            }
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFavItem();
            }
        });
    }

    private void initViews(){
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
        mImg = (ImageView) findViewById(R.id.imageview_details);
        mRating = (TextView) findViewById(R.id.textview_details_rating);

        /*mRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRating.setText(mBarObject.getRating());//update
            }
        });*/

        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent();
                    callIntent.setAction(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + mBarObject.getPhone()));
                    startActivity(callIntent);
                } catch (Exception e) {
                    Snackbar.make(v, R.string.noDial, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
            }
        });
    }

    private void setViews(){
        mOpenSun.setText(mBarObject.getOpeningHours().getSunday());
        mOpenMon.setText(mBarObject.getOpeningHours().getMonday());
        mOpenTue.setText(mBarObject.getOpeningHours().getTuesday());
        mOpenWens.setText(mBarObject.getOpeningHours().getWednesday());
        mOpenThur.setText(mBarObject.getOpeningHours().getThursday());
        mOpenFri.setText(mBarObject.getOpeningHours().getFriday());
        mOpenSat.setText(mBarObject.getOpeningHours().getSaturday());
        mAddress.setText(mBarObject.getAddress());
        mPhoneButton.setText(mBarObject.getPhone());

        if(mBarObject.getRating() == null){
            mRating.setText(getString(R.string.no_rating_available));
        }
        else {
            mRating.setText(mBarObject.getRating());
        }
        Image image= mRtDatabase.getImagebyId(mBarId);
        String description = mBarObject.getDescription();
        String decodedString = base64ToUTF8(description);

        if(decodedString != null) {
            mDetailsContent.setText(decodedString);
        }
        mImg.setImageBitmap(decodeImg(image.getImage()));

        if(mBarObject.getFood().equals("true")){
            mFood.setText(R.string.food);
        }
        else{
            mFood.setText(R.string.noFood);
        }

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            if(mfavItem != null) {
                mfavItem.setVisible(false);
            }
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
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
            if(mfavItem != null) {
                mfavItem.setVisible(false);
            }
        }
    }

    private void changeFavItem(){

        String savedBarId = sharedPrefFavorites.getString(mBarObject.getName(), getString(R.string.default_favorites_value));
        mIsFav = !savedBarId.equals("NO FAVORITES SAVED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!mIsFav) {
                mIsFav = true;
                addFavItem();
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp, getApplicationContext().getTheme()));
            } else {
                mIsFav = false;
                removeFavItem();
                mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp, getApplicationContext().getTheme()));
            }
        }
    }

    private void addFavItem(){

        Map savedFavorites = sharedPrefFavorites.getAll();
        Integer favoritesNumber = savedFavorites.size();
        String savedBarId;
        String favorite_key = mBarObject.getName();
        String mBarId = mBarObject.getId();
        if(favoritesNumber == 0){
            editor.putString(favorite_key, mBarObject.getId());
        }
        else {
            for (int i = 0; i <= favoritesNumber; i++) {
                savedBarId = sharedPrefFavorites.getString(favorite_key, getString(R.string.default_favorites_value));
                if (savedBarId.equals(mBarId)) {
                    break; //favorite already saved
                } else if ((savedBarId != mBarObject.getId()) & (i == favoritesNumber)) {
                    editor.putString(favorite_key, mBarObject.getId());
                    break;
                }
            }
        }
        //editor.clear(); //delete all favorites
        editor.commit();
        Toast.makeText(mCtx, "Bar als Favorit markiert", Toast.LENGTH_SHORT).show();

    }

    private void removeFavItem(){

        Map savedFavorites = sharedPrefFavorites.getAll();
        Integer favoritesNumber = savedFavorites.size();
        String savedBarId;
        String favorite_key = mBarObject.getName();
        String mBarId = mBarObject.getId();
        if(favoritesNumber != 0) {
            //if favorite exists it will be removed
            savedBarId = sharedPrefFavorites.getString(favorite_key, getString(R.string.default_favorites_value));
            if (savedBarId.equals(mBarId)) {
                editor.remove(favorite_key); //favorite will be removed
                editor.commit();
                Toast.makeText(mCtx, "Bar aus Favoritenliste entfernt", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(mCtx, "Favorit existiert nicht", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(mCtx, "Keine Favoriten vorhanden", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap decodeImg(String data){
        String base64Image = data.split(",")[1];
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private String base64ToUTF8(String data){
        byte[] descriptionByte = Base64.decode(data, Base64.DEFAULT);
        String decodedString;
        try {
            decodedString = new String(descriptionByte, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return decodedString;
    }
}
