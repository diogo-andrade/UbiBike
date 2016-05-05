package pt.ulisboa.tecnico.cmov.ubibike.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import pt.ulisboa.tecnico.cmov.ubibike.MapsActivity;
import pt.ulisboa.tecnico.cmov.ubibike.R;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private static final String EXTRA_TRACK = "pt.ulisboa.tecnico.cmov.ubibike.TRACK";
    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private List<LatLng> _stationsCoordinates;

    TextView txtListChild;
    Boolean isBikeBooked = false;

    public MyExpandableAdapter(Context context, HashMap<String, List<String>> listChildData,
                               List<String> listDataHeader, List<LatLng> coordinates) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._stationsCoordinates = coordinates;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout, null);
        }

        txtListChild = (TextView) convertView
                .findViewById(R.id.textViewChild);

        txtListChild.setText(childText);

        Button button = (Button) convertView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ("BOOK".equals(((Button) v).getText().toString()) && !isBikeBooked) {
                    isBikeBooked = true;
                    ((Button) v).setText("CANCEL");
                    String[] text = ((TextView)((RelativeLayout) v.getParent()).findViewById(R.id.textViewChild)).getText().toString().split(": ");
                    String t1 = text[0];
                    int t2 = Integer.parseInt(text[1])+1;
                    ((TextView)((RelativeLayout) v.getParent()).findViewById(R.id.textViewChild)).setText(t1 + ": " + t2);
                }
                else if ("CANCEL".equals(((Button) v).getText().toString())) {
                    isBikeBooked = false;
                    ((Button) v).setText("BOOK");
                }
            }
        });

        Button mapButton = (Button) convertView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LatLng coordinates = _stationsCoordinates.get(groupPosition);
                Trajectory track = new Trajectory(coordinates);
                String station = _listDataHeader.get(groupPosition);
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra(EXTRA_TRACK, (Parcelable) track);
                intent.putExtra(EXTRA_NAME, station);
                v.getContext().startActivity(intent);
                //finish();

            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.parent_layout, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.textViewParent);
        //lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}