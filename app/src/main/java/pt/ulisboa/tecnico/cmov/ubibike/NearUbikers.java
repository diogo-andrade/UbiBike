package pt.ulisboa.tecnico.cmov.ubibike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.UbibikerAdapter;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;


/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link NearUbikers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NearUbikers extends Fragment {
    private static final String EXTRA_NAME = "pt.ulisboa.tecnico.cmov.ubibike.NAME";
    private static final String EXTRA_EMAIL = "pt.ulisboa.tecnico.cmov.ubibike.EMAIL";

    private static final String STATE_ITEMS="items";

    private View mProgressView;
    private ListView listView;

    private ArrayList<Ubibiker> mItems;
    private ArrayAdapter<String> mAdapter;

    //private OnFragmentInteractionListener mListener;

    public NearUbikers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * @return A new instance of fragment UbibikersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NearUbikers newInstance() {
        NearUbikers fragment = new NearUbikers();

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
        return inflater.inflate(R.layout.fragment_near_ubikers, container, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // On screen rotation loads ListView items previously searched
        if (savedInstanceState != null) {
            mItems = (ArrayList<Ubibiker>) savedInstanceState.getSerializable(STATE_ITEMS);
        } else {
            mItems = new ArrayList<>();
        }
        //mProgressView =   getActivity().findViewById(R.id.search_progress);
        listView = (ListView) getActivity().findViewById(R.id.nearUbibikersList);

        mItems = generateQueryResult();

        mAdapter = new UbibikerAdapter(getActivity().getBaseContext(),R.layout.ubibiker_list_item, mItems);

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView vName = (TextView) view.findViewById(R.id.ubibikerName);
                TextView vEmail = (TextView) view.findViewById(R.id.ubibikerEmail);
                Intent intent = new Intent(getContext(), UbibikerProfileActivity.class);
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

    public ArrayList<Ubibiker> generateQueryResult() {
        Ubibiker u1 = new Ubibiker("Diogo Andrade", "diogo@yubo.be");
        Ubibiker u2 = new Ubibiker("Rafael Barreira", "raba@inesc.pt");
        Ubibiker u3 = new Ubibiker("Pedro Loureiro", "pl@hotmail.com");

        ArrayList<Ubibiker> result = new ArrayList<Ubibiker>();

        result.add(u1);
        result.add(u2);
        result.add(u3);

        return result;
    }

}
