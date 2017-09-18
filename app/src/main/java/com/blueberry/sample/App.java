package com.blueberry.sample;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.blueberry.crash.CrashHandler;
import com.blueberry.crash.GlobalErrorHandler;

/**
 * Created by blueberry on 9/18/2017.
 */

public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance()
                .initialize(this)
                .isWriteLocal(true)
                .isDebug(true)
                .setDebugErrorHandler(new GlobalErrorHandler() {
                    @Override
                    public void handlerError(String header, Throwable throwable) {
                        Log.i(TAG, "handlerError: " + header);
                        throwable.printStackTrace();

                        Intent intent = new Intent(App.this, MainActivity.class);
                        startActivity(intent);
                        System.exit(0);
                    }
                })
                .start();
    }
}
