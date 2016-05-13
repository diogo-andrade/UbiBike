package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.UbibikerAdapter;
import pt.ulisboa.tecnico.cmov.ubibike.exceptions.ErrorCodeException;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;
import pt.ulisboa.tecnico.cmov.ubibike.services.UBIClient;

/**
 * Created by Loureiro on 18-04-2016.
 */
public class ProfileFragment extends Fragment {

    private static final String STATE_TAB = "tab";
    private static final String STATE_TRACKS = "tracks";
    private static final String STATE_SCORE = "score";

    private static final String EXTRA_TRACKS = "pt.ulisboa.tecnico.cmov.ubibike.TRACKS";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private TabHost mTabHost;
    private int mCurrentTab;

    LocalActivityManager mLocalActivityManager;

    // TODO: Rename and change types of parameters
    private String mName;
    private String mEmail;
    private String mScore;

    TabHost.TabSpec tabRecentTracks;
    TabHost.TabSpec tabPastTracks;
    View rootView;

    Ubibiker ubibiker = null;
    ArrayList<Trajectory> mTracks;

    private UpdateProfileTask mUpdateProfileTask;

    //private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UbibikersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2, String param3) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mName = getArguments().getString(ARG_PARAM1);
            mEmail = getArguments().getString(ARG_PARAM2);
            mScore = getArguments().getString(ARG_PARAM3);
        }

        if (savedInstanceState != null) {
            mCurrentTab =  (int) savedInstanceState.getSerializable(STATE_TAB);
            mTracks = (ArrayList<Trajectory>) savedInstanceState.getSerializable(STATE_TRACKS);
            mScore = (String) savedInstanceState.getSerializable(STATE_SCORE);

        } else {
            mTracks = new ArrayList<Trajectory>();
            mUpdateProfileTask = new UpdateProfileTask(mEmail);
            mUpdateProfileTask.execute((Void) null);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView t1 = (TextView)rootView.findViewById(R.id.nameView);
        t1.setText(mName);
        TextView t2 = (TextView)rootView.findViewById(R.id.emailView);
        t2.setText(mEmail);

        mTabHost = (TabHost) rootView.findViewById(R.id.tabHost);

        mLocalActivityManager = new LocalActivityManager(getActivity(),false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);

        if (savedInstanceState != null) {
            updateView();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocalActivityManager.dispatchResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocalActivityManager.dispatchPause(isRemoving());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_TAB, mCurrentTab);
        outState.putSerializable(STATE_TRACKS, mTracks);
        outState.putSerializable(STATE_SCORE, mScore);
    }

    public void processResult(String json) {
        mTracks = new ArrayList<Trajectory>();

        try {
            JSONObject mObject = new JSONObject(json.toString());
            String name = mObject.getString("name");
            String email = mObject.getString("email");
            int score = mObject.getInt("points");
            ubibiker = new Ubibiker(name, email);
            ubibiker.setPoints(score);

            Trajectory track;
            JSONArray arr = mObject.getJSONArray("tracks");
            for (int i = 0; i < arr.length(); i++) {
                String trackName = arr.getJSONObject(i).getString("name");
                long ts = arr.getJSONObject(i).getLong("ts");
                JSONObject end = arr.getJSONObject(i).getJSONObject("end");
                double endLat = end.getDouble("lat");
                double endLng = end.getDouble("lng");
                JSONObject start = arr.getJSONObject(i).getJSONObject("start");
                double startLat = end.getDouble("lat");
                double startLng = end.getDouble("lng");
                JSONArray line = arr.getJSONObject(i).getJSONArray("line");
                PolylineOptions polyline = drawLine(line);
                track = new Trajectory(new LatLng(endLat, endLng), new LatLng(startLat,startLng),polyline);
                track.setName(trackName);
                track.setTimeStamp(ts);
                mTracks.add(track);
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }
        ubibiker.setTrajectories(mTracks);
    }

    public PolylineOptions drawLine(JSONArray line) {
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        try {
            for (int l = 0; l < line.length(); l++) {
                double lineLat = line.getJSONObject(l).getDouble("lat");
                double lineLng = line.getJSONObject(l).getDouble("lng");
                LatLng point = new LatLng(lineLat,lineLng);
                options.add(point);
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return options;
    }

    public class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private int mErrorCode;

        UpdateProfileTask(String mEmail) {
            this.mEmail = mEmail;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //String json = "[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
                String response = new UBIClient().GET("http://10.0.2.2:5000/profile?email="+mEmail);

                processResult(response);
                mScore = ubibiker.getPoints() + "";

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
            updateView();
        }
    }

    public void updateView () {
        TextView t3 = (TextView) rootView.findViewById(R.id.scoreView);
        t3.setText("SCORE: "+ mScore);

        mTabHost.setup(mLocalActivityManager);
        //mTabHost.setup(LocalActivityManager);

        tabRecentTracks = mTabHost.newTabSpec("Tab1");
        tabRecentTracks.setIndicator("Most Recent Tracks");
        Intent i1 = new Intent().setClass(getContext(),TracksActivity.class);
        i1.putParcelableArrayListExtra(EXTRA_TRACKS, mTracks);
        tabRecentTracks.setContent(i1);
        mTabHost.addTab(tabRecentTracks);

        tabPastTracks = mTabHost.newTabSpec("Tab2");
        tabPastTracks.setIndicator("Past Tracks");
        Intent i2 = new Intent().setClass(getContext(), TracksActivity.class);
        tabPastTracks.setContent(i2);
        mTabHost.addTab(tabPastTracks);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mCurrentTab = mTabHost.getCurrentTab();
            }
        });

        mTabHost.setCurrentTab(mCurrentTab);
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
