package hs_ab.com.TrinkBar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.FavoritesListAdapter;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

/**
 * Created by tabo on 3/21/18.
 */

public class FavoritesActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener, DistanceCallback {

        private static FavoritesActivity mInstance;


        private Context mCtx;
        private static final String TAG = "FavoritesActivity";
        private List<Bar> barList;
        private RecyclerView mRv;
        private RealtimeDBAdapter mRtDatabase;
        public List<Bar> barFavoritesList;
        FavoritesListAdapter adapter;
        private LocationDistance mDistance;
        private SharedPreferences sharedPrefFavorites;
        private Map sharedPrefTempFav;
        private Map savedFavorites;
        private SharedPreferences.Editor editor;
        private String[] favorites;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mInstance=this;
            mCtx = getApplicationContext();
            setContentView(R.layout.activity_favorites_list);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Side menu
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_details);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_details);
            navigationView.setNavigationItemSelectedListener(this);

            sharedPrefFavorites = mCtx.getSharedPreferences(getString(R.string.preference_file_key), mCtx.MODE_PRIVATE);
            savedFavorites = sharedPrefFavorites.getAll();


            //Collection favoritesCollection = savedFavorites.values();
            //favorites = new String[savedFavorites.size()];



            //SharedPreferences.Editor edtior = sharedPref.edit();
            barList = new ArrayList<Bar>();
            mRtDatabase = RealtimeDBAdapter.getInstance(mCtx);


            if(barFavoritesList == null){
                barFavoritesList = new ArrayList<Bar>();
            }

            // RV for List
            mRv = (RecyclerView) findViewById(R.id.rv_favorites);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            mRv.setLayoutManager(llm);
            mRv.setHasFixedSize(true);

        }


    @Override
    protected void onStop() {
        super.onStop();
        mDistance.close();

    }

        @Override
        protected void onResume() {

            super.onResume();

            mDistance = LocationDistance.getInstance(mCtx);
            mDistance.setCallbacks(this);
            mDistance.calculateDistance();

            //update of current favorites list
            savedFavorites = sharedPrefFavorites.getAll();
            //TODO change AND to OR -> list will be displayed twice
            //init of sharedPrefTemFav to avoid breakdown because of <null> at next condition
            if(sharedPrefTempFav == null){
                sharedPrefTempFav = sharedPrefFavorites.getAll();
                sharedPrefTempFav.clear();
            }
            //condition: -only after a change of favorites list size,
            //- then distinguish if favList is filled or empty
            //if (((savedFavorites.size() != sharedPrefTempFav.size()) & (savedFavorites.size() != 0)) | sharedPrefTempFav == null)
            //if (savedFavorites.size() != sharedPrefTempFav.size()){
                //if(((mRv.getAdapter()==null & savedFavorites.size()!=0)|(sharedPrefTempFav.size() != savedFavorites.size())))
                if (((savedFavorites.size() != 0) & savedFavorites.size() != sharedPrefTempFav.size())) {
                    //barList.clear();
                    barFavoritesList.clear();
                    barList = mRtDatabase.getBarList();
                    for (int i = 0; i < barList.size(); i++) {

                        Bar barToCompare = barList.get(i);
                        String key_favorite_bar = barToCompare.getName();
                        String savedBarId = sharedPrefFavorites.getString(key_favorite_bar, getString(R.string.default_favorites_value));
                        Log.i(TAG, "sharedPref -> barId= " + savedBarId);
                        if (barToCompare.getId().equals(savedBarId)) {
                            barFavoritesList.add(barToCompare);
                            Image image = mRtDatabase.getImagebyId(barList.get(i).getId());
                            for (int j = 0; j < barFavoritesList.size(); j++) {
                                //for loop needed -> barFavorites.size() != barList.size()
                                if (barToCompare.getId().equals(barFavoritesList.get(j).getId())) {
                                    barFavoritesList.get(j).setImageData(image.getImage()); //adding image to corresponding bar in favorites list
                                }
                            }
                        }

                    }
                    initializeAdapter();


                } else if (savedFavorites.size() == 0) {
                    barFavoritesList.clear();
                    Bar dummyBar = new Bar();
                    dummyBar.setId(getString(R.string.dummy_id_favorites));
                    dummyBar.setAddress("Musterstrasse");
                    dummyBar.setDistance(getString(R.string.dummy_distance_favorites));
                    dummyBar.setDescription("Only a dummy to show you a possible favorite");
                    dummyBar.setName(getString(R.string.dummy_text_no_favorites));
                    dummyBar.setImageData("This is the imageData, dummyText instead of an image");
                    barFavoritesList.add(dummyBar);
                    initializeAdapter();
                }
            //}
            sharedPrefTempFav = sharedPrefFavorites.getAll(); //temporary value to handle back-button (from DetailsActivity to FavoritesActivity)
            mRv.getAdapter().notifyDataSetChanged();
        }


    private void initializeAdapter() {
        Log.i(TAG, "initializeFavAdapter");
        adapter = new FavoritesListAdapter(this, barFavoritesList);
        mRv.setAdapter(adapter);
    }

    // Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_list clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }



    // Side menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item_list clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent i = new Intent(FavoritesActivity.this, MapActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // to resume on the existing map (and to create an new one)
            startActivity(i);

        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(FavoritesActivity.this, ListActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_favorites) {
            Intent i = new Intent(FavoritesActivity.this, FavoritesActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_acc) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_details);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static synchronized FavoritesActivity getInstance()
    {
        return mInstance;
    }

    @Override
    public void callbackCall(List<Bar> barList) {
        Log.d(TAG, "callbackCall: "+barList);
    }
}
