package ru.cardiacare.cardiacare.hisdocuments;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.DemographicData;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Демографические данные" */

public class DemographicDataActivity extends AppCompatActivity {

    String searchstring = null;
    String fieldName = null;
    String dateFrom = null;
    String dateTo = null;

    static public String hisRequestUri;
    static public String hisResponseUri;
    static public String hisDocumentUri;

    DemographicData dd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_demographic_data);
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

        String hisDocumentType = "http://oss.fruct.org/smartcare#DemographicData";

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

        dd = new DemographicData("name", "surname", "patronymic", "birthDate", "sex",
                "residence", "contactInformation");
        dd = MainActivity.smart.getHisDemographicData(MainActivity.nodeDescriptor, hisDocumentUri);


        EditText etName = (EditText) findViewById(R.id.etName);
        assert etName != null;
        etName.setText(dd.getPatientName());
        EditText etSurname = (EditText) findViewById(R.id.etSurname);
        assert etSurname != null;
        etSurname.setText(dd.getSurname());
        EditText etPatronymic = (EditText) findViewById(R.id.etPatronymic);
        assert etPatronymic != null;
        etPatronymic.setText(dd.getPatronymic());
        EditText etBirthDate = (EditText) findViewById(R.id.etBirthDate);
        assert etBirthDate != null;
        etBirthDate.setText(dd.getBirthDate());
        EditText etSex = (EditText) findViewById(R.id.etSex);
        assert etSex != null;
        etSex.setText(dd.getSex());
        EditText etResidence = (EditText) findViewById(R.id.etResidence);
        assert etResidence != null;
        etResidence.setText(dd.getResidence());
        EditText etContactInformation = (EditText) findViewById(R.id.etContactInformation);
        assert etContactInformation != null;
        etContactInformation.setText(dd.getContactInformation());
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
