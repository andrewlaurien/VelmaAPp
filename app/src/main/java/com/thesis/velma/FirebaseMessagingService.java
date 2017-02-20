package com.thesis.velma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
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


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent i = new Intent(this, LandingActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        switch (remoteMessage.getData().get("title")) {

            case "Invitation":


                Intent detailsIntent1 = new Intent(this, acceptEvent.class);
                Bundle b = new Bundle();
                b.putString("userid", remoteMessage.getData().get("userid"));
                b.putLong("eventid", Long.parseLong(remoteMessage.getData().get("eventid")));
                b.putString("eventname", remoteMessage.getData().get("eventname"));
                b.putString("eventDescription", remoteMessage.getData().get("eventDescription"));
                b.putString("eventLocation", remoteMessage.getData().get("eventLocation"));
                b.putString("eventStartDate", remoteMessage.getData().get("eventStartDate"));
                b.putString("eventStartTime", remoteMessage.getData().get("eventStartTime"));
                b.putString("eventEndDate", remoteMessage.getData().get("eventEndDate"));
                b.putString("eventEndTime", remoteMessage.getData().get("eventEndTime"));
                b.putString("notify", remoteMessage.getData().get("notify"));
                b.putString("invitedfirends", remoteMessage.getData().get("invitedfirends"));
                Intent detailsIntent2 = new Intent(this, declineEvent.class);

//        LandingActivity.db.saveEvent(remoteMessage.getData().get("userid"), Long.parseLong(remoteMessage.getData().get("eventid")),
//                remoteMessage.getData().get("eventname"), remoteMessage.getData().get("eventDescription"), remoteMessage.getData().get("eventLocation")
//                , remoteMessage.getData().get("eventStartDate"), remoteMessage.getData().get("eventStartTime"),
//                remoteMessage.getData().get("eventEndDate"), remoteMessage.getData().get("eventEndTime"), remoteMessage.getData().get("notify"),
//                remoteMessage.getData().get("invitedfirends"));

                String[] mydates = remoteMessage.getData().get("eventStartDate").split("-");
                String[] mytimes = remoteMessage.getData().get("eventStartTime").split(":");

                builder = new NotificationCompat.Builder(this)
                        .addAction(R.drawable.ic_add_circle_black_24dp, "Accept", PendingIntent.getActivity(this, 0111, detailsIntent1, 0)) // #0
                        .addAction(R.drawable.ic_delete_black_24dp, "Decline", PendingIntent.getActivity(this, 0111, detailsIntent2, 0))  // #1
                        .setAutoCancel(true)
                        .setContentTitle(remoteMessage.getData().get("eventname"))
                        .setContentText(remoteMessage.getData().get("text"))
                        .setSmallIcon(R.drawable.hair);

                //HARDCODED VALUES 10:51
//        Calendar calNow = Calendar.getInstance();
//        Calendar calSet = (Calendar) calNow.clone();


//        int AM_PM;
//        if (Integer.parseInt(mytimes[0]) < 12) {
//            AM_PM = 0;
//        } else {
//            AM_PM = 1;
//        }

//        calSet.set(Calendar.YEAR, Integer.parseInt(mydates[2]));
//        calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1])-1);
//        calSet.set(Calendar.DATE, Integer.parseInt(mydates[0]));
//        calSet.set(Calendar.HOUR, Integer.parseInt(mytimes[0]));
//        calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
//        calSet.set(Calendar.SECOND, 0);
//        calSet.set(Calendar.MILLISECOND, 0);
//        calSet.set(Calendar.AM_PM, AM_PM);

//        calSet.clear();
//        calSet.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]) - 1, Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]));


//        PendingIntent mypendingintent;
//        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
//        myIntent.putExtra("name", remoteMessage.getData().get("eventname"));
//        mypendingintent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), mypendingintent);


                manager.notify(0, builder.build());


                break;


            case "DeleteEvent":

                builder = new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setContentTitle(remoteMessage.getData().get("eventname"))
                        .setContentText("Event is Deletd")
                        .setSmallIcon(R.drawable.hair);


                LandingActivity.db.deleteEvent(Long.parseLong(remoteMessage.getData().get("eventid")));

                manager.notify(0, builder.build());


                break;


        }


    }


}
