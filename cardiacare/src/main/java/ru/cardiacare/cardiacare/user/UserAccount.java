package ru.cardiacare.cardiacare.user;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Регистрация пользователя */

public class UserAccount extends AppCompatActivity {

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

    AccountStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

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
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etSecondName = (EditText) findViewById(R.id.etSecondName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etHeight = (EditText) findViewById(R.id.etHeight);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etAge = (EditText) findViewById(R.id.etAge);

        storage = new AccountStorage();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
//        MainActivity.smart.updatePersonName(MainActivity.nodeDescriptor, MainActivity.patientUri, etFirstName.getText() + " "+ etSecondName.getText());
        super.onBackPressed();
        MainActivity.backgroundFlag = 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if ( item.getItemId() == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        String version = storage.getQuestionnaireVersion();
        String lastquestionnairepassdate = storage.getLastQuestionnairePassDate();
        String periodpassservey = storage.getPeriodPassServey();
        storage.setAccountPreferences(
                etSibName.getText().toString(),
                etSibIp.getText().toString(),
                etSibPort.getText().toString(),
                MainActivity.patientUri,
                MainActivity.authorization_token,
                etEmail.getText().toString(),
                etFirstName.getText().toString(),
                etSecondName.getText().toString(),
                etPhoneNumber.getText().toString(),
                etHeight.getText().toString(),
                etWeight.getText().toString(),
                etAge.getText().toString(),
                version,
                lastquestionnairepassdate,
                periodpassservey);
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }

    protected void onResume() {
        super.onResume();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        etEmail.setText(storage.getAccountEmail());
        etFirstName.setText(storage.getAccountFirstName());
        etSecondName.setText(storage.getAccountSecondName());
        etPhoneNumber.setText(storage.getAccountPhoneNumber());
        etHeight.setText(storage.getAccountHeight());
        etWeight.setText(storage.getAccountWeight());
        etAge.setText(storage.getAccountAge());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
        MainActivity.ConnectToSmartSpace();
    }
}
