package ru.cardiacare.cardiacare.ecgviewer;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "ЭКГ" */

public class ECGActivity extends AppCompatActivity {

    final int Data = 1;
    ECGView myView;
    private int[] viewDemoSignal;

    //    private BluetoothService mBluetoothService = null;
    private static final String TAG = "ECGActivity";
    private static final float TWO_INCHES = 2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate ECGActivity Activity");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ecg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ecg_activity_toolbar);
        setSupportActionBar(toolbar);

        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.backgroundFlag = 1;
                onBackPressed();
            }
        });

        RelativeLayout v = (RelativeLayout) findViewById(R.id.ecg_view);
        myView = new ECGView(this, setViewWidthInMillimeter());

        assert v != null;
        v.addView(myView);

        Resources res = getResources();
        viewDemoSignal = res.getIntArray(R.array.demosignal);

        myView.getECGData(viewDemoSignal);

        Handler handler;

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case Data:
                        int[] readBuf = (int[]) msg.obj;
//                        String strIncom = new String(readBuf, 0, msg.arg1);
//                        mytext.setText("Данные от Arduino: " + strIncom);
//                        Log.i ("TAG","Данные от Alive: " + readBuf.length);
                        myView.getECGData(readBuf);
                }
            }

            ;
        };
//        mBluetoothService = new BluetoothService(getApplicationContext(), handler);
    }

    private double setViewWidthInMillimeter() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float mXDpi = metrics.xdpi;
        double ppmm = mXDpi / 25.4f;
        return ppmm;
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy ECGActivity Activity");
        myView.pulseTimer.cancel();
        myView.myTimer.cancel();
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
        MainActivity.ConnectToSmartSpace();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        super.onBackPressed();
    }
}
