package com.example.florescer_juntos;

import android.app.Application;

public class MyApplication extends Application {
    private NetworkMonitor networkMonitor;

    @Override
    public void onCreate() {
        super.onCreate();
        networkMonitor = new NetworkMonitor(this);
        networkMonitor.start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        networkMonitor.stopMonitoring();
    }
}