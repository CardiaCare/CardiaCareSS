package ru.cardiacare.cardiacare.ecgviewer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/* This class contains common data and methods for all of the supported ECG signal sources */

abstract public class SignalSource {

    private static enum States {
        STATE_DISCONNECTED,
        STATE_CONNECTING,
        STATE_CONNECTED,
        STATE_DISCONNECTING
    }

    private States mState;
    private final Handler mRemoteHandler;
    private final Handler mLocalHandler;

    public SignalSource(Context context, Handler handler) {
        mState = States.STATE_DISCONNECTED;
        mRemoteHandler = handler;
        mLocalHandler = new Handler() {
            public void handleMessage(Message msg) {
                processMessage(msg);
            }
        };
    }

    public Handler getHandler() {
        return mLocalHandler;
    }

    private int processMessage(Message msg) {
        return 0;
    }

    public synchronized States getState() {
        return mState;
    }

    private synchronized void setState(States state) {
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
//        mRemoteHandler.obtainMessage(BluetoothFindActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized void connect(Bundle config) {
    }

    public synchronized void disconnect(Bundle config) {
    }

    private static class ConnectionThread extends Thread {
    }

    private static class WorkerThread extends Thread {
    }
}
