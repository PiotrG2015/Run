package pl.piotrgorczyca.myrunnerapp;

import android.app.Application;

import com.android.volley.RequestQueue;

/**
 * Created by Piotr on 2015-12-26. Enjoy!
 */
public class RunnerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Creating Request Queue
        RequestQueue queue = MySingleton.getInstance(this).
                getRequestQueue();
    }
}
