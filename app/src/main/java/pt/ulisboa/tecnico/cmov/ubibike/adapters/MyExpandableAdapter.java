package pt.ulisboa.tecnico.cmov.ubibike.adapters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import pt.ulisboa.tecnico.cmov.ubibike.MapsActivity;
import pt.ulisboa.tecnico.cmov.ubibike.R;
import pt.ulisboa.tecnico.cmov.ubibike.exceptions.ErrorCodeException;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;
import pt.ulisboa.tecnico.cmov.ubibike.services.UBIClient;

public class MyExpandableAdapter extends BaseExpandableListAdapter {

    private static final String EXTRA_TRACK = "pt.ulisboa.tecnico.cmov.ubibike.TRACK";
    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";

    private BookTask mBookTask = null;
    private BookCancelTask mBookCancelTask = null;

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    private List<LatLng> _stationsCoordinates;
    private String _email;

    TextView txtListChild;
    Boolean isBikeBooked = false;

    public MyExpandableAdapter(Context context, HashMap<String, List<String>> listChildData,
                               List<String> listDataHeader, List<LatLng> coordinates, String email) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this._stationsCoordinates = coordinates;
        this._email = email;
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

        final String parentText = (String) getGroup(groupPosition);

        final LatLng coordinates = _stationsCoordinates.get(groupPosition);

        Button button = (Button) convertView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ("BOOK".equals(((Button) v).getText().toString()) && !isBikeBooked) {
                    isBikeBooked = true;
                    ((Button) v).setText("CANCEL");
                    mBookTask = new BookTask(_email, parentText, (Button) v);
                    mBookTask.execute((Void) null);
                }
                else if ("CANCEL".equals(((Button) v).getText().toString())) {
                    isBikeBooked = false;
                    ((Button) v).setText("BOOK");
                    mBookCancelTask = new BookCancelTask(_email, (Button) v);
                    mBookCancelTask.execute((Void) null);
                }
            }
        });

        Button mapButton = (Button) convertView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Trajectory track = new Trajectory(parentText, coordinates);
                Intent intent = new Intent(v.getContext(), MapsActivity.class);
                intent.putExtra(EXTRA_TRACK, (Parcelable) track);
                intent.putExtra(EXTRA_NAME, parentText);
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

    public class BookTask extends AsyncTask<Void, Void, Boolean> {
        private int mErrorCode;
        private String mEmail;
        private String mStation;
        private Button mBtn;
        private String response;

        BookTask(String email, String station, Button btn) {
            this.mEmail = email;
            this.mStation = station;
            this.mBtn = btn;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                response = new UBIClient().GET("http://10.0.2.2:5000/book?email="+mEmail+"&station="+mStation.replaceAll(" ", "%20"));


                //processResponse(response);
            } catch (ErrorCodeException e){
                mErrorCode = e.getCode();
                return false;
            }
            catch (InterruptedException e) {
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success) {
                Toast.makeText(_context, "You have booked bike "+response +".", Toast.LENGTH_SHORT).show();
            } else if(mErrorCode == 410) {
                mBtn.setText("BOOK");
                isBikeBooked = false;
                Toast.makeText(_context, "This station are out of bikes.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public class BookCancelTask extends AsyncTask<Void, Void, Boolean> {
        private int mErrorCode;
        private String mEmail;
        private Button mBtn;
        private String response;

        BookCancelTask(String email, Button btn) {
            this.mEmail = email;
            this.mBtn = btn;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                response = new UBIClient().GET("http://10.0.2.2:5000/book/cancel?email="+mEmail);

                //processResponse(response);
            } catch (ErrorCodeException e){
                mErrorCode = e.getCode();
                return false;
            }
            catch (InterruptedException e) {
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if(success) {
                Toast.makeText(_context, response, Toast.LENGTH_SHORT).show();
            } else if(mErrorCode == 410) {
                mBtn.setText("BOOK");
                isBikeBooked = false;
                Toast.makeText(_context, "You don't have any bike booked.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}