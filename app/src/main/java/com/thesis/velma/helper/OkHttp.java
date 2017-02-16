package com.thesis.velma.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.thesis.velma.LandingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by andrewlaurienrsocia on 25/01/2017.
 */
public class OkHttp {

    public static Context mcontext;
    private static OkHttp parser;
    OkHttpClient client;
    Handler mainHandler;
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";

    MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public OkHttp(Context context) {
        this.mcontext = context;
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(mcontext.getApplicationContext().getCacheDir(), "cacheFileName"), cacheSize);
        client = new OkHttpClient.Builder().cache(cache).build();
        mainHandler = new Handler(mcontext.getMainLooper());
    }

    public static synchronized OkHttp getInstance(Context c) {
        mcontext = c;
        if (parser == null) {
            parser = new OkHttp(c);
        }
        return parser;
    }

    public void saveProfile(final String userid, final String useremail, String name) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://velma.000webhostapp.com/users.php").newBuilder();
        urlBuilder.addQueryParameter("userid", userid);
        urlBuilder.addQueryParameter("useremail", useremail);
        urlBuilder.addQueryParameter("name", name);
        String Url = urlBuilder.build().toString();

        Log.d("URL", Url);

        Request request = new Request.Builder()
                .url(Url)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding
                // Read data on the worker thread
                final String responseData = response.body().string();
                Log.d("Data", responseData);
                if (response.code() == 200) {


                    fetchEvents(userid, useremail);

                    // Run view-related code back on the main thread
                    if (responseData.equalsIgnoreCase("Records added successfully.")) {

//                        mainHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(mcontext, LandingActivity.class);
//                                mcontext.startActivity(intent);
//                                ((Activity) mcontext).finish();
//                            }
//                        });
                    }
                    if (responseData.equalsIgnoreCase("User Already in Exists")) {
//                        mainHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(mcontext, LandingActivity.class);
//                                mcontext.startActivity(intent);
//                                ((Activity) mcontext).finish();
//                            }
//                        });
                    }
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext, "Failed to register", Toast.LENGTH_SHORT).show();
                        }
                    });


                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
                    prefs.edit().putBoolean("isLoggedIn", false).commit();

                }
            }
        });
    }

    public void fetchEvents(String userid, String useremail) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://velma.000webhostapp.com/fetch_velma_events.php").newBuilder();
        urlBuilder.addQueryParameter("userid", userid);
        urlBuilder.addQueryParameter("useremail", useremail);
        String Url = urlBuilder.build().toString();

        Log.d("URL", Url);

        Request request = new Request.Builder()
                .url(Url)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding
                // Read data on the worker thread
                final String responseData = response.body().string();
                Log.d("DataReturn", responseData);
                if (response.code() == 200) {


                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyy");//yyyy-MM-dd HH:mm:ss
                    String startdate[];
                    String endate[];

                    //7-2-20172

                    try {
                        JSONArray myarray = new JSONArray(responseData);


                        for (int i = 0; i < myarray.length(); i++) {
                            JSONObject myobject = myarray.getJSONObject(i);
                            Log.d("myData", myobject.getString("EventID"));

                            startdate = myobject.getString("StartDate").split("-");
                            endate = myobject.getString("EndDate").split("-");

                            LandingActivity.db.saveEvent(myobject.getString("UserID"), myobject.getLong("EventID"),
                                    myobject.getString("EventName"), myobject.getString("EventDescription"),
                                    myobject.getString("EventLocation"), startdate[2] + "-" + startdate[1] + "-" + startdate[0],
                                    myobject.getString("StartTime"), endate[2] + "-" + endate[1] + "-" + endate[0],
                                    myobject.getString("EndTime"), "", myobject.getString("InvitedFriends"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Run view-related code back on the main thread

                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext, "Failed to register", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

    }

    public void saveEvent(Long eventid, String eventname, String eventDescription, String eventLocation,
                          String eventStartDate, String eventStartTime, String eventEndDate,
                          String eventEndTime, String notify, String invitedfirends) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://velma.000webhostapp.com/add_velma_events.php").newBuilder();
        urlBuilder.addQueryParameter("userid", "" + LandingActivity.imei);
        urlBuilder.addQueryParameter("eventid", "" + eventid);
        urlBuilder.addQueryParameter("eventname", eventname);
        urlBuilder.addQueryParameter("eventDescription", eventDescription);
        urlBuilder.addQueryParameter("eventLocation", eventLocation);
        urlBuilder.addQueryParameter("eventStartDate", eventStartDate);
        urlBuilder.addQueryParameter("eventStartTime", eventStartTime);
        urlBuilder.addQueryParameter("eventEndDate", eventEndDate);
        urlBuilder.addQueryParameter("eventEndTime", eventEndTime);
        urlBuilder.addQueryParameter("notify", notify);
        urlBuilder.addQueryParameter("invitedfirends", invitedfirends);

        String Url = urlBuilder.build().toString();

        Log.d("URL", Url);

        Request request = new Request.Builder()
                .url(Url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding
                // Read data on the worker thread
                final String responseData = response.body().string();
                Log.d("Data", responseData);
                if (response.code() == 200) {
                    // Run view-related code back on the main thread

                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext, "Faile to register", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


    }

    public void sendNotification(Long eventid, String eventname, String eventDescription, String eventLocation,
                                 String eventStartDate, String eventStartTime, String eventEndDate,
                                 String eventEndTime, String notify, String invitedfirends, String target) {


        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://velma.000webhostapp.com/sendNotification.php").newBuilder();
        urlBuilder.addQueryParameter("userid", "" + LandingActivity.imei);
        urlBuilder.addQueryParameter("eventid", "" + eventid);
        urlBuilder.addQueryParameter("eventname", eventname);
        urlBuilder.addQueryParameter("eventDescription", eventDescription);
        urlBuilder.addQueryParameter("eventLocation", eventLocation);
        urlBuilder.addQueryParameter("eventStartDate", eventStartDate);
        urlBuilder.addQueryParameter("eventStartTime", eventStartTime);
        urlBuilder.addQueryParameter("eventEndDate", eventEndDate);
        urlBuilder.addQueryParameter("eventEndTime", eventEndTime);
        urlBuilder.addQueryParameter("notify", notify);
        urlBuilder.addQueryParameter("invitedfirends", invitedfirends);
        urlBuilder.addQueryParameter("target", target);
        urlBuilder.addQueryParameter("name", LandingActivity.profilename);

        String Url = urlBuilder.build().toString();

        Log.d("URL", Url);

        Request request = new Request.Builder()
                .url(Url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding
                // Read data on the worker thread
                final String responseData = response.body().string();
                Log.d("Data", responseData);
                if (response.code() == 200) {
                    // Run view-related code back on the main thread

                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext, "Faile to register", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }
        });


    }


    public void deleteEvent(String eventID) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://velma.000webhostapp.com/delete_velma_events.php").newBuilder();
        urlBuilder.addQueryParameter("eventid", eventID);

        String Url = urlBuilder.build().toString();

        Log.d("URL", Url);

        Request request = new Request.Builder()
                .url(Url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // ... check for failure using `isSuccessful` before proceeding
                // Read data on the worker thread
                final String responseData = response.body().string();
                Log.d("Data", responseData);
                if (response.code() == 200) {
                    // Run view-related code back on the main thread
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext, "Event successfully deleted", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mcontext, "Failed to register", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


//    public void sendMessage(JSONObject robject) {
//
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://fcm.googleapis.com/fcm/send").newBuilder();
//        urlBuilder.addQueryParameter("Authorization", "AIzaSyDKV5ZLw_4bT2CUit-J567KbZhzIql7h-I");
//        urlBuilder.addQueryParameter("data", robject.toString());
//        String url = urlBuilder.build().toString();
//
//        Request request = new Request.Builder()
//                .url(url)
//                .method("POST", RequestBody.create(null, new byte[0]))
//                .build();
//
//        Log.d("URL", request.toString());
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                Log.d("URL", "Fail");
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//            }
//        });
//
//
//    }


}