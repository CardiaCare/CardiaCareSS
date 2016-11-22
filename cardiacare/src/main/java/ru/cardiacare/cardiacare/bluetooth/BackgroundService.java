package ru.cardiacare.cardiacare.bluetooth;

import android.app.IntentService;
import android.content.Intent;

/* This class represents a service running in background and responsible for messaging between signal source and consumers */

public class BackgroundService extends IntentService {

    private static BackgroundService serviceInstance = null;

    public static BackgroundService getInstance() {

        if (serviceInstance == null) {
            new BackgroundService();
        }
        return serviceInstance;
    }

    private BackgroundService() {
        super("CardiaCare Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }
}
