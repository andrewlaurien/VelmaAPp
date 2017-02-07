package com.thesis.velma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("Notification1", "" + remoteMessage.getData());
        // showNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("sender"));
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {

        Intent i = new Intent(this, LandingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);


        LandingActivity.db.saveEvent(Long.parseLong(remoteMessage.getData().get("eventid")),
                remoteMessage.getData().get("eventname"), remoteMessage.getData().get("eventDescription"), remoteMessage.getData().get("eventLocation")
                , remoteMessage.getData().get("eventStartDate"), remoteMessage.getData().get("eventStartTime"),
                remoteMessage.getData().get("eventEndDate"), remoteMessage.getData().get("eventEndTime"), remoteMessage.getData().get("notify"),
                remoteMessage.getData().get("invitedfirends"));


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(remoteMessage.getData().get("eventname"))
                .setContentText(remoteMessage.getData().get("text"))
                .setSmallIcon(R.drawable.hair)
                .setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }


}
