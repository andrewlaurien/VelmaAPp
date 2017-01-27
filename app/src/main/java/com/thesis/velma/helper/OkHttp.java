package com.thesis.velma.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.thesis.velma.LandingActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
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

    public void saveProfile(String userid, String useremail, String name) {

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
                    // Run view-related code back on the main thread
                    if (responseData.equalsIgnoreCase("Records added successfully.")) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mcontext, LandingActivity.class);
                                mcontext.startActivity(intent);
                                ((Activity) mcontext).finish();
                            }
                        });
                    }
                    if (responseData.equalsIgnoreCase("User Already in Exists")) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mcontext, LandingActivity.class);
                                mcontext.startActivity(intent);
                                ((Activity) mcontext).finish();
                            }
                        });
                    }
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

}