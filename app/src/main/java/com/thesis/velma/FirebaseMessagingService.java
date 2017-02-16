package com.thesis.velma;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;


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


        LandingActivity.db.saveEvent(remoteMessage.getData().get("userid"),Long.parseLong(remoteMessage.getData().get("eventid")),
                remoteMessage.getData().get("eventname"), remoteMessage.getData().get("eventDescription"), remoteMessage.getData().get("eventLocation")
                , remoteMessage.getData().get("eventStartDate"), remoteMessage.getData().get("eventStartTime"),
                remoteMessage.getData().get("eventEndDate"), remoteMessage.getData().get("eventEndTime"), remoteMessage.getData().get("notify"),
                remoteMessage.getData().get("invitedfirends"));

        String[] mydates = remoteMessage.getData().get("eventStartDate").split("-");
        String[] mytimes = remoteMessage.getData().get("eventStartTime").split(":");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(remoteMessage.getData().get("eventname"))
                .setContentText(remoteMessage.getData().get("text"))
                .setSmallIcon(R.drawable.hair)
                .setContentIntent(pendingIntent);

        //HARDCODED VALUES 10:51
        Calendar calNow = Calendar.getInstance();
        Calendar calSet = (Calendar) calNow.clone();


        int AM_PM;
        if (Integer.parseInt(mytimes[0]) < 12) {
            AM_PM = 0;
        } else {
            AM_PM = 1;
        }

//        calSet.set(Calendar.YEAR, Integer.parseInt(mydates[2]));
//        calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1])-1);
//        calSet.set(Calendar.DATE, Integer.parseInt(mydates[0]));
//        calSet.set(Calendar.HOUR, Integer.parseInt(mytimes[0]));
//        calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
//        calSet.set(Calendar.SECOND, 0);
//        calSet.set(Calendar.MILLISECOND, 0);
//        calSet.set(Calendar.AM_PM, AM_PM);

        calSet.clear();
        calSet.set(Integer.parseInt(mydates[2]),Integer.parseInt(mydates[1])-1,Integer.parseInt(mydates[0]),Integer.parseInt(mytimes[0]),Integer.parseInt(mytimes[1]));


        PendingIntent mypendingintent;
        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        myIntent.putExtra("name", remoteMessage.getData().get("eventname"));
        mypendingintent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), mypendingintent);

//        Calendar myAlarmDate = Calendar.getInstance();
//        myAlarmDate.setTimeInMillis(System.currentTimeMillis());
//        //myAlarmDate.set(Integer.parseInt(mydates[1]), 11, 25, 12, 00, 0);
//        myAlarmDate.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]), Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]), 0);
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//
//        Intent _myIntent = new Intent(getApplicationContext(), Alarm_Receiver.class);
//        _myIntent.putExtra("MyMessage", name);
//        PendingIntent _myPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 123, _myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), _myPendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }


}
