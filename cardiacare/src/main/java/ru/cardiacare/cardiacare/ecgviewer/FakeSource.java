package ru.cardiacare.cardiacare.ecgviewer;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;

import ru.cardiacare.cardiacare.R;

/* This class contains a fake ECG signal source for testing purposes */

public class FakeSource extends SignalSource {

    private final int[] mDemoSignal;

    public FakeSource(Context context, Handler handler) {
        super(context, handler);

        Resources res = context.getResources();
        mDemoSignal = res.getIntArray(R.array.demosignal);
    }

    public synchronized void connect(String BTAddress) {
    }

    private static class ConnectionThread extends Thread {
    }
}
