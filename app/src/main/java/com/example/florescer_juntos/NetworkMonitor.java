package com.example.florescer_juntos;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.florescer_juntos.View.SplashActivity;

public class NetworkMonitor extends Thread {
    private static final long CHECK_INTERVAL = 10000; // 10 segundos
    private boolean running = true;
    private boolean isOffline = true;
    private Context context;

    public NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void run() {
        while (running) {
            if (isConnectedToInternet()) {
                // Se estiver conectado, não faz nada
                if(isOffline){
                    this.isOffline=false;
                    Intent intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else {
                // Se não estiver conectado,
                if(!isOffline){
                    this.isOffline=true;
                    Intent intent = new Intent(context, SplashActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopMonitoring() {
        running = false;
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
