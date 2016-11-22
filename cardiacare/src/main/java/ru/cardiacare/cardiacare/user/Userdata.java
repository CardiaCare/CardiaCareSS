package ru.cardiacare.cardiacare.user;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Учётная запись" */

public class Userdata extends AppCompatActivity {

    SharedPreferences sPref;

    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    private static final String ACCOUNT_PREFERENCES_SIBNAME = "sibname";
    private static final String ACCOUNT_PREFERENCES_SIBIP = "sibip";
    private static final String ACCOUNT_PREFERENCES_SIBPORT = "sibport";

    private static final String ACCOUNT_PREFERENCES_EMAIL = "email";
    public static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    public static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";
    public static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    public static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    public static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    public static final String ACCOUNT_PREFERENCES_AGE = "age";
    private static final String ACCOUNT_PREFERENCES_PERIODPASSSERVEY = "time";

    EditText etSibName;
    EditText etSibIp;
    EditText etSibPort;

    EditText etEmail;
    EditText etFirstName;
    EditText etSecondName;
    EditText etPhoneNumber;
    EditText etHeight;
    EditText etWeight;
    EditText etAge;
    EditText etPeriodPassServey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_activity_toolbar);
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
        etEmail = (EditText) findViewById(R.id.etEmail);
        etSibName = (EditText) findViewById(R.id.etSibName);
        etSibIp = (EditText) findViewById(R.id.etSibIp);
        etSibPort = (EditText) findViewById(R.id.etSibPort);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etSecondName = (EditText) findViewById(R.id.etSecondName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etHeight = (EditText) findViewById(R.id.etHeight);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etAge = (EditText) findViewById(R.id.etAge);
        etPeriodPassServey = (EditText) findViewById(R.id.etPeriodPassServey);

        sPref = getSharedPreferences(ACCOUNT_PREFERENCES, MODE_ENABLE_WRITE_AHEAD_LOGGING);
    }

    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sPref.edit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_EMAIL, etEmail.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_SIBNAME, etSibName.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_SIBIP, etSibIp.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_SIBPORT, etSibPort.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_FIRSTNAME, etFirstName.getText().toString()).commit();
//        Log.i("TAG", "1 " + sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
        sPref.edit().putString(ACCOUNT_PREFERENCES_SECONDNAME, etSecondName.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_PHONENUMBER, etPhoneNumber.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_HEIGHT, etHeight.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_WEIGHT, etWeight.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_AGE, etAge.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, etPeriodPassServey.getText().toString()).commit();

        editor.apply();

        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }

    protected void onResume() {
        super.onResume();
        sPref = getSharedPreferences(ACCOUNT_PREFERENCES, MODE_ENABLE_WRITE_AHEAD_LOGGING);

        if (sPref.contains(ACCOUNT_PREFERENCES_SIBNAME)) {
            etSibName.setText(sPref.getString(ACCOUNT_PREFERENCES_SIBNAME, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_SIBIP)) {
            etSibIp.setText(sPref.getString(ACCOUNT_PREFERENCES_SIBIP, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_SIBPORT)) {
            etSibPort.setText(sPref.getString(ACCOUNT_PREFERENCES_SIBPORT, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_EMAIL)) {
            etEmail.setText(sPref.getString(ACCOUNT_PREFERENCES_EMAIL, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_FIRSTNAME)) {
            etFirstName.setText(sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_SECONDNAME)) {
            etSecondName.setText(sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_PHONENUMBER)) {
            etPhoneNumber.setText(sPref.getString(ACCOUNT_PREFERENCES_PHONENUMBER, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_HEIGHT)) {
            etHeight.setText(sPref.getString(ACCOUNT_PREFERENCES_HEIGHT, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_WEIGHT)) {
            etWeight.setText(sPref.getString(ACCOUNT_PREFERENCES_WEIGHT, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_AGE)) {
            etAge.setText(sPref.getString(ACCOUNT_PREFERENCES_AGE, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_PERIODPASSSERVEY)) {
            etPeriodPassServey.setText(sPref.getString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, ""));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
        MainActivity.ConnectToSmartSpace();
    }

    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        super.onBackPressed();
    }
}
