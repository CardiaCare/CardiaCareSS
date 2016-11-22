package ru.cardiacare.cardiacare.hisdocuments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Документы" */

public class DocumentsActivity extends AppCompatActivity {

    static public String hisUri;
    static public String hisPatientUri;
//    static public long hisSibUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_documents);
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

//        hisSibUri = MainActivity.smart.connectSmartSpace("X", "109.195.115.73", 10010);

        Log.i("docs", MainActivity.nodeDescriptor + "");
        hisUri = MainActivity.smart.getHis(MainActivity.nodeDescriptor);

        if (hisUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_mis_error_message)
                    .setTitle(R.string.dialog_mis_error_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_mis_error_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        hisPatientUri = MainActivity.smart.setHisId(MainActivity.nodeDescriptor, hisUri, MainActivity.patientUri);

        if (hisPatientUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_unregistered_user_message)
                    .setTitle(R.string.dialog_unregistered_user_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_unregistered_user_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        Button demographicButton = (Button) findViewById(R.id.demographicData);
        assert demographicButton != null;
        demographicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, DemographicDataActivity.class));
            }
        });

        Button laboratoryButton = (Button) findViewById(R.id.laboratoryStudies);
        assert laboratoryButton != null;
        laboratoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, LaboratoryStudyActivity.class));
            }
        });

        Button resultsDoctorButton = (Button) findViewById(R.id.resultsDoctor);
        assert resultsDoctorButton != null;
        resultsDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, DoctorExaminationActivity.class));
            }
        });

        Button resultsBloodButton = (Button) findViewById(R.id.resultsBlood);
        assert resultsBloodButton != null;
        resultsBloodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, BloodPressureActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
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

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.backgroundFlag = 0;

    }
}
