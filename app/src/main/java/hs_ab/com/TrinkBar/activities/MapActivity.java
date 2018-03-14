package hs_ab.com.TrinkBar.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.ui.IconGenerator;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.helper.PermissionUtils;
import hs_ab.com.TrinkBar.models.Bar;


public class MapActivity extends AppCompatActivity
        implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnPoiClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    private static final String TAG = "MapActivity";
    private Context mCtx;
    private List<Bar> mBarList;
    private LatLng mlocation = null;
    private RealtimeDBAdapter mRtDatabase;
    private FloatingActionButton mFabBottom;
    private FloatingActionButton mFabLocation;
    private FloatingActionButton mFabTarget;
    private FloatingActionButton mFabShare;
    private FusedLocationProviderClient mFusedLocationClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private GeoDataClient mGeoDataClient;
    private boolean mFabStatus = false;
    //Animations
    private Animation show_fab_location;
    private Animation hide_fab_location;
    private Animation show_fab_target;
    private Animation hide_fab_target;
    private Animation show_fab_share;
    private Animation hide_fab_share;

    private DatabaseReference mDatabase;
    private ArrayList<Marker> mMarkerArray;
    private String mPlacesAPIKey = "AIzaSyC2144RCdtuiUP2HF-lMNg3Q9raPDmQy2M";
    private GeofencingClient mGeofencingClient;


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
    private ArrayList<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getApplicationContext();
        setContentView(R.layout.activity_main);

        initAnimations();
        initFAB();
        initSideMenu();
        setupRealtimeDB();
        initMap();

        mMarkerArray = new ArrayList<>();
        mGeofenceList = new ArrayList<>();

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        mGeofencingClient = LocationServices.getGeofencingClient(this);


        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("ab")

                .setCircularRegion(
                        49.9749658,
                        9.1534121,
                        10
                )
                .setExpirationDuration(10)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: GEO");
                        // Geofences added
                        // ...
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        Log.d(TAG, "onFailure: GEO");
                        // ...
                    }
                });




    }



    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.

        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsJobIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }



    private void initSideMenu() {

        //mark selected menu item
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void initMap() {
        // Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void initFAB() {
        mFabBottom = findViewById(R.id.fab_bottom);
        mFabLocation = findViewById(R.id.fab_location);
        mFabTarget = findViewById(R.id.fab_target);
        mFabShare = findViewById(R.id.fab_share);


        mFabBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFabStatus == false) {
                    //Display FAB menu
                    expandFAB();
                    mFabStatus = true;
                } else {
                    //Close FAB menu
                    hideFAB();
                    mFabStatus = false;
                }
            }
        });

        mFabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MapActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Toast.makeText(getApplication(), "Aktuelle Position", Toast.LENGTH_SHORT).show();
                                    mlocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    CameraUpdate center = CameraUpdateFactory.newLatLng(mlocation);
                                    mMap.moveCamera(center);
                                } else {
                                    Toast.makeText(getApplication(), "Bitte Standort aktivieren", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });


        mFabTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Aschaffenburg", Toast.LENGTH_SHORT).show();
                CameraUpdate center =
                        CameraUpdateFactory.newLatLng(new LatLng(49.969527, 9.150233));
                mMap.moveCamera(center);
            }
        });

        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "Share Location", Toast.LENGTH_SHORT).show();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hallo Du, ich befinde mich zur Zeit bei xx komm doch vorbei!");
                sendIntent.setType("text/plain");
                //sendIntent.setPackage("com.whatsapp");
                Intent chooser = Intent.createChooser(sendIntent, "Share");
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    //startActivity(sendIntent);
                    startActivity(chooser);
                }
            }
        });
    }

    private void initAnimations() {
        //Animations
        show_fab_location = AnimationUtils.loadAnimation(getApplication(), R.anim.fablocation_show);
        hide_fab_location = AnimationUtils.loadAnimation(getApplication(), R.anim.fablocation_hide);
        show_fab_target = AnimationUtils.loadAnimation(getApplication(), R.anim.fabtarget_show);
        hide_fab_target = AnimationUtils.loadAnimation(getApplication(), R.anim.fabtarget_hide);
        show_fab_share = AnimationUtils.loadAnimation(getApplication(), R.anim.fabshare_show);
        hide_fab_share = AnimationUtils.loadAnimation(getApplication(), R.anim.fabshare_hide);
    }


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

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Map
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnPoiClickListener(this);
        enableMyLocation();

        LatLngBounds ASCHAFFENBURG = new LatLngBounds(
                new LatLng(49.969527, 9.150233), new LatLng(49.980977, 9.150233));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ASCHAFFENBURG.getCenter(), 15));
        mMap.getUiSettings().setMapToolbarEnabled(false);


        // Open Details Activity on marker click
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Log.d(TAG, arg0.getTitle());
                String name = arg0.getTitle();
                for (int i = 0; i < mBarList.size(); i++) {
                    String barname = mBarList.get(i).getName();
                    if (barname.equals(name)) {
                        Intent intent = new Intent(MapActivity.this, DetailsActivity.class);
                        intent.putExtra("EXTRA_DETAILS_TITLE", mBarList.get(i).getId());
                        startActivity(intent);

                    }

                }
                return false;
            }

        });

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                //Here your code
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub

            }
        });

    }

    // set marker on the map with the coordinates from the server
    private void setMarker() {
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setColor(Color.WHITE);
        iconFactory.setTextAppearance(R.style.iconGenText);


        for(int j = 0; j < mMarkerArray.size(); j++) {
                    mMarkerArray.get(j).remove();
                    mMarkerArray.remove(j);
        }
        for (int i = 0; i < mBarList.size(); i++) {
            Double lat = Double.valueOf(mBarList.get(i).getCoordinates().getLatitude());
            Double lon = Double.valueOf(mBarList.get(i).getCoordinates().getLongitude());
            String name = mBarList.get(i).getName();
            LatLng place = new LatLng(lat, lon);
            List<Address> addresses=new ArrayList<>();
            //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&types=food&name=cruise&key=YOUR_API_KEY
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            try {
                addresses = geo.getFromLocation(lat, lon, 1);
                Log.d(TAG, "setMarker: ");
            } catch (IOException e) {
                e.printStackTrace();
            }

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mBarList.get(i).getVisitor()))) // + "\n"
                    .position(place)
                    .title(name));
           // marker.setDraggable(true);
            mMarkerArray.add(marker);

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
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFabLocation.getLayoutParams();
        layoutParams.rightMargin += (int) (mFabLocation.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (mFabLocation.getHeight() * 0.25);
        mFabLocation.setLayoutParams(layoutParams);
        mFabLocation.startAnimation(show_fab_location);
        mFabLocation.setClickable(true);

        //Floating Action Target
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) mFabTarget.getLayoutParams();
        layoutParams2.rightMargin += (int) (mFabTarget.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (mFabTarget.getHeight() * 1.5);
        mFabTarget.setLayoutParams(layoutParams2);
        mFabTarget.startAnimation(show_fab_target);
        mFabTarget.setClickable(true);

        //Floating Action Share
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) mFabShare.getLayoutParams();
        layoutParams3.rightMargin += (int) (mFabShare.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (mFabShare.getHeight() * 1.7);
        mFabShare.setLayoutParams(layoutParams3);
        mFabShare.startAnimation(show_fab_share);
        mFabShare.setClickable(true);
    }


    private void hideFAB() {

        //Floating Action Location
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mFabLocation.getLayoutParams();
        layoutParams.rightMargin -= (int) (mFabLocation.getWidth() * 1.6);
        layoutParams.bottomMargin -= (int) (mFabLocation.getHeight() * 0.24);
        mFabLocation.setLayoutParams(layoutParams);
        mFabLocation.startAnimation(hide_fab_location);
        mFabLocation.setClickable(false);

        //Floating Action Target
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) mFabTarget.getLayoutParams();
        layoutParams2.rightMargin -= (int) (mFabTarget.getWidth() * 1.4);
        layoutParams2.bottomMargin -= (int) (mFabTarget.getHeight() * 1.4);
        mFabTarget.setLayoutParams(layoutParams2);
        mFabTarget.startAnimation(hide_fab_target);
        mFabTarget.setClickable(false);

        //Floating Action Share
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) mFabShare.getLayoutParams();
        layoutParams3.rightMargin -= (int) (mFabShare.getWidth() * 0.24);
        layoutParams3.bottomMargin -= (int) (mFabShare.getHeight() * 1.6);
        mFabShare.setLayoutParams(layoutParams3);
        mFabShare.startAnimation(hide_fab_share);
        mFabShare.setClickable(false);
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {

        //get Place Details
        mGeoDataClient.getPlaceById(poi.placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                 @Override
                 public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                     if (task.isSuccessful()) {
                         PlaceBufferResponse places = task.getResult();
                         Place myPlace = places.get(0);
                         //Place.TYPE_SHOPPING_MALL
                         Toast.makeText(getApplicationContext(), "Clicked: " +
                                         myPlace.getName() + "\nRating:" + myPlace.getRating() +
                                         "\nAddress:" + myPlace.getAddress() +  myPlace.getPlaceTypes().toString() ,
                                 Toast.LENGTH_LONG).show();

                         /*RequestQueue queue = Volley.newRequestQueue(MapActivity.this);

                         String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+ myPlace.getId()+"&key="+ mPlacesAPIKey;

                         JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                 (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                     @Override
                                     public void onResponse(JSONObject response) {
                                         Log.d("Response: " , response.toString());
                                     }
                                 }, new Response.ErrorListener() {

                                     @Override
                                     public void onErrorResponse(VolleyError error) {
                                         // TODO Auto-generated method stub

                                     }
                                 });

                         // Access the RequestQueue through your singleton class.
                         queue.add(jsObjRequest);*/



                         places.release();
                     } else {
                         Toast.makeText(getApplicationContext(), "Place not found.",
                                 Toast.LENGTH_SHORT).show();
                     }





                 }
             });

    }

    public void setupRealtimeDB(){

        mRtDatabase = RealtimeDBAdapter.getInstance(mCtx);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                mRtDatabase.DataSnapshotHandler(dataSnapshot);
                mBarList = mRtDatabase.getBarList();
                if (mMap != null) {
                    setMarker();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                mRtDatabase.DataSnapshotHandler(dataSnapshot);
                mBarList = mRtDatabase.getBarList();
                if (mMap != null) {
                    setMarker();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                mRtDatabase.DataSnapshotHandler(dataSnapshot);
                mBarList = mRtDatabase.getBarList();
                if (mMap != null) {
                    setMarker();
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                //Toast.makeText(mContext, "Failed to load comments.",
                //       Toast.LENGTH_SHORT).show();
            }
        };
        mDatabase.addChildEventListener(childEventListener);

    }
}
