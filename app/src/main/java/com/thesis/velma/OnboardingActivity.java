package com.thesis.velma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.thesis.velma.helper.OkHttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.thesis.velma.OnboardingFragment2.dateEnd;
import static com.thesis.velma.OnboardingFragment2.dateStart;
import static com.thesis.velma.OnboardingFragment2.timeStart;

/**
 * Created by jeanneviegarciano on 8/10/2016.
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager pager;
    private SmartTabLayout indicator;
    public Button skip;
    public Button BtnAddEvent;
    public EditText event;
    Context context;

    public static TextView des;
    public static EditText descrip;
    public static TextView loc;
    public static TextView locate;
    public static TextView distanceduration;
    int PLACE_PICKER_REQUEST = 1;
    Double latitude, longtiude;

    public static String geolocation;
    String modetravel = "driving";
    PlaceAutocompleteFragment autocompleteFragment;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        context = this;

        event = (EditText) findViewById(R.id.eventname);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (SmartTabLayout) findViewById(R.id.indicator);
        BtnAddEvent = (Button) findViewById(R.id.btnAddEvent);

        BtnAddEvent.setVisibility(View.GONE);

        des = (TextView) findViewById(R.id.description);
        descrip = (EditText) findViewById(R.id.descriptionText);
        loc = (TextView) findViewById(R.id.location);
        locate = (TextView) findViewById(R.id.locationText);
        locate.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        distanceduration = (TextView) findViewById(R.id.distanceduration);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(14.599512, 120.984222),
                new LatLng(14.599512, 120.984222)));


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress());//get place details here

                locate.setText("" + place.getAddress());
                geolocation = place.getAddress().toString();

                latitude = place.getLatLng().latitude;
                longtiude = place.getLatLng().longitude;


                Log.d("latlang", "" + latitude + ":" + longtiude);

                new getDetails().execute();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
//                    case 0:
//                        return new OnboardingFragment1();
                    case 0:
                        return new OnboardingFragment2();
                    case 1:
                        return new OnboardingFragment3();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    BtnAddEvent.setVisibility(View.GONE);
                } else if (position == 1) {
                    BtnAddEvent.setVisibility(View.VISIBLE);
                }

            }

        });


        BtnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Random r = new Random();
                long unixtime = (long) (1293861599 + r.nextDouble() * 60 * 60 * 24 * 365);


                final String name = event.getText().toString();
                final String eventDescription = descrip.getText().toString();
                final String eventLocation = locate.getText().toString();
                final String startDate = dateStart.getText().toString();
                final String endDate = dateEnd.getText().toString();
                final String startTime = timeStart.getText().toString();
                final String endTime = OnboardingFragment2.timeEnd.getText().toString();
                final String notify = OnboardingFragment2.alarming.getText().toString();
                final String invitedContacts = OnboardingFragment3.mtxtinvited.getText().toString();

                Log.d("StarTime", startDate + " " + startTime);
                Log.d("EndTime", endDate + " " + endTime);


                String[] mydates = startDate.split("-");
                String[] mytimes = startTime.split(":");


                //HARDCODED VALUES 10:51
                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();


                Log.d("Calendar.YEAR", "" + Integer.parseInt(mydates[2]));
                Log.d("Calendar.MONTH", "" + Integer.parseInt(mydates[1]));
                Log.d("Calendar.DATE", "" + Integer.parseInt(mydates[0]));
                Log.d("Calendar.HOUR_OF_DAY", "" + Integer.parseInt(mytimes[0]));
                Log.d("Calendar.MINUTE", "" + Integer.parseInt(mytimes[1]));

                int AM_PM;
                if (Integer.parseInt(mytimes[0]) < 12) {
                    AM_PM = 0;
                } else {
                    AM_PM = 1;
                }

//                calSet.set(Calendar.YEAR, Integer.parseInt(mydates[2]));
//                calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1])-1);
//                calSet.set(Calendar.DATE, Integer.parseInt(mydates[0]));
//                calSet.set(Calendar.HOUR, Integer.parseInt(mytimes[0]));
//                calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
//                calSet.set(Calendar.SECOND, 0);
//                calSet.set(Calendar.MILLISECOND, 0);
//                calSet.set(Calendar.AM_PM, AM_PM);

                calSet.setTimeInMillis(System.currentTimeMillis());
                calSet.clear();
                calSet.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]) - 1, Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]));
              

                Intent myIntent = new Intent(context, AlarmReceiver.class);
                myIntent.putExtra("name", name);
                pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);


                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);


//                Calendar myAlarmDate = Calendar.getInstance();
//                myAlarmDate.setTimeInMillis(System.currentTimeMillis());
//                //myAlarmDate.set(Integer.parseInt(mydates[1]), 11, 25, 12, 00, 0);
//                myAlarmDate.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]), Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]), 0);
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                Intent _myIntent = new Intent(context, Alarm_Receiver.class);
//                _myIntent.putExtra("MyMessage", name);
//                PendingIntent _myPendingIntent = PendingIntent.getBroadcast(context, 123, _myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), _myPendingIntent);


                if (name.isEmpty()) {
                    Toast.makeText(context, "Invalid Event Name", Toast.LENGTH_SHORT).show();
                } else if (eventDescription.isEmpty()) {
                    Toast.makeText(context, "Invalid Event Description", Toast.LENGTH_SHORT).show();
                } else if (startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(context, "Please add Starting date and completion date.", Toast.LENGTH_SHORT).show();
                } else {

                    LandingActivity.db.saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts);
                    OkHttp.getInstance(getBaseContext()).saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts);

                    for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {

                        String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");

                        OkHttp.getInstance(context).sendNotification(unixtime, name, eventDescription, eventLocation,
                                startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma");

                    }

                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();


                }


            }
        });


    }

    class getDetails extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0)
                    out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {

            String text = null;
            String coordinates = latitude + "," + longtiude;
            Log.d("Coordinates", coordinates);
            try {
                String regAPIURL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + LandingActivity.origlatitude + "," + LandingActivity.origlongitude;
                regAPIURL = regAPIURL + "&destinations=" + URLEncoder.encode(coordinates);
                regAPIURL = regAPIURL + "&mode=" + URLEncoder.encode(modetravel);
                regAPIURL = regAPIURL + "&key=AIzaSyDWjoAbJf9uDrLCFAM_fCSWxP0muVEGbOA";
                Log.d("URI", regAPIURL);
                HttpGet httpGet = new HttpGet(regAPIURL);
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 60000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 60000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                text = null;
            }

            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Result", s);


            if (s != null) {


                try {


                    String distance = new JSONObject(s)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("distance").getString("text");

                    String duration = new JSONObject(s)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("duration").getString("text");

                    distanceduration.setText("Distance : " + distance + ": Duration : " + duration);
                    distanceduration.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }


}
