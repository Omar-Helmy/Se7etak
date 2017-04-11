package com.example.omar.healthcare;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.support.v4.app.NotificationCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

public class ChatService extends IntentService {

    public static boolean FIRST_TIME = true;
    private String timeStamp;
    private Cursor cursor;
    // Gets an instance of the NotificationManager service
    public static NotificationManager mNotifyMgr;
    // Sets an ID for the notification
    public final static int notificationID = 1;
    // builder
    public static NotificationCompat.Builder mBuilder;

    public ChatService() {
        super("ChatService");
    }

    /**
     * I start this service from main activity
     **/
    @Override
    protected void onHandleIntent(Intent intent) {

        FIRST_TIME = false;
        String[] cols = {"ts"};
        cursor = getContentResolver().query(Se7etakProvider.URI_CHAT.buildUpon().appendPath("string").build(), cols, "ts > 1", null, "ts DESC");
        try {
            cursor.moveToFirst();
            timeStamp = cursor.getString(cursor.getColumnIndex("ts"));
        } catch (CursorIndexOutOfBoundsException e) {
            timeStamp = "0";
            e.printStackTrace();
        }

        if(Se7etak.logged)setListenerOnChatList(timeStamp);
    }


    private void setListenerOnChatList(String ts) {

        String timeStamp = String.valueOf(Long.parseLong(ts) + 1);
        Se7etak.chatNode.orderByChild("ts").startAt(timeStamp).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {

                String msg = snapshot.child("msg").getValue().toString();
                ContentValues contentValues = new ContentValues();
                contentValues.put("ts", snapshot.child("ts").getValue().toString());
                contentValues.put("msg", msg);
                contentValues.put("dir", Integer.parseInt(snapshot.child("dir").getValue().toString()));
                getContentResolver().insert(Se7etakProvider.URI_CHAT, contentValues);
                initiateNotification(msg);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
        });
    }

    private void initiateNotification(String msg) {
        // notification:
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.splash_logo)
                        .setContentTitle("New Message Received!")
                        .setContentText(msg);
        Intent resultIntent = new Intent(getBaseContext(), MainActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getBaseContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr.notify(notificationID, mBuilder.build());

    }
}
