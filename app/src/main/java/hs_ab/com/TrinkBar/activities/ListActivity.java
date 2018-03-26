package hs_ab.com.TrinkBar.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
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
import android.view.Window;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.BarListAdapter;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Image;

public class ListActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener, DistanceCallback {


    private static ListActivity mInstance;


    private Context mCtx;
    private static final String TAG = "LOG";
    private List<Bar> barList;
    private RecyclerView mRv;
    private RealtimeDBAdapter mRtDatabase;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationDistance mDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        mCtx = getApplicationContext();
        setContentView(R.layout.activity_list);
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
        barList = new ArrayList<Bar>();
        mRtDatabase = RealtimeDBAdapter.getInstance(mCtx);

        // RV for List
        mRv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRv.setLayoutManager(llm);
        mRv.setHasFixedSize(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        mDistance= LocationDistance.getInstance(mCtx);
        mDistance.setCallbacks(this);
        mDistance.calculateDistance();


        //barList.clear();
        barList= mRtDatabase.getBarList();
        // init Adapter with Data from Server
        for (int i=0;i<barList.size();i++ ){

            Image image = mRtDatabase.getImagebyId(barList.get(i).getId());
            barList.get(i).setImageData(image.getImage());

        }
        if(mRv.getAdapter()==null) {
            initializeAdapter();
        }
        mRv.getAdapter().notifyDataSetChanged();


    }

    @Override
    protected void onStop() {
        super.onStop();
        mDistance.close();

    }



    private void initializeAdapter() {
        Log.d(TAG, "initializeAdapter");
        BarListAdapter adapter = new BarListAdapter(this, barList);
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
            Intent i = new Intent(ListActivity.this, MapActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // to resume on the existing map (and to create an new one)
            startActivity(i);

        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(ListActivity.this, ListActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_acc) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_details);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static synchronized ListActivity getInstance()
    {
        return mInstance;
    }

    @Override
    public void callbackCall(List<Bar> barList) {
        this.barList=barList;
        mRv.getAdapter().notifyDataSetChanged();
    }
}
