package pt.ulisboa.tecnico.cmov.ubibike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.UbibikerAdapter;
import pt.ulisboa.tecnico.cmov.ubibike.exceptions.ErrorCodeException;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;
import pt.ulisboa.tecnico.cmov.ubibike.services.UBIClient;

public class UbibikersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";
    private static final String EXTRA_EMAIL = "pt.ulisboa.tecnico.cmov.ubibike.EMAIL";

    private static final String STATE_ITEMS="items";
    private static final String STATE_VISIBLE="visible";

    private QueryTask mQueryTask;
    private View mProgressView;
    private ListView listView;
    TextView noResults;

    private boolean mFoundResults;

    private ArrayList<Ubibiker> mItems;
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

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // On screen rotation loads ListView items previously searched
        if (savedInstanceState != null) {
            mItems = (ArrayList<Ubibiker>) savedInstanceState.getSerializable(STATE_ITEMS);
            mFoundResults = (boolean)  savedInstanceState.getSerializable(STATE_VISIBLE);

        } else {
            mFoundResults = true;
            mItems = new ArrayList<>();
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

        mProgressView =   getActivity().findViewById(R.id.search_progress);
        listView = (ListView) getActivity().findViewById(R.id.listUbibikersView);

        noResults = (TextView) getActivity().findViewById(R.id.no_results);

        if(mFoundResults) {
            noResults.setVisibility(View.GONE);
        } else {
            noResults.setVisibility(View.VISIBLE);
        }

        mAdapter = new UbibikerAdapter(getActivity().getBaseContext(),R.layout.ubibiker_list_item, mItems);
        listView.setAdapter(mAdapter);

        SearchView sv = (SearchView) view.findViewById(R.id.searchView);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                mFoundResults = false;
                noResults.setVisibility(View.GONE);
                showProgress(true);
                mQueryTask = new QueryTask(query);
                mQueryTask.execute((Void) null);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //mAdapter = new UbibikerAdapter(getActivity().getBaseContext(),R.layout.ubibiker_list_item, mItems);

        //listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vName = (TextView) view.findViewById(R.id.ubibikerName);
                TextView vEmail = (TextView) view.findViewById(R.id.ubibikerEmail);
                Intent intent = new Intent(getActivity().getBaseContext(), UbibikerProfileActivity.class);
                intent.putExtra(EXTRA_NAME, vName.getText());
                intent.putExtra(EXTRA_EMAIL, vEmail.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_ITEMS, mItems);
        outState.putBoolean(STATE_VISIBLE, mFoundResults);
    }

    public class QueryTask extends AsyncTask<Void, Void, Boolean> {
        private final String mQuery;
        private int mErrorCode;

        QueryTask(String query) {
            mQuery = query;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String response = new UBIClient().GET("http://10.0.2.2:5000/ubibiker?name=" + mQuery.replaceAll(" ", "%20"));

                mItems = processResult(response);
                mAdapter = new UbibikerAdapter(getActivity().getBaseContext(),R.layout.ubibiker_list_item, mItems);

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
            showProgress(false);
            if (mItems.isEmpty()) {
                mFoundResults = false;
                noResults.setVisibility(View.VISIBLE);
            }
            else {
                mFoundResults = true;
            }
            listView.setAdapter(mAdapter);

        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            listView.setVisibility(show ? View.GONE : View.VISIBLE);
            listView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            listView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public ArrayList<Ubibiker> processResult(String response) {
        ArrayList<Ubibiker> result = new ArrayList<Ubibiker>();

        try {
            JSONObject mObject = new JSONObject(response.toString());

            JSONArray arr = mObject.getJSONArray("ubibikers");

            for (int i = 0; i < arr.length(); i++) {
                String name = arr.getJSONObject(i).getString("name");
                String email = arr.getJSONObject(i).getString("email");
                Ubibiker ubibiker = new Ubibiker(name, email);
                result.add(ubibiker);
            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
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
