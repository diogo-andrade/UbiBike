package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.TracksAdapter;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;



public class TracksActivity extends ListActivity {
    private static final String EXTRA_TRACKS = "pt.ulisboa.tecnico.cmov.ubibike.TRACKS";

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

            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getBaseContext(), "Fui clicado, yupi!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}