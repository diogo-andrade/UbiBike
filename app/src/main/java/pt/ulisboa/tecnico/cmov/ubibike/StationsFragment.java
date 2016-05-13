package pt.ulisboa.tecnico.cmov.ubibike;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.MyExpandableAdapter;
import pt.ulisboa.tecnico.cmov.ubibike.exceptions.ErrorCodeException;
import pt.ulisboa.tecnico.cmov.ubibike.services.UBIClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationsFragment extends Fragment {

    private static final String STATE_STATIONS="stations";
    private static final String STATE_KEYS="keys";
    private static final String STATE_COORDINATES="coordinates";

    public HashMap<String,List<String>> stationsHashMap;
    public ArrayList<String> stationsHashMapKeys;
    public ArrayList<LatLng> stationsCoordinates;
    public ArrayList<Boolean> stateBooking;

    private UpdateStationsTask mUpdateStationsTask;
    private String mEmail;
    Timer t;
    View v;
    Bundle lastBundle;
    MyExpandableAdapter mAdapter;
    ExpandableListView expandableListView;
    private int lastExpandedPosition = -1;
    Boolean first = false;

    private static final String ARG_PARAM1 = "param1";

    public StationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UbibikersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationsFragment newInstance(String email) {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lastBundle = savedInstanceState;
        if (getArguments() != null) {
            mEmail = getArguments().getString(ARG_PARAM1);
        }

        if (savedInstanceState != null) {
            stationsHashMap = (HashMap<String,List<String>>) savedInstanceState.getSerializable(STATE_STATIONS);
            stationsHashMapKeys = (ArrayList<String>) savedInstanceState.getSerializable(STATE_KEYS);
            stationsCoordinates = (ArrayList<LatLng>)  savedInstanceState.getSerializable(STATE_COORDINATES);
            first = false;
        } else {
            first = true;
            stationsHashMapKeys = new ArrayList<String>();
            stationsHashMap = new HashMap<String, List<String>>();
            stationsCoordinates = new ArrayList<LatLng>();
            //prepareListData();
        }
        t = new Timer();
        t.scheduleAtFixedRate(timer, 0, 2000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stations, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        expandableListView = (ExpandableListView) getActivity().findViewById(R.id.stationsList);

        mAdapter =  new MyExpandableAdapter(getActivity().getBaseContext(), stationsHashMap, stationsHashMapKeys, stationsCoordinates, mEmail);
        expandableListView.setAdapter(mAdapter);

      /*  expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });*/
    }

    /*
     * Preparing the list data
     */
    private void processResponse(String json, String state) {

        stationsHashMapKeys.clear();
        stationsCoordinates.clear();
        stationsHashMap.clear();

        try {
            JSONObject mObject = new JSONObject(json.toString());
            JSONArray arrStations =  mObject.getJSONArray("stations");

            for (int i = 0; i < arrStations.length(); i++) {
                String stationName = arrStations.getJSONObject(i).getString("name");

                JSONObject location = arrStations.getJSONObject(i).getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");

                JSONArray arrBikes = arrStations.getJSONObject(i).getJSONArray("bikes");

                int bikesCounter = 0;
                for (int b = 0; b < arrBikes.length(); b++) {
                    bikesCounter+=1;
                }

                stationsHashMapKeys.add(stationName);
                stationsCoordinates.add(new LatLng(lat, lng));
                List<String> station = new ArrayList<String>();
                station.add("Available bikes: " + bikesCounter);
                stationsHashMap.put(stationsHashMapKeys.get(i), station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

/*

        // Adding child data
        stationsHashMapKeys.add("Station 0 - Cais do Sodré");
        stationsHashMapKeys.add("Station 1 - Chiado");
        stationsHashMapKeys.add("Station 2 - Areeiro");

        // Adding stations coordinates
        stationsCoordinates.add(new LatLng(38.7059084,-9.144370200000026));
        stationsCoordinates.add(new LatLng(38.710672,-9.139091000000008));
        stationsCoordinates.add(new LatLng(38.742599,-9.133806999999933));

        // Adding child data
        List<String> station0 = new ArrayList<String>();
        station0.add("Available bikes: 4");

        List<String> station1 = new ArrayList<String>();
        station1.add("Available bikes: 7");

        List<String> station2 = new ArrayList<String>();
        station2.add("Available bikes: 1");

        stationsHashMap.put(stationsHashMapKeys.get(0), station0); // Header, Child data
        stationsHashMap.put(stationsHashMapKeys.get(1), station1);
        stationsHashMap.put(stationsHashMapKeys.get(2), station2);*/
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_STATIONS, stationsHashMap);
        outState.putSerializable(STATE_KEYS, stationsHashMapKeys);
        outState.putSerializable(STATE_COORDINATES,stationsCoordinates);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.cancel();
    }

    TimerTask timer= new TimerTask(){

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mUpdateStationsTask = new UpdateStationsTask();
                    mUpdateStationsTask.execute((Void) null);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    public class UpdateStationsTask extends AsyncTask<Void, Void, Boolean> {
        private int mErrorCode;

        UpdateStationsTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                String response = new UBIClient().GET("http://10.0.2.2:5000/stations");
                String state = new UBIClient().GET("http://10.0.2.2:5000/stations");

                processResponse(response, state);

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
            mAdapter.notifyDataSetChanged();
        }
    }

/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/
/*
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
*/
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
