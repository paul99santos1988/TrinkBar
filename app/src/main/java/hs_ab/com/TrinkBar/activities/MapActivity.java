package hs_ab.com.TrinkBar.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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


import java.util.ArrayList;
import java.util.List;


import hs_ab.com.TrinkBar.R;
import hs_ab.com.TrinkBar.adapters.RealtimeDBAdapter;
import hs_ab.com.TrinkBar.models.Bar;
import io.github.yavski.fabspeeddial.FabSpeedDial;



public class MapActivity extends AppCompatActivity
        implements
        GoogleMap.OnPoiClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener{

    private static final String TAG = "MapActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String NOTIFICATION_MSG = "NOTIFICATION";
    private static final long GEO_DURATION = 60 * 60 * 1000;
    private static final String GEOFENCE_REQ_ID = "My Geofence";
    private static final float GEOFENCE_RADIUS = 50.0f; // in meters
    private final int GEOFENCE_REQ_CODE = 0;
    private GoogleMap mMap;
    private Context mCtx;
    private List<Bar> mBarList;
    private LatLng mlocation = null;
    private RealtimeDBAdapter mRtDatabase;
    private FusedLocationProviderClient mFusedLocationClient;
    private GeoDataClient mGeoDataClient;
    private DatabaseReference mDatabase;
    private ArrayList<Marker> mMarkerArray;
    private GeofencingClient mGeofencingClient;
    private ArrayList<Circle> geoFenceLimits;
    private boolean mPermissionDenied = false;
    private PendingIntent geoFencePendingIntent;

    public static Intent makeNotificationIntent(Context context, String msg) {
        Intent intent = new Intent( context, MapActivity.class );
        intent.putExtra( NOTIFICATION_MSG, msg );
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getApplicationContext();
        setContentView(R.layout.activity_main);

        initFAB();
        initSideMenu();
        setupRealtimeDB();
        initMap();

        mMarkerArray = new ArrayList<>();
        geoFenceLimits =new ArrayList<>();
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mGeofencingClient = LocationServices.getGeofencingClient(this);

    }

    private void initSideMenu() {

        //mark selected menu item
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true; //false: don't show menu
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //Toast.makeText(MapActivity.this,""+menuItem.getTitle(),Toast.LENGTH_SHORT).show();
                switch ( menuItem.getItemId() ) {

                    case R.id.action_location: {
                        if (checkPermission()) {
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
                        else{
                            askPermission();
                        }
                        return true;
                    }
                    case R.id.action_share: {
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
                        return true;
                    }
                    case R.id.action_target: {
                        Toast.makeText(getApplication(), "Aschaffenburg", Toast.LENGTH_SHORT).show();
                        CameraUpdate center =
                                CameraUpdateFactory.newLatLng(new LatLng(49.969527, 9.150233));
                        mMap.moveCamera(center);
                        return true;
                    }
                }

                return true;
            }

            @Override
            public void onMenuClosed() {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ) {

            case R.id.map_hybrid: {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            }
            case R.id.map_roadmap: {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            }
            case R.id.map_terrain: {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            }
            case R.id.geofence: {
                restoreGeofences();
                return true;
            }
            case R.id.clear: {
                clearGeofence();
                return true;
            }
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
        mMap.setOnPoiClickListener(this);
        enableMyLocation();

        LatLngBounds ASCHAFFENBURG = new LatLngBounds(
                new LatLng(49.969527, 9.150233), new LatLng(49.980977, 9.150233));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ASCHAFFENBURG.getCenter(), 15));
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);


    }

    // set marker on the map with the coordinates from the server
    private void setMarker() {
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setColor(Color.WHITE);
        iconFactory.setTextAppearance(R.style.iconGenText);

        if (mMarkerArray!=null){
            mMarkerArray.clear();
        }

        for (int i = 0; i < mBarList.size(); i++) {
            Double lat = Double.valueOf(mBarList.get(i).getCoordinates().getLatitude());
            Double lon = Double.valueOf(mBarList.get(i).getCoordinates().getLongitude());
            String name = mBarList.get(i).getName();
            LatLng place = new LatLng(lat, lon);

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mBarList.get(i).getVisitor()))) // + "\n"
                    .position(place)
                    .title(name));
            startGeofence(marker,i);
            mMarkerArray.add(marker);
        }


    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if(checkPermission()){
            if(mMap!= null){
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.setMyLocationEnabled(true);
            }
        }
        else{
            askPermission();
        }
    }

    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        // Permission to access the location is missing.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
            mPermissionDenied=false;

        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            askPermission();
        }
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
                         Toast.makeText(getApplicationContext(), "Clicked: " +
                                         myPlace.getName() + "\nRating:" + myPlace.getRating() +
                                         "\nAddress:" + myPlace.getAddress() +  myPlace.getPlaceTypes().toString() , Toast.LENGTH_LONG).show();
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        try{
            database.setPersistenceEnabled(true);
        }catch (Exception e){
            Log.w(TAG,"SetPresistenceEnabled:Fail"+FirebaseDatabase.getInstance().toString());
            e.printStackTrace();
        }

        mDatabase = database.getReference();

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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, marker.getTitle());
        String name = marker.getTitle();
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

    // Start Geofence creation process
    private void startGeofence(Marker marker,int req_code) {
        Log.i(TAG, "startGeofence()");
            Geofence geofence = createGeofence( marker.getPosition(), GEOFENCE_RADIUS,marker.getTitle() );
            GeofencingRequest geofenceRequest = createGeofenceRequest( geofence );
            addGeofence( geofenceRequest, req_code );
            drawGeofence(marker);
    }

    private void restoreGeofences(){
        for (int i=0;i<mMarkerArray.size();i++){
            startGeofence(mMarkerArray.get(i),i);
        }

    }

    // Create a Geofence
    private Geofence createGeofence( LatLng latLng, float radius, String req_id ) {
        Log.d(TAG, "createGeofence");
        return new Geofence.Builder()
                .setRequestId(req_id)
                .setCircularRegion( latLng.latitude, latLng.longitude, radius)
                .setExpirationDuration( GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }

    // Create a Geofence Request
    private GeofencingRequest createGeofenceRequest( Geofence geofence ) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger( GeofencingRequest.INITIAL_TRIGGER_ENTER )
                .addGeofence( geofence )
                .build();
    }

    private PendingIntent createGeofencePendingIntent(int req_code) {
        Log.d(TAG, "createGeofencePendingIntent");
        if ( geoFencePendingIntent != null )
            return geoFencePendingIntent;

        Intent intent = new Intent( this, GeofenceTrasitionService.class);
        return PendingIntent.getService(
                this, req_code, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    // Add the created GeofenceRequest to the device's monitoring list
    private void addGeofence(GeofencingRequest request,int req_code) {
        Log.d(TAG, "addGeofence");
        if (checkPermission())
            mGeofencingClient.addGeofences(request, createGeofencePendingIntent(req_code)
            ).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG, "succsess add Geofence " + aVoid);
                    // ...
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: add Geofence " + e);
                }
            });
    }

    // Draw Geofence circle on GoogleMap

    private void drawGeofence(Marker marker) {
        Log.d(TAG, "drawGeofence()");

        CircleOptions circleOptions = new CircleOptions()
                .center( marker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( GEOFENCE_RADIUS );
        geoFenceLimits.add(mMap.addCircle( circleOptions));
    }

    // Clear Geofence
    private void clearGeofence() {
        Log.d(TAG, "clearGeofence()");
        if(mMarkerArray != null) {
            for (int i=0; i<mMarkerArray.size();i++) {
                mGeofencingClient.removeGeofences(createGeofencePendingIntent(i)).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeGeofenceDraw();
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to remove geofences
                        // ...
                    }
                });
            }
        }
    }

    private void removeGeofenceDraw() {
        Log.d(TAG, "removeGeofenceDraw()");
        if ( geoFenceLimits != null )
            for (int i=0;i<geoFenceLimits.size();i++) {
                geoFenceLimits.get(i).remove();
            }
        geoFenceLimits.clear();
    }
}
