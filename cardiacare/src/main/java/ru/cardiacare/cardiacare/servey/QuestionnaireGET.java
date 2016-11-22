package ru.cardiacare.cardiacare.servey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.servey.Questionnaire;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.cardiacare.cardiacare.R;

/* Загрузка периодического опросника с сервера */

public class QuestionnaireGET extends AsyncTask<Void, Integer, Integer> {

    private Context context;
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String resultJson = "";

    public QuestionnaireGET(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        MainActivity.mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public Integer doInBackground(Void... params) {
        try {
            URL url = new URL(QuestionnaireHelper.serverUri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        Gson json = new Gson();
        if (QuestionnaireHelper.questionnaireType.equals("periodic")) {
            QuestionnaireHelper.questionnaire = json.fromJson(resultJson, Questionnaire.class);
            if (QuestionnaireHelper.questionnaire == null) {
                return -1;
            }
        } else {
            QuestionnaireHelper.alarmQuestionnaire = json.fromJson(resultJson, Questionnaire.class);
            if (QuestionnaireHelper.alarmQuestionnaire == null) {
                return -1;
            }
        }
        try {
            OutputStreamWriter writer;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                writer = new OutputStreamWriter(context.openFileOutput(QuestionnaireHelper.questionnaireFile, Context.MODE_PRIVATE));
            else writer = new OutputStreamWriter(context.openFileOutput(QuestionnaireHelper.alarmQuestionnaireFile, Context.MODE_PRIVATE));
            writer.write(resultJson);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
//        QuestionnaireHelper.printQuestionnaire(QuestionnaireHelper.questionnaire);
        if (QuestionnaireHelper.questionnaireType.equals("periodic"))
            QuestionnaireHelper.questionnaireDownloaded = true;
        else QuestionnaireHelper.alarmQuestionnaireDownloaded = true;
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == -1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(R.string.dialog_server_error_title);
            alertDialog.setMessage(R.string.dialog_server_error_message);
            alertDialog.setNegativeButton(R.string.dialog_server_error_positive_button,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
//                            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
            alertDialog.show();
        } else {
//            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(context, QuestionnaireActivity.class);
            context.startActivity(intent);
        }
    }
}