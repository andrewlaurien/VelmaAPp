package com.thesis.velma;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class acceptEvent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle b = getIntent().getExtras();

        LandingActivity.db.saveEvent(b.getString("userid"), b.getLong("eventid"),
                b.getString("eventname"), b.getString("eventDescription"), b.getString("eventLocation")
                , b.getString("eventStartDate"), b.getString("eventStartTime"),
                b.getString("eventEndDate"), b.getString("eventEndTime"), b.getString("notify"),
                b.getString("invitedfirends"));


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

    }

}
