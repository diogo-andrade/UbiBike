package pt.ulisboa.tecnico.cmov.ubibike.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.R;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;

/**
 * Created by diogo on 29-04-2016.
 */
public class TracksAdapter extends ArrayAdapter {

    private ArrayList<Trajectory> mObjects;
    private Context mContext;
    private int mResourceView;

    public TracksAdapter(Context context, int resource, ArrayList<Trajectory> objects) {
        super(context, resource ,objects);
        this.mObjects = objects;
        this.mContext = context;
        this.mResourceView = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResourceView, null);
        }

        Trajectory trajectory = mObjects.get(position);

        // Check if list is empty
        if (trajectory != null) {
            TextView name = (TextView) convertView.findViewById(R.id.trackName);
            TextView date = (TextView) convertView.findViewById(R.id.trackDate);

            name.setText(trajectory.getName());
            date.setText(trajectory.getTimeStamp().toString());
        }

        return convertView;
    }

}
