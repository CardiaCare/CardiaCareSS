package ru.cardiacare.cardiacare.hisdocuments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Лабораторные исследования" */

public class LaboratoryStudyActivity extends AppCompatActivity {

    String organizationName = "Ivanov Corporation";
    Integer hemoglobin = 0;
    Float erythrocyte = new Float(0.0);
    Integer hematocrit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_laboratory_studies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        EditText etOrganizationName = (EditText) findViewById(R.id.etOrganizationName);
        assert etOrganizationName != null;
        etOrganizationName.setText(organizationName);
        EditText etHemoglobin = (EditText) findViewById(R.id.etHemoglobin);
        assert etHemoglobin != null;
        etHemoglobin.setText(hemoglobin.toString());
        EditText etErythrocyte = (EditText) findViewById(R.id.etErythrocyte);
        assert etErythrocyte != null;
        etErythrocyte.setText(erythrocyte.toString());
        EditText etHematocrit = (EditText) findViewById(R.id.etHematocrit);
        assert etHematocrit != null;
        etHematocrit.setText(hematocrit.toString());
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
