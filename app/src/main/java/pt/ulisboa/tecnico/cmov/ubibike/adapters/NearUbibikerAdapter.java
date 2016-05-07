package pt.ulisboa.tecnico.cmov.ubibike.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.ubibike.ChatActivity;
import pt.ulisboa.tecnico.cmov.ubibike.R;
import pt.ulisboa.tecnico.cmov.ubibike.objects.Ubibiker;

/**
 * Created by diogo on 29-04-2016.
 */
public class NearUbibikerAdapter extends ArrayAdapter {

    private ArrayList<Ubibiker> mObjects;
    private Context mContext;
    private int mResourceView;
    private String xtra;
    private String m_Text = "";

    public NearUbibikerAdapter (Context context, int resource, String extra, ArrayList<Ubibiker> objects) {
        super(context, resource ,objects);
        this.mObjects = objects;
        this.mContext = context;
        this.mResourceView = resource;
        this.xtra =  extra;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResourceView, null);
        }

        Ubibiker ubibiker = mObjects.get(position);

        // Check if list is empty
        if (ubibiker != null) {
            TextView name = (TextView) convertView.findViewById(R.id.ubibikerName);
            TextView email = (TextView) convertView.findViewById(R.id.ubibikerEmail);

            name.setText(ubibiker.getName());
            email.setText(ubibiker.getEmail());
        }
        final RelativeLayout relativeclic = (RelativeLayout) convertView.findViewById(R.id.near_ubibiker_item);
        final Button btt = (Button) convertView.findViewById(R.id.share_points_btn);
        relativeclic.setOnClickListener(rlClickListener);
        btt.setOnClickListener(bttClickLinestener);

        return convertView;
    }

    private View.OnClickListener rlClickListener = new View.OnClickListener()

    {
        @Override
        public void onClick(View v) {
            TextView vName = (TextView) v.findViewById(R.id.ubibikerName);
            TextView vEmail = (TextView) v.findViewById(R.id.ubibikerEmail);
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra(xtra, vName.getText().toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    };

    private View.OnClickListener bttClickLinestener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Activity) v.getContext());
            builder.setTitle("Share Points");

            // Set up the input
            final EditText input = new EditText(getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    };
}