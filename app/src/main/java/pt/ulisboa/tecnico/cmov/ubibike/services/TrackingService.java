package pt.ulisboa.tecnico.cmov.ubibike.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.ulisboa.tecnico.cmov.ubibike.MainActivity;
import pt.ulisboa.tecnico.cmov.ubibike.objects.SimWifiP2pBroadcastReceiver;


public class TrackingService extends Service {
    private static final String EXTRA_WIFI = "pt.ulisboa.tecnico.cmov.ubibike.WIFI";

    private static Timer timer = new Timer();
    private Context mContext;
    private boolean isWiFiON;

    public TrackingService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    public void onCreate() {
        super.onCreate();
        mContext = this;
        startService();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return flags;
    }

    @Override
    public void onDestroy() {
        stopSelf();
    }


    private void startService() {
        timer.scheduleAtFixedRate(new mainTask(), 0, 2000);
    }

    private class mainTask extends TimerTask {

        @Override
        public void run() {

            if(TermiteService.getInstance().isWiFiON())
                Log.d("TRACKING", "......................");
                System.out.println("Bike: "+TermiteService.getInstance().getPeers());
            Log.d("TRACKING", "......................");
        }
    }
}
