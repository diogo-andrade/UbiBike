package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;

/**
 * Created by gae on 10/05/2016.
 */
public class WifiFragment extends Fragment{

    private SimWifiP2pManager mManager = null;
    private boolean mBound = false;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;

    public WifiFragment() {
        // Required empty public constructor
    }

    public static WifiFragment newInstance() {
        WifiFragment fragment = new WifiFragment();

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
        return inflater.inflate(R.layout.wifi_fragment, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.idWifiOnButton).setOnClickListener(listenerWifiOnButton);
        view.findViewById(R.id.idWifiOffButton).setOnClickListener(listenerWifiOffButton);
        view.findViewById(R.id.idWifiOnButton).setEnabled(true);
        view.findViewById(R.id.idWifiOffButton).setEnabled(true);
    }


    private View.OnClickListener listenerWifiOnButton = new View.OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), SimWifiP2pService.class);
            getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;

            getView().findViewById(R.id.idWifiOffButton).setEnabled(true);

            v.findViewById(R.id.idWifiOnButton).setEnabled(false);


        }
    };

    private View.OnClickListener listenerWifiOffButton = new View.OnClickListener() {
        public void onClick(View v){
            Log.d("DEBUG", "" + mBound);
            if (mBound) {
                getActivity().unbindService(mConnection);
                mBound = false;
                getView().findViewById(R.id.idWifiOnButton).setEnabled(true);
                v.findViewById(R.id.idWifiOffButton).setEnabled(false);
            }
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mManager = new SimWifiP2pManager(new Messenger(service));
            mChannel = mManager.initialize( getActivity().getApplication(), getActivity().getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

}
