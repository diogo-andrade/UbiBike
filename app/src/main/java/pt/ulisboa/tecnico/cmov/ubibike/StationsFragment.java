package pt.ulisboa.tecnico.cmov.ubibike;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.MyExpandableAdapter;


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

    public HashMap<String,List<String>> stationsHashMap;
    public ArrayList<String> stationsHashMapKeys;

    public StationsFragment() {
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
    public static StationsFragment newInstance() {
        StationsFragment fragment = new StationsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        ExpandableListView expandableListView = (ExpandableListView) getActivity().findViewById(R.id.stationsList);

        if (savedInstanceState != null) {
            stationsHashMap = (HashMap<String,List<String>>) savedInstanceState.getSerializable(STATE_STATIONS);
            stationsHashMapKeys = (ArrayList<String>) savedInstanceState.getSerializable(STATE_KEYS);
        } else {
            prepareListData();
        }
        MyExpandableAdapter mAdapter =  new MyExpandableAdapter(getActivity().getBaseContext(), stationsHashMap, stationsHashMapKeys);
        expandableListView.setAdapter(mAdapter);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        stationsHashMapKeys = new ArrayList<String>();
        stationsHashMap = new HashMap<String, List<String>>();

        // Adding child data
        stationsHashMapKeys.add("Station 0 - Cais do Sodré");
        stationsHashMapKeys.add("Station 1 - Chiado");
        stationsHashMapKeys.add("Station 2 - Areeiro");

        // Adding child data
        List<String> station0 = new ArrayList<String>();
        station0.add("Available bikes: 4");

        List<String> station1 = new ArrayList<String>();
        station1.add("Available bikes: 7");

        List<String> station2 = new ArrayList<String>();
        station2.add("Available bikes: 1");

        stationsHashMap.put(stationsHashMapKeys.get(0), station0); // Header, Child data
        stationsHashMap.put(stationsHashMapKeys.get(1), station1);
        stationsHashMap.put(stationsHashMapKeys.get(2), station2);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_STATIONS, stationsHashMap);
        outState.putSerializable(STATE_KEYS, stationsHashMapKeys);
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