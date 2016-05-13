package pt.ulisboa.tecnico.cmov.ubibike;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.ubibike.objects.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.cmov.ubibike.services.TermiteService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener
         {

    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";
    private static final String EXTRA_EMAIL = "pt.ulisboa.tecnico.cmov.ubibike.EMAIL";
    private static final String EXTRA_SCORE = "pt.ulisboa.tecnico.cmov.ubibike.SCORE";

    Fragment fragment = null;
    FragmentManager fragmentManager = getSupportFragmentManager();
    private String mName = "DEFAULT";
    private String mEmail = "DEFAULT";
    private String mScore = "DEFAULT";
    boolean  bool = false;

    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TermiteService tm = new TermiteService();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mName= getIntent().getStringExtra(EXTRA_NAME);
            mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
            mScore = getIntent().getStringExtra(EXTRA_SCORE);
        }

        // initialize the Termite API

        filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setOverflowButtonColor(Color.WHITE);
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView= navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView tvHeaderName= (TextView) navHeaderView.findViewById(R.id.header_name);
        tvHeaderName.setText(mName);

        //Set profile as Home fragment
        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            //TODO: fragment

            fragment = new ProfileFragment().newInstance(mName, mEmail, mScore);
            fragmentManager.beginTransaction().replace(R.id.Content, fragment).commit();
        }

        // Setup Location manager and receiver
        LocationManager lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);
    }

    // Search for the Overflow icon and replace his color
    private void setOverflowButtonColor(final int color) {
        final String overflowDescription = MainActivity.this.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) MainActivity.this.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription,
                        View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
                // removeOnGlobalLayoutListener(decorView, this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_near_ubibikers) {

                fragment = new NearUbikers().newInstance();
                fragmentManager.beginTransaction().replace(R.id.Content, fragment).commit();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        boolean isFragment = false;

        // Handle navigation view item clicks here.
        switch(item.getItemId()) {
            case R.id.nav_profile:
                //TODO: fragment
                fragment = new ProfileFragment().newInstance(mName, mEmail, mScore);
                isFragment = true;
                break;
            case R.id.nav_ubibikers:
                String arg = "test";
                fragment = new UbibikersFragment().newInstance(arg, arg);
                isFragment = true;
                break;
            case R.id.nav_stations:
                fragment = new StationsFragment().newInstance(mEmail);
                isFragment = true;
                break;
            case R.id.nav_wifi:
                //TODO: activity
                fragment = new WifiFragment().newInstance();
                isFragment = true;
                break;
            case R.id.nav_settings:
                //TODO: activity
                break;
            case R.id.nav_logout:
                DialogFragment newFragment = LogoutDialogAlertFragment.newInstance();
                newFragment.show(getFragmentManager(), "dialog");
                break;
        }

        if (isFragment) {
            // Replace whatever is in the fragment Content view with this fragment
            fragmentManager.beginTransaction().replace(R.id.Content, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    // Methods invoked by LogoutDialogAlertFragment
    public void doYesClick() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void doNoClick() {
        // Do stuff here.
    }

    // Metodos relativos ao location
    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getBaseContext(), "selected Item Name is " + location.toString(), Toast.LENGTH_LONG).show();
        Log.d("GPS", "Location Changed " + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}

