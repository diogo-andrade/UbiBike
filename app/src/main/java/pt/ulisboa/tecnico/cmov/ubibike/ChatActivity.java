package pt.ulisboa.tecnico.cmov.ubibike;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import pt.ulisboa.tecnico.cmov.ubibike.adapters.ChatArrayAdapter;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private static final String KEY_TEXT_VALUE = "chatText";
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("pt.ulisboa.tecnico.cmov.ubibike.NAME");
            getSupportActionBar().setTitle(name);
        }

        buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        if (savedInstanceState != null) {
            List<ChatMessage> list = (List<ChatMessage>)savedInstanceState.get("myList");
            if (list != null) {
                for(ChatMessage c : list){
                    chatArrayAdapter.add(c);
                }
            }
        }

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("DEBUG","vou guardar tudo!");
        List<ChatMessage> list = chatArrayAdapter.getChatMessageList();
        outState.putSerializable ("myList", (Serializable)list);
    }



    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        side = !side;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
