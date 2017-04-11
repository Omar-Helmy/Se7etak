package com.example.omar.healthcare;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView chatsList;
    private ChatAdapter chatAdapter;
    private ImageButton sendBtn;
    private EditText chatMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatsList = (ListView) findViewById(R.id.chats_list);
        sendBtn = (ImageButton) findViewById(R.id.chat_send);
        chatMsg = (EditText) findViewById(R.id.chat_input);
        getLoaderManager().initLoader(0,null,this);
        chatAdapter = new ChatAdapter(this,null,0);
        chatsList.setAdapter(chatAdapter);
        chatsList.smoothScrollByOffset(1);
        //setListenerOnChatList();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = Long.toString(System.currentTimeMillis());
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("ts", time);
                map.put("msg",chatMsg.getText().toString());
                map.put("dir",0);
                //Se7etak.chatNode.child(time).setValue(map);
                Se7etak.chatNode.push().setValue(map);
                chatMsg.setText("");
                chatsList.smoothScrollToPosition(chatsList.getCount()-1);
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this,Se7etakProvider.URI_CHAT,null,null,null,null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        chatAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        chatAdapter.swapCursor(cursor);
    }


    /**************************************************/
    private class ChatAdapter extends CursorAdapter{


        public ChatAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

            return LayoutInflater.from(context).inflate(R.layout.chat_item_in, viewGroup, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView chatMsg = (TextView) view.findViewById(R.id.chat_txt);
            TextView chatTime = (TextView) view.findViewById(R.id.chat_time);
            LinearLayout chatItem = (LinearLayout) view.findViewById(R.id.chat_item);
            chatMsg.setText(cursor.getString(cursor.getColumnIndex("msg")));
            chatTime.setText(new Timestamp(Long.parseLong(cursor.getString(cursor.getColumnIndex("ts")))).toString().substring(0,16));
            if(cursor.getInt(cursor.getColumnIndex("dir"))==0) {
                chatMsg.setBackgroundColor(getResources().getColor(R.color.Green));
                chatItem.setGravity(Gravity.RIGHT);
            }

        }
    }

}
