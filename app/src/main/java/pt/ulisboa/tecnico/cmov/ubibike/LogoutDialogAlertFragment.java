package pt.ulisboa.tecnico.cmov.ubibike;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class LogoutDialogAlertFragment extends DialogFragment {

    public static LogoutDialogAlertFragment newInstance() {
        LogoutDialogAlertFragment frag = new LogoutDialogAlertFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_dialog_alert)
                .setTitle(R.string.logout)
                .setMessage(R.string.dialog_logout)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity) getActivity()).doYesClick();
                            }
                        }
                )
                .setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity) getActivity()).doNoClick();
                            }
                        }
                )
                .create();

    }
}