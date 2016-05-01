package pt.ulisboa.tecnico.cmov.ubibike.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.R;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;

/**
 * Created by diogo on 29-04-2016.
 */
public class UbibikerAdapter extends ArrayAdapter {

    private ArrayList<Ubibiker> mObjects;
    private Context mContext;
    private int mResourceView;

    public UbibikerAdapter (Context context, int resource, ArrayList<Ubibiker> objects) {
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

        Ubibiker ubibiker = mObjects.get(position);

        // Check if list is empty
        if (ubibiker != null) {
            TextView name = (TextView) convertView.findViewById(R.id.ubibikerName);
            TextView email = (TextView) convertView.findViewById(R.id.ubibikerEmail);

            name.setText(ubibiker.getName());
            email.setText(ubibiker.getEmail());
        }

        return convertView;
    }

}
