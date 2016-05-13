
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
        import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

        /**
 * Created by gae on 10/05/2016.
 */
public class WifiFragment extends Fragment{

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
            ((UBIApplication)getActivity().getApplication()).setBound(true);

            getView().findViewById(R.id.idWifiOffButton).setEnabled(true);

            v.findViewById(R.id.idWifiOnButton).setEnabled(false);

         new IncommingCommTask().executeOnExecutor(
                 AsyncTask.THREAD_POOL_EXECUTOR);


        }
    };

    private View.OnClickListener listenerWifiOffButton = new View.OnClickListener() {
        public void onClick(View v){
            if (((UBIApplication) getActivity().getApplication()).getBound()) {
                getActivity().unbindService(mConnection);
                ((UBIApplication) getActivity().getApplication()).setBound(false);
                getView().findViewById(R.id.idWifiOnButton).setEnabled(true);
                v.findViewById(R.id.idWifiOffButton).setEnabled(false);
            }
        }
    };


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            ((UBIApplication) getActivity().getApplication()).setManager(new SimWifiP2pManager(new Messenger(service)));
            ((UBIApplication) getActivity().getApplication()).setChannel(((UBIApplication) getActivity().getApplication()).getManager().initialize(getActivity().getApplication(), getActivity().getMainLooper(), null));
            ((UBIApplication) getActivity().getApplication()).setBound(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            ((UBIApplication) getActivity().getApplication()).setManager(null);
            ((UBIApplication) getActivity().getApplication()).setChannel(null);
            ((UBIApplication) getActivity().getApplication()).setBound(false);
        }
    };

            public  class IncommingCommTask extends AsyncTask<Void, String, Void> {

                @Override
                protected Void doInBackground(Void... params) {

                    Log.d("YO", "IncommingCommTask started (" + this.hashCode() + ").");

                    try {
                        ((UBIApplication) getActivity().getApplication()).setServer(new SimWifiP2pSocketServer(
                                Integer.parseInt(getString(R.string.port))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            SimWifiP2pSocket sock = ((UBIApplication) getActivity().getApplication()).getServer().accept();
                            try {
                                BufferedReader sockIn = new BufferedReader(
                                        new InputStreamReader(sock.getInputStream()));
                                String st = sockIn.readLine();
                                Log.d("DEBUG", st);
                                publishProgress(st);
                                if (st.equals("Request")) {
                                    Log.d("DEBUG", "oi oi oi oioi");
                                    Toast.makeText(getContext(), "oiii", Toast.LENGTH_LONG).show();
                                } else if (st.startsWith("InfoProfile")) {
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
            }
}