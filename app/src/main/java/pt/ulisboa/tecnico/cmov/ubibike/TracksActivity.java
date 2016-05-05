package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.TracksAdapter;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;



public class TracksActivity extends ListActivity {
    private static final String EXTRA_TRACKS = "pt.ulisboa.tecnico.cmov.ubibike.TRACKS";
    private static final String EXTRA_TRACK = "pt.ulisboa.tecnico.cmov.ubibike.TRACK";
    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";

    private ArrayAdapter<String> mAdapter;
    private ArrayList<Trajectory> mItems;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);

        Bundle extras = getIntent().getExtras();

        ListView view = (ListView) findViewById(android.R.id.list);

        if (extras != null) {
            mItems =  extras.getParcelableArrayList(EXTRA_TRACKS);
            mAdapter = new TracksAdapter(getApplicationContext(),R.layout.track_list_item, mItems);

            view.setAdapter(mAdapter);

            //Handles all the track click to open on Map Activity
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView textView = (TextView) view.findViewById(R.id.trackName);
                    String trackName = textView.getText().toString();
                    Trajectory selectedTrack = getTrajectory(trackName);

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra(EXTRA_TRACK, (Parcelable) selectedTrack);
                    intent.putExtra(EXTRA_NAME, trackName);
                    startActivity(intent);
                }
            });
        }
    }

    public Trajectory getTrajectory(String name) {
        for (Trajectory track : mItems) {
            if (name.equals(track.getName())) {
                return track;
            }
        }
        return null;
    }
}