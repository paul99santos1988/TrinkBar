package hs_ab.com.TrinkBar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MapActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private static final String TAG = "LOG";
    private Context mCtx;

    private FloatingActionButton fab_bottom;
    private FloatingActionButton fab_location;
    private FloatingActionButton fab_target;
    private FloatingActionButton fab_share;

    private boolean FAB_Status = false;

    private View mAnchor;


    //Animations
    Animation show_fab_location;
    Animation hide_fab_location;
    Animation show_fab_target;
    Animation hide_fab_target;
    Animation show_fab_share;
    Animation hide_fab_share;


    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Animations
        show_fab_location = AnimationUtils.loadAnimation(getApplication(), R.anim.fablocation_show);
        hide_fab_location = AnimationUtils.loadAnimation(getApplication(), R.anim.fablocation_hide);
        show_fab_target = AnimationUtils.loadAnimation(getApplication(), R.anim.fabtarget_show);
        hide_fab_target = AnimationUtils.loadAnimation(getApplication(), R.anim.fabtarget_hide);
        show_fab_share = AnimationUtils.loadAnimation(getApplication(), R.anim.fabshare_show);
        hide_fab_share = AnimationUtils.loadAnimation(getApplication(), R.anim.fabshare_hide);

        // FAB
        fab_bottom = (FloatingActionButton) findViewById(R.id.fab_bottom);
        fab_location = (FloatingActionButton) findViewById(R.id.fab_location);
        fab_target = (FloatingActionButton) findViewById(R.id.fab_target);
        fab_share = (FloatingActionButton) findViewById(R.id.fab_share);


        fab_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });

        fab_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Aktuelle Position", Toast.LENGTH_SHORT).show();
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(mMap.getMyLocation().getLatitude(),
                                mMap.getMyLocation().getLongitude()));
                mMap.moveCamera(center);
                //Log.d(TAG, "onMapReady: "+ mMap.getMyLocation().getLatitude());

            }
        });

        fab_target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Aschaffenburg", Toast.LENGTH_SHORT).show();
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(49.969527, 9.150233));
                mMap.moveCamera(center);
            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Share Location", Toast.LENGTH_SHORT).show();
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /*@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.map_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }
        if (id == R.id.map_roadmap) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }
        if (id == R.id.map_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item_list clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent i = new Intent(MapActivity.this, MapActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // to resume on the existing map (and to create an new one)
            startActivity(i);

        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(MapActivity.this, ListActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_acc) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();



        LatLngBounds ASCHAFFENBURG = new LatLngBounds(
                new LatLng(49.969527, 9.150233), new LatLng(49.980977, 9.150233));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ASCHAFFENBURG.getCenter(), 15));
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //----HTTP
        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(mCtx);
        String url = "http://sightseeing-fhws.azurewebsites.net";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        setMarker(response);

                        // Save Data from Server in local File +++not used+++
                        //create new File
                        try {
                            FileWriter file = new FileWriter(mCtx.getFilesDir().getPath() + "/data.json");
                            file.flush();
                            file.close();

                            //write to existing file
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mCtx.openFileOutput("data.json", Context.MODE_APPEND));
                            outputStreamWriter.write(response.toString());
                            outputStreamWriter.close();

                            File f = new File(mCtx.getFilesDir().getPath() + "/data.json");
                            //check whether file exists
                            FileInputStream is = new FileInputStream(f);
                            int size1 = is.available();
                            byte[] buffer1 = new byte[size1];
                            is.read(buffer1);
                            is.close();
                            String out = new String(buffer1);
                            Log.d(TAG, "STRING FROM FILE" + out);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
            }
        });

        queue.add(stringRequest);


        // Open Details Activity on marker click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Log.d(TAG, arg0.getTitle());
                //Intent i = new Intent(MapActivity.this, ListActivity.class);
                Intent i = new Intent(MapActivity.this, DetailsActivity.class);
                i.putExtra("EXTRA_DETAILS_TITLE", arg0.getTitle());
                startActivity(i);
                return false;
            }

        });

    }

    // set marker on the map with the coordinates from the server
    private void setMarker(String data) {
        try {
            JSONObject obj = new JSONObject(data.toString());
            JSONArray places = obj.getJSONArray("placesOfInterest");
            Log.d(TAG, places.toString());

            for (int i = 0; i < places.length(); i++) {
                Double lat = places.getJSONObject(i).getJSONArray("coordinates").getJSONObject(0).getDouble("latitude ");
                Log.d(TAG, lat.toString());
                Double lon = places.getJSONObject(i).getJSONArray("coordinates").getJSONObject(1).getDouble("longitude");
                String name = places.getJSONObject(i).getString("name");
                LatLng place = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(place).title(name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void expandFAB() {

        //Floating Action Location
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab_location.getLayoutParams();
        layoutParams.rightMargin += (int) (fab_location.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab_location.getHeight() * 0.25);
        fab_location.setLayoutParams(layoutParams);
        fab_location.startAnimation(show_fab_location);
        fab_location.setClickable(true);

        //Floating Action Target
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab_target.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab_target.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab_target.getHeight() * 1.5);
        fab_target.setLayoutParams(layoutParams2);
        fab_target.startAnimation(show_fab_target);
        fab_target.setClickable(true);

        //Floating Action Share
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab_share.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab_share.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab_share.getHeight() * 1.7);
        fab_share.setLayoutParams(layoutParams3);
        fab_share.startAnimation(show_fab_share);
        fab_share.setClickable(true);
    }


    private void hideFAB() {

        //Floating Action Location
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab_location.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab_location.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab_location.getHeight() * 0.25);
        fab_location.setLayoutParams(layoutParams);
        fab_location.startAnimation(hide_fab_location);
        fab_location.setClickable(false);

        //Floating Action Target
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab_target.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab_target.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab_target.getHeight() * 1.5);
        fab_target.setLayoutParams(layoutParams2);
        fab_target.startAnimation(hide_fab_target);
        fab_target.setClickable(false);

        //Floating Action Share
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab_share.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab_share.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab_share.getHeight() * 1.7);
        fab_share.setLayoutParams(layoutParams3);
        fab_share.startAnimation(hide_fab_share);
        fab_share.setClickable(false);
    }
}
