package ru.cardiacare.cardiacare.user;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран авторизации */
// Не используется. Кандидат на удаление

public class Login extends AppCompatActivity implements OnClickListener {

    EditText etLogin;
    EditText etPassword;
    String UserLogin, UserPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_activity_toolbar);
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

        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Button btEnter = (Button) findViewById(R.id.btEnter);
        assert btEnter != null;
        btEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btEnter:
                extractLogPass();
                break;
            default:
                break;
        }
    }

    private void extractLogPass() {
        UserLogin = etLogin.getText().toString();
        UserPass = etPassword.getText().toString();
        if (etLogin.getText().toString().trim() == "") {
            // TODO доделать - для этих логина и пароля получить юзера. Пока заглушка
            startActivity(new Intent(this, Userdata.class));
        } else {
//            JSONGenerator jsonGen = new JSONGenerator();
//            JSONObject json = jsonGen.generateAuthJSON(UserLogin, UserPass);
//            Log.d("LOG_TAG", json.toString());
//            String json = "{\"email\":\"test_patient@test.ru\",\"password\":\"test_patient\"}";
//            AuthorizationService intServ = new AuthorizationService();
//            intServ.execute(json);

            startActivity(new Intent(this, Userdata.class));
        }
    }

    private boolean emptyCheck() {
        if (UserPass.equals("") || UserLogin.equals("")) {
            Toast.makeText(getApplicationContext(), "Please, fill fields", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
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