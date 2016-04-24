package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UbibikersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UbibikersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UbibikersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String EXTRA_NAME = "name";
    private static final String EXTRA_EMAIL = "email";

    private static final String STATE_ITEMS="items";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> mItems;
    private ArrayAdapter<String> mAdapter;

    //private OnFragmentInteractionListener mListener;

    public UbibikersFragment() {
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
    public static UbibikersFragment newInstance(String param1, String param2) {
        UbibikersFragment fragment = new UbibikersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ubibikers, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // On screen rotation loads ListView items previously searched
        if (savedInstanceState != null) {
            mItems = (ArrayList<String>) savedInstanceState.getSerializable(STATE_ITEMS);
        } else {
            mItems = new ArrayList<>();
        }

        SearchView sv = (SearchView) view.findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: tratar da query e ligar-se ao server
                String[] items = {"João Silva", "Bárbara Água", "Diogo Andrade"};
                mItems = new ArrayList<>(Arrays.asList(items));
                ListView listView = (ListView) getActivity().findViewById(R.id.listUbibikersView);
                mAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.ubibiker_list_item, R.id.ubibikerName, mItems);

                listView.setAdapter(mAdapter);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ListView listView = (ListView) getActivity().findViewById(R.id.listUbibikersView);
        mAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), R.layout.ubibiker_list_item, R.id.ubibikerName, mItems);

        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vName = (TextView) view.findViewById(R.id.ubibikerName);
                TextView vEmail = (TextView) view.findViewById(R.id.ubibikerEmail);
                Intent intent = new Intent(getContext(), UbibikerProfileActivity.class);
                intent.putExtra(EXTRA_NAME,vName.getText());
                intent.putExtra(EXTRA_EMAIL,vEmail.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_ITEMS, mItems);
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
