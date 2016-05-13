
        package pt.ulisboa.tecnico.cmov.ubibike;

        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.ServiceConnection;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.os.Messenger;
        import android.support.v4.app.Fragment;
        import android.util.Log;
        import android.view.LayoutInflater;
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
        import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
        import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
        import pt.ulisboa.tecnico.cmov.ubibike.services.TermiteService;

        /**
 * Created by gae on 10/05/2016.
 */
public class WifiFragment extends Fragment{

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

            getView().findViewById(R.id.idWifiOffButton).setEnabled(true);

            v.findViewById(R.id.idWifiOnButton).setEnabled(false);

            new TermiteService();
            getActivity().startService(new Intent(getActivity(),TermiteService.class));

        }
    };

    private View.OnClickListener listenerWifiOffButton = new View.OnClickListener() {
        public void onClick(View v){
                getView().findViewById(R.id.idWifiOnButton).setEnabled(true);
                v.findViewById(R.id.idWifiOffButton).setEnabled(false);
                TermiteService.getInstance().onDisconect();
            }
    };
}