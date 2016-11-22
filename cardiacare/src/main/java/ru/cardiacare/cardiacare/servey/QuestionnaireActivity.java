package ru.cardiacare.cardiacare.servey;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.SmartCareLibrary;
import com.petrsu.cardiacare.smartcare.servey.Answer;
import com.petrsu.cardiacare.smartcare.servey.Feedback;
import com.petrsu.cardiacare.smartcare.servey.Question;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Отображение периодического опросника */

public class QuestionnaireActivity extends AppCompatActivity {

    public static final int TextField = 0;
    public static final int Multiplechoice = 1;
    public static final int Singlechoice = 2;
    public static final int Bipolarquestion = 3;
    public static final int Guttmanscale = 4;
    public static final int Likertscale = 5;
    public static final int Continuousscale = 6;
    public static final int Dichotomous = 7;
    public static final int DefaultValue = 8;

    RecyclerView QuestionnaireRecyclerView;
    RecyclerView.Adapter QuestionnaireAdapter;
    RecyclerView.LayoutManager QuestionnaireLayoutManager;
    public Context context = this;
    boolean refreshFlag = false; // Была ли нажата кнопка "Обновить", true - была нажата / false - не была
    static ImageButton buttonRefresh;
    String periodic = "periodic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        FileInputStream fIn;
        try {
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                 fIn = openFileInput("feedback.json");
            else fIn = openFileInput("alarmFeedback.json");
            String jsonFromFile = readSavedData();
            Gson json = new Gson();
            Feedback qst = json.fromJson(jsonFromFile, Feedback.class);
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                MainActivity.feedback = qst;
            else MainActivity.alarmFeedback = qst;
        } catch (Exception e) {

        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_questionnaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.backgroundFlag = 1;
                Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
                configIntent.setAction(" ");
                startActivity(configIntent);
            }
        });

        QuestionnaireRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        QuestionnaireLayoutManager = new LinearLayoutManager(getApplicationContext());
        QuestionnaireRecyclerView.setLayoutManager(QuestionnaireLayoutManager);

        LinkedList<Question> questionnaire;
        if (QuestionnaireHelper.questionnaireType.equals(periodic))
            questionnaire = QuestionnaireHelper.questionnaire.getQuestions();
        else questionnaire = QuestionnaireHelper.alarmQuestionnaire.getQuestions();
        int[] Types = new int[questionnaire.size()];

        for (int i = 0; i < questionnaire.size(); i++) {
            Question question = questionnaire.get(i);
            Answer answer = question.getAnswer();
            switch (answer.getType()) {
                case "Text":
                    Types[i] = TextField;
                    break;
                case "MultipleChoise":
                    Types[i] = Multiplechoice;
                    break;
                case "SingleChoise":
                    Types[i] = Singlechoice;
                    break;
                case "BipolarQuestion":
                    Types[i] = Bipolarquestion;
                    break;
                case "Dichotomous":
                    Types[i] = Dichotomous;
                    break;
                case "GuttmanScale":
                    Types[i] = Guttmanscale;
                    break;
                case "LikertScale":
                    Types[i] = Likertscale;
                    break;
                case "ContinuousScale":
                    Types[i] = Continuousscale;
                    break;
                default:
                    Types[i] = DefaultValue;
            }
        }
        if (QuestionnaireHelper.questionnaireType.equals(periodic)) {
            QuestionnaireAdapter = new RecyclerViewAdapter(QuestionnaireHelper.questionnaire.getQuestions(), Types, context);
            QuestionnaireRecyclerView.setAdapter(QuestionnaireAdapter);
        } else {
            QuestionnaireAdapter = new AlarmRecyclerViewAdapter(QuestionnaireHelper.alarmQuestionnaire.getQuestions(), Types, context);
            QuestionnaireRecyclerView.setAdapter(QuestionnaireAdapter);
        }

        buttonRefresh = (ImageButton) findViewById(R.id.buttonClean);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {// Clean
            @Override // Clean
            public void onClick(View v) {
                String jsonStr = "";
                refreshFlag = true;
                MainActivity.backgroundFlag = 1;
                buttonRefresh.setEnabled(false);
                if (QuestionnaireHelper.questionnaireType.equals(periodic))
                    MainActivity.feedback = new Feedback("1 test", "Student", "feedback");
                else MainActivity.alarmFeedback = new Feedback("2 test", "Student", "alarmFeedback");

                Gson json = new Gson();
                if (QuestionnaireHelper.questionnaireType.equals(periodic))
                    jsonStr = json.toJson(MainActivity.feedback);
                else jsonStr = json.toJson(MainActivity.alarmFeedback);
                System.out.println(jsonStr);
                writeData(jsonStr);

                Intent intent = getIntent();
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }// Clean
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.backgroundFlag = 0;
        if (QuestionnaireHelper.questionnaireType.equals(periodic))
            MainActivity.serveyButton.setEnabled(true);
        else {
            MainActivity.alarmButton.setEnabled(true);
            MainActivity.alarmButton.setBackgroundResource(R.color.alarm_button_standart_color);
        }

//        MainActivity.QuestionnaireButton.setEnabled(true);//возвращаем состояние нажатия от повторного нажатия
//        buttonRefresh.setEnabled(true);//возвращаем состояние нажатия от повторного нажатия
//        MainActivity.alarmButton.setEnabled(true);//возвращаем состояние нажатия от повторного нажатия
    }

    public void writeData(String data) {
        try {
//            FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut;
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                fOut = context.openFileOutput("feedback.json", context.MODE_PRIVATE);
            else fOut = context.openFileOutput("alarmFeedback.json", context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readSavedData() {
        StringBuilder datax = new StringBuilder("");
        FileInputStream fIn;
        try {
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                fIn = openFileInput("feedback.json");
            else fIn = openFileInput("alarmFeedback.json");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
        MainActivity.ConnectToSmartSpace();
    }

    @Override
    public void onPause() {
        if (refreshFlag == false) {
            String jsonStr;
            Gson json = new Gson();
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                jsonStr = json.toJson(MainActivity.feedback);
            else jsonStr = json.toJson(MainActivity.alarmFeedback);
            System.out.println(jsonStr);
            writeData(jsonStr);
            if (QuestionnaireHelper.questionnaireType.equals(periodic)) {
                // To SIB
                Long timestamp = System.currentTimeMillis() / 1000;
                String ts = timestamp.toString();
                SmartCareLibrary.sendFeedback(MainActivity.nodeDescriptor, MainActivity.patientUri, MainActivity.feedbackUri, ts);
                MainActivity.storage.setLastQuestionnairePassDate(ts);
            }
            FeedbackPOST feedbackPOST = new FeedbackPOST(context);
            feedbackPOST.execute();
        }
        super.onPause();
        refreshFlag = false;
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
        configIntent.setAction(" ");
        startActivity(configIntent);
//        super.onBackPressed();
    }
}