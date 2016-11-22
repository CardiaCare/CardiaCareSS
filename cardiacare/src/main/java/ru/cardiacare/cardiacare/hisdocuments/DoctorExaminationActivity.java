package ru.cardiacare.cardiacare.hisdocuments;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultDoctorExamination;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Результаты обследования врачом */

public class DoctorExaminationActivity extends AppCompatActivity {

    String searchstring = null;
    String fieldName = null;
    String dateFrom = null;
    String dateTo = null;

    static public String hisRequestUri;
    static public String hisDocumentUri;
    static public String hisResponseUri;
    ResultDoctorExamination rde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results_doctor);
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

        String hisDocumentType = "http://oss.fruct.org/smartcare#DoctorExamination";

        hisRequestUri = MainActivity.smart.sendHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, MainActivity.patientUri,
                hisDocumentType, searchstring, fieldName, dateFrom, dateTo);

        hisResponseUri = MainActivity.smart.getHisResponce(MainActivity.nodeDescriptor, hisRequestUri);

        if (hisResponseUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_server_error_message)
                    .setTitle(R.string.dialog_server_error_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_server_error_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        hisDocumentUri = MainActivity.smart.getHisDocument(MainActivity.nodeDescriptor, hisResponseUri);

        if (hisDocumentUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_document_message)
                    .setTitle(R.string.dialog_document_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_document_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        rde = new ResultDoctorExamination("Examination reason", "Visit order",
                "Diagnoses", "Medications", "true", "No", "h", "w", "Diagnoses");
        rde = MainActivity.smart.getHisDoctorExamination(MainActivity.nodeDescriptor, hisDocumentUri);

        EditText etExaminationReason = (EditText) findViewById(R.id.etExaminationReason);
        assert etExaminationReason != null;
        etExaminationReason.setText(rde.getExaminationReason());
        EditText etVisitOrder = (EditText) findViewById(R.id.etVisitOrder);
        assert etVisitOrder != null;
        etVisitOrder.setText(rde.getVisitOrder());
        EditText etDiagnoses = (EditText) findViewById(R.id.etDiagnoses);
        assert etDiagnoses != null;
        etDiagnoses.setText(rde.getDiagnoses());
        EditText etMedications = (EditText) findViewById(R.id.etMedications);
        assert etMedications != null;
        etMedications.setText(rde.getMedications());
        EditText etSmooking = (EditText) findViewById(R.id.etSmooking);
        assert etSmooking != null;
        etSmooking.setText(rde.getSmooking());
        EditText etDrinking = (EditText) findViewById(R.id.etDrinking);
        assert etDrinking != null;
        etDrinking.setText(rde.getDrinking());
        EditText etHeight = (EditText) findViewById(R.id.etHeight);
        assert etHeight != null;
        etHeight.setText(rde.getHeight());
        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        assert etWeight != null;
        etWeight.setText(rde.getWeight());
        EditText etDiseasePredisposition = (EditText) findViewById(R.id.etDiseasePredisposition);
        assert etDiseasePredisposition != null;
        etDiseasePredisposition.setText(rde.getDiseasePredisposition());
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
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisDocumentUri);
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisResponseUri);
        MainActivity.smart.removeHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, hisRequestUri);
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisRequestUri);
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
