package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.LocalActivityManager;
import android.content.Intent;
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

import java.sql.Timestamp;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.objects.Trajectory;

/**
 * Created by Loureiro on 18-04-2016.
 */
public class ProfileFragment extends Fragment {

    private static final String STATE_TAB = "tab";

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
    private String p_name;
    private String p_email;
    private String p_score;

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
            p_name = getArguments().getString(ARG_PARAM1);
            p_email = getArguments().getString(ARG_PARAM2);
            p_score = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentTab =  (int) savedInstanceState.getSerializable(STATE_TAB);
        }
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView t1 = (TextView)rootView.findViewById(R.id.nameView);
        t1.setText(p_name);
        TextView t2 = (TextView)rootView.findViewById(R.id.emailView);
        t2.setText(p_email);
        TextView t3 = (TextView)rootView.findViewById(R.id.scoreView);
        t3.setText("SCORE: "+ p_score);

        mTabHost = (TabHost) rootView.findViewById(R.id.tabHost);
        mLocalActivityManager = new LocalActivityManager(getActivity(),false);
        mLocalActivityManager.dispatchCreate(savedInstanceState);
        mTabHost.setup(mLocalActivityManager);
        //mTabHost.setup(LocalActivityManager);

        TabHost.TabSpec tabRecentTracks = mTabHost.newTabSpec("Tab1");
        tabRecentTracks.setIndicator("Most Recent Tracks");
        Intent i1 = new Intent().setClass(getContext(),TracksActivity.class);
        i1.putParcelableArrayListExtra(EXTRA_TRACKS, generateData());
        tabRecentTracks.setContent(i1);
        mTabHost.addTab(tabRecentTracks);

        TabHost.TabSpec tabPastTracks = mTabHost.newTabSpec("Tab2");
        tabPastTracks.setIndicator("Past Tracks");
        Intent i2 = new Intent().setClass(getContext(),TracksActivity.class);
        tabPastTracks.setContent(i2);
        mTabHost.addTab(tabPastTracks);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mCurrentTab = mTabHost.getCurrentTab();
            }
        });

        mTabHost.setCurrentTab(mCurrentTab);

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
    }

    public ArrayList<Trajectory> generateData() {
        Trajectory t1 = new Trajectory(new LatLng(38.7363219,-9.1378428));
        Trajectory t2 = new Trajectory(new LatLng(38.7363219,-9.1378428));
        Trajectory t3 = new Trajectory(new LatLng(38.7363219,-9.1378428));

        t1.setName("Técnico");
        t2.setName("Secção de Folhas");
        t3.setName("Desordem dos Engenheiros");

        t1.setTimeStamp(new Timestamp(1213124211));
        t2.setTimeStamp(new Timestamp(950432932));
        t3.setTimeStamp(new Timestamp(329432909));

        ArrayList<Trajectory> result = new ArrayList<Trajectory>();

        result.add(t1);
        result.add(t2);
        result.add(t3);

        return result;
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
