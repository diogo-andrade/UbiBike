package pt.ulisboa.tecnico.cmov.ubibike;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.Channel;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by gae on 10/05/2016.
 */

//It's an activity and not a fragment
public class WifiActivity extends AppCompatActivity {
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
            new IncommingCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);


        }
    };

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("YO", "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                ((UBIApplication) getApplication()).setServer(new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = ((UBIApplication) getApplication()).getServer().accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        if(st.equals("Request")){
                            Toast.makeText(getBaseContext(), "oiii", Toast.LENGTH_LONG).show();
                        }
                        else if(st.startsWith("InfoProfile")){
                            //extract info from the message and added to near ubibikers list
                        }
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }

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
