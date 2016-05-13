package pt.ulisboa.tecnico.cmov.ubibike.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.ubibike.MainActivity;
import pt.ulisboa.tecnico.cmov.ubibike.R;
import pt.ulisboa.tecnico.cmov.ubibike.objects.SimWifiP2pBroadcastReceiver;

/**
 * Created by gae on 13/05/2016.
 */
public class TermiteService extends Service implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {
    private SimWifiP2pBroadcastReceiver mReceiver;
    private SimWifiP2pManager.Channel mChannel = null;
    private SimWifiP2pManager mManager;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private static TermiteService instance = null;

    private static String Name;
    private static String Mail;
    private List<String[]> groupUsers = new ArrayList<String[]>();

    public TermiteService() {
        super();
        instance = this;
    }

    public static TermiteService getInstance() {
        return instance;
    }

    public List<String[]> getGroupUsers(){
        return groupUsers;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DEBUG", "ola ola ola");
        Name = intent.getStringExtra("UserName");
        Mail = intent.getStringExtra("UserMail");
        startTermite();

        return 0;
    }

    public void startTermite(){

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(TermiteService.this);
        registerReceiver(mReceiver, filter);


        //BindService
        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;

        // spawn the chat server background task
        new IncommingCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void onDisconect(){
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        instance = null;
    }

    @Override
    public void onDestroy() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        instance = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void updatePeers() {
        if (mBound) {
            mManager.requestPeers(mChannel, TermiteService.this);
        } else {
            Toast.makeText(getBaseContext(), "Service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateGroup() {
        if (mBound) {
            mManager.requestGroupInfo(mChannel, TermiteService.this);
        } else {
            Toast.makeText(getBaseContext(), "Service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("DEBUG", "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        if(st.startsWith("[INFO]")){
                            Log.d("DEBUG", "INFO");
                            String[] stt = st.split("%%");
                            groupUsers.add(stt);
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

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                mCliSocket = new SimWifiP2pSocket(msg[1],
                        Integer.parseInt(getString(R.string.port)));
                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices, SimWifiP2pInfo groupInfo)  {
        StringBuilder peersStr = new StringBuilder();
        Log.d("DEBUG", "GROUP");
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
            Toast.makeText(this.getApplicationContext(), device.deviceName + "%%" + device.getVirtIp(),
                    Toast.LENGTH_SHORT).show();
            String content = "[INFO]";
            String ip = device.getVirtIp();
            new SendCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    content, ip, device.getVirtIp());
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
            Toast.makeText(this.getApplicationContext(), device.deviceName + "%%" + device.getVirtIp(),
                    Toast.LENGTH_SHORT).show();

        }

    }



    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };
}
