package hs_ab.com.TrinkBar;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;


import java.util.ArrayList;
import java.util.List;

import hs_ab.com.TrinkBar.models.Bar;
import hs_ab.com.TrinkBar.models.Bars;

public class ListActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue requestQueue;
    private static ListActivity mInstance;


    private Context mCtx;
    private static final String TAG = "LOG";
    private List<Bar> barList;
    private RecyclerView mRv;
    private DBAdapter db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance=this;
        mCtx = getApplicationContext();
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Side menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_details);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_details);
        navigationView.setNavigationItemSelectedListener(this);
        barList = new ArrayList<>();
        db = DBAdapter.getInstance(mCtx);

        // RV for List
        mRv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRv.setLayoutManager(llm);
        mRv.setHasFixedSize(true);
    }


    @Override
    protected void onResume() {
        super.onResume();


        barList= db.getBarList();
        // init Adapter with Data from Server
        if(mRv.getAdapter()==null) {
            initializeAdapter();
        }
        /*String url="https://trinkbar.azurewebsites.net/files/bars.json";

        HttpGetRequest myCustomRequest=new HttpGetRequest(Request.Method.GET, url,Bars.class, new Response.Listener<Bars>() {
            @Override
            public void onResponse(Bars bar) {

                barList = bar.getBars();
                // init Adapter with Data from Server
                if(mRv.getAdapter()==null) {
                    initializeAdapter();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError response) {

                //Failure callback
                Toast.makeText(ListActivity.this,"Error Encountered",Toast.LENGTH_SHORT).show();

            }
        });*/

        //Adding the request to a request queue
        //ListActivity.getInstance().addToRequestQueue(myCustomRequest,"tag");
    }


    private void initializeAdapter() {
        Log.d(TAG, "initializeAdapter");
        RVAdapter adapter = new RVAdapter(this, barList);
        mRv.setAdapter(adapter);
    }

    // Action Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_details, menu);
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

    public static synchronized ListActivity getInstance()
    {
        return mInstance;
    }
}
