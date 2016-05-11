package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Application;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmov.ubibike.objects.SimWifiP2pBroadcastReceiver;

public class UBIApplication extends Application {

    private SimWifiP2pBroadcastReceiver mReceiver;
    private SimWifiP2pManager.Channel mChannel = null;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private boolean mBound = false;

    public SimWifiP2pSocketServer getServer() {
        return mSrvSocket;
    }

    public void setServer(SimWifiP2pSocketServer s) {
        mSrvSocket = s;
    }

    public SimWifiP2pSocket getSocket() {
        return mCliSocket;
    }

    public void setSocket(SimWifiP2pSocket s) {
        mCliSocket = s;
    }

    public boolean getBound() {
        return mBound;
    }

    public void setBound(boolean b) {
        mBound = b;
    }

    public SimWifiP2pBroadcastReceiver getReceiver() {
        return mReceiver;
    }

    public void setReceiver(SimWifiP2pBroadcastReceiver mr) {
        mReceiver = mr;
    }

    public SimWifiP2pManager.Channel getChannel() {
        return mChannel;
    }

    public void setChannel(SimWifiP2pManager.Channel mc) {
        mChannel = mc;
    }

    public SimWifiP2pManager getManager() {
        return mManager;
    }

    public void setManager(SimWifiP2pManager m) {
        mManager = m;
    }
}