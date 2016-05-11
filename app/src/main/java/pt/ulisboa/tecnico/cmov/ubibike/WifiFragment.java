package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

//It's an activity and not a fragment
public class WifiFragment extends AppCompatActivity {
    private static final String TAG = "WifiActivity";

    private Messenger mService = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_fragment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.idWifiOnButton).setOnClickListener(listenerWifiOnButton);
        findViewById(R.id.idWifiOffButton).setOnClickListener(listenerWifiOffButton);
        findViewById(R.id.idWifiOnButton).setEnabled(true);
        findViewById(R.id.idWifiOffButton).setEnabled(true);
    }



    private View.OnClickListener listenerWifiOnButton = new View.OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(v.getContext(), SimWifiP2pService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            ((UBIApplication) getApplication()).setBound(true);

            findViewById(R.id.idWifiOffButton).setEnabled(true);

            v.findViewById(R.id.idWifiOnButton).setEnabled(false);


        }
    };

    private View.OnClickListener listenerWifiOffButton = new View.OnClickListener() {
        public void onClick(View v){
            if (((UBIApplication) getApplication()).getBound()) {
                unbindService(mConnection);
                ((UBIApplication) getApplication()).setBound(false);
                findViewById(R.id.idWifiOnButton).setEnabled(true);
                v.findViewById(R.id.idWifiOffButton).setEnabled(false);
            }
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ((UBIApplication) getApplication()).setManager(new SimWifiP2pManager(new Messenger(service)));
            ((UBIApplication) getApplication()).setChannel(((UBIApplication) getApplication()).getManager().initialize(getApplication(), getMainLooper(), null));
            ((UBIApplication) getApplication()).setBound(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            ((UBIApplication) getApplication()).setManager(null);
            ((UBIApplication) getApplication()).setChannel(null);
            ((UBIApplication) getApplication()).setBound(false);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
        return true;
    }

}
