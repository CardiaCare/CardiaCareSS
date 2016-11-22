package ru.cardiacare.cardiacare.servey;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.*;
import java.net.MalformedURLException;

import ru.cardiacare.cardiacare.MainActivity;

/* Отправка ответов на сервер */

public class FeedbackPOST extends AsyncTask<Void, Integer, Integer> {

    private Context context;
    HttpURLConnection urlConnection = null;

    public FeedbackPOST(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public Integer doInBackground(Void... params) {
        try {
            Gson json = new Gson();
            String jsonFeedback;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                jsonFeedback = json.toJson(MainActivity.feedback);
            else jsonFeedback = json.toJson(MainActivity.alarmFeedback);
//            jsonFeedback = "{\"user_id\":" + "123456" + ", \"date\":" + "123456"+jsonFeedback+"}";

            // TODO feedback отправляется всегда, независимо от того, изменились ответы или нет.
//            String jsonFeedbackOld = readSavedData();
//            String newfb = jsonFeedback.substring(jsonFeedback.indexOf("personUri"), jsonFeedback.length());
//            String oldfb = jsonFeedbackOld.substring(jsonFeedbackOld.indexOf("personUri"), jsonFeedbackOld.length());
//            if (newfb.equals(oldfb)) {
//                Log.i(MainActivity.TAG, "Feedback не отправлен");
//                return 0;
//            }
            writeData(jsonFeedback);

            String myURL = "http://api.cardiacare.ru/index.php?r=feedback/create";
            String parameters;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                parameters = "user_id=" + "123456" + "&date=" + "123456" + "&feedback=" + jsonFeedback;
            else parameters = "user_id=" + "654321" + "&date=" + "654321" + "&feedback=" + jsonFeedback;
            byte[] data = null;
            InputStream is = null;

            try {
                URL url = new URL(myURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
                OutputStream os = conn.getOutputStream();
                data = parameters.getBytes("UTF-8");
                os.write(data);
                data = null;

                conn.connect();
                int responseCode = conn.getResponseCode();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (responseCode == 200) {
                    is = conn.getInputStream();

                    // Такого вот размера буфер
                    byte[] buffer = new byte[8192];
                    // Далее, например, вот так читаем ответ
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
//                    data = baos.toByteArray();
//                    resultString = new String(data, "UTF-8");
                }
            } catch (Exception e) {
//                resultString = "Exception:" + e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(MainActivity.TAG, "Feedback отправлен");
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }

    private String readSavedData() {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                fIn = context.openFileInput("feedbackold.json");
            else fIn = context.openFileInput("alarmfeedbackold.json");
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = buffreader.readLine();
            }
            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return datax.toString();
    }

    private void writeData(String data) {
        try {
//            FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                fOut = context.openFileOutput("feedbackold.json", context.MODE_PRIVATE);
            else fOut = context.openFileOutput("alarmfeedbackold.json", context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}