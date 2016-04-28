package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        //Set profile as Home fragment
        navigationView.getMenu().getItem(0).setChecked(true);
        //TODO: fragment
        String args = "test";
        fragment = new ProfileFragment().newInstance(args, args);
        fragmentManager.beginTransaction().replace(R.id.Content, fragment).commit();
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
                String args = "test";
                fragment = new ProfileFragment().newInstance(args, args);
                isFragment = true;
                break;
            case R.id.nav_ubibikers:
                String arg = "test";
                fragment = new UbibikersFragment().newInstance(arg, arg);
                isFragment = true;
                break;
            case R.id.nav_stations:
                Intent intent = new Intent(this, RoutesActivity.class);
                startActivity(intent);
                //TODO: fragment
                break;
            case R.id.nav_routes:
                isFragment = true;
                //TODO: fragment
                break;
            case R.id.nav_wifi:
                //TODO: activity
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

    // Methods invoked by LogoutDialogAlertFragment
    public void doYesClick() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void doNoClick() {
        // Do stuff here.
    }

}

