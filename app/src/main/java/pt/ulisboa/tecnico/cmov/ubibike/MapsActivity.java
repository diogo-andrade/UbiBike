package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String EXTRA_TRACK = "pt.ulisboa.tecnico.cmov.ubibike.TRACK";
    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";

    private GoogleMap mMap;
    private LatLng startPoint;
    private LatLng endPoint;
    private PolylineOptions line;
    private String trackName;
    private Boolean isTrajectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Trajectory track = getIntent().getParcelableExtra(EXTRA_TRACK);

        if(track != null)
            // Situação em que temos apenas um ponto
            if(track.getEnd() == null) {
                isTrajectory = false;
                startPoint = track.getStart();
                trackName = getIntent().getExtras().getString(EXTRA_NAME);
                System.out.println(trackName);
            }  else { //Situação em que temos uma trajectória
                isTrajectory = true;
                startPoint = track.getStart();
                endPoint = track.getEnd();
                line = track.getLine();
                trackName = getIntent().getExtras().getString(EXTRA_NAME);
            }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        if (isTrajectory) {
          setTrajectoryOnMap();
        } else {
            setPointOnMap();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(startPoint)
                .zoom(17).build();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    public void setPointOnMap (){
        mMap.addMarker(new MarkerOptions().position(startPoint).title(trackName));
    }

    public void setTrajectoryOnMap (){
        mMap.addMarker(new MarkerOptions().position(startPoint).title(trackName));
        mMap.addMarker(new MarkerOptions().position(endPoint));
        mMap.addPolyline(line);
    }

}
