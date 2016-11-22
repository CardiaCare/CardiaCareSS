package ru.cardiacare.cardiacare.user;

import android.content.SharedPreferences;

/* Хранение данных аккаунта */

public class AccountStorage {

    public SharedPreferences sPref;
    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    // Настроки подключения к SIB'у
    private static final String ACCOUNT_PREFERENCES_SIBNAME = "sibname"; // Имя
    private static final String ACCOUNT_PREFERENCES_SIBIP = "sibip"; // IP
    private static final String ACCOUNT_PREFERENCES_SIBPORT = "sibport"; // Порт

    private static final String ACCOUNT_PREFERENCES_PATIENTID = "id";
    private static final String ACCOUNT_PREFERENCES_TOKEN = "token"; // Токен доступа, полученный с сервера
    private static final String ACCOUNT_PREFERENCES_EMAIL = "email";
    private static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    private static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";
    private static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    private static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    private static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    private static final String ACCOUNT_PREFERENCES_AGE = "age";
    private static final String ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION = "questionnaireversion"; // Версия последней загруженной анкеты
    private static final String ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE = "date"; // Дата последнего прохождения периодического опроса
    private static final String ACCOUNT_PREFERENCES_PERIODPASSSERVEY = "time"; // Период прохождения периодического опроса (например, 1 раз в 30 дней), в секундах


    private String strSibName;
    private String strSibIp;
    private String strSibPort;

    private String strId;
    private String strToken;
    private String strEmail;
    private String strFirstName;
    private String strSecondName;
    private String strPhoneNumber;
    private String strHeight;
    private String strWeight;
    private String strAge;
    private String strQuestionnaireVersion;
    private String strLastQuestionnairePassDate;
    private String strPeriodPassServey;

    public void setAccountPreferences(String sibName, String sibIp, String sibPort, String patientId, String token, String email, String firstname, String secondname, String phonenumber, String height, String weight, String age, String questionnaireversion, String lastquestionnairepassdate, String periodpassservey) {
        SharedPreferences.Editor editor = sPref.edit();

        editor.putString(ACCOUNT_PREFERENCES_SIBNAME, sibName);
        editor.putString(ACCOUNT_PREFERENCES_SIBIP, sibIp);
        editor.putString(ACCOUNT_PREFERENCES_SIBPORT, sibPort);

        editor.putString(ACCOUNT_PREFERENCES_PATIENTID, patientId);
        editor.putString(ACCOUNT_PREFERENCES_TOKEN, token);
        editor.putString(ACCOUNT_PREFERENCES_EMAIL, email);
        editor.putString(ACCOUNT_PREFERENCES_FIRSTNAME, firstname);
        editor.putString(ACCOUNT_PREFERENCES_SECONDNAME, secondname);
        editor.putString(ACCOUNT_PREFERENCES_PHONENUMBER, phonenumber);
        editor.putString(ACCOUNT_PREFERENCES_HEIGHT, height);
        editor.putString(ACCOUNT_PREFERENCES_WEIGHT, weight);
        editor.putString(ACCOUNT_PREFERENCES_AGE, age);
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, questionnaireversion);
        editor.putString(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE, lastquestionnairepassdate);
        editor.putString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, periodpassservey);

        editor.apply();
    }

    public void setVersion(String questionnaireversion) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, questionnaireversion);
        editor.apply();
    }

    public void setLastQuestionnairePassDate (String lastquestionnairepassdate) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE, lastquestionnairepassdate);
        editor.apply();
    }

    public void setPeriodPassServey (String periodpassservey) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, periodpassservey);
        editor.apply();
    }

    public String getSibName() {
        if (sPref.contains(ACCOUNT_PREFERENCES_SIBNAME)) {
            strSibName = sPref.getString(ACCOUNT_PREFERENCES_SIBNAME, "");
        } else strSibName = "";
        return strSibName;
    }

    public String getSibIp() {
        if (sPref.contains(ACCOUNT_PREFERENCES_SIBIP)) {
            strSibIp = sPref.getString(ACCOUNT_PREFERENCES_SIBIP, "");
        } else strSibIp = "";
        return strSibIp;
    }

    public String getSibPort() {
        if (sPref.contains(ACCOUNT_PREFERENCES_SIBPORT)) {
            strSibPort = sPref.getString(ACCOUNT_PREFERENCES_SIBPORT, "");
        } else strSibPort = "";
        return strSibPort;
    }

    public String getAccountId() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PATIENTID)) {
            strId = sPref.getString(ACCOUNT_PREFERENCES_PATIENTID, "");
        } else strId = "";
        return strId;
    }

    public String getAccountToken() {
        if (sPref.contains(ACCOUNT_PREFERENCES_TOKEN)) {
            strToken = sPref.getString(ACCOUNT_PREFERENCES_TOKEN, "");
        } else strToken = "";
        return strToken;
    }

    public String getAccountEmail() {
        if (sPref.contains(ACCOUNT_PREFERENCES_EMAIL)) {
            strEmail = sPref.getString(ACCOUNT_PREFERENCES_EMAIL, "");
        } else strEmail = "";
        return strEmail;
    }

    public String getAccountFirstName() {
        if (sPref.contains(ACCOUNT_PREFERENCES_FIRSTNAME)) {
            strFirstName = sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, "");
        } else strFirstName = "";
        return strFirstName;
    }

    public String getAccountSecondName() {
        if (sPref.contains(ACCOUNT_PREFERENCES_SECONDNAME)) {
            strSecondName = sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, "");
        } else strSecondName = "";
        return strSecondName;
    }

    String getAccountPhoneNumber() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PHONENUMBER)) {
            strPhoneNumber = sPref.getString(ACCOUNT_PREFERENCES_PHONENUMBER, "");
        } else strPhoneNumber = "";
        return strPhoneNumber;
    }

    String getAccountHeight() {
        if (sPref.contains(ACCOUNT_PREFERENCES_HEIGHT)) {
            strHeight = sPref.getString(ACCOUNT_PREFERENCES_HEIGHT, "");
        } else strHeight = "";
        return strHeight;
    }

    String getAccountWeight() {
        if (sPref.contains(ACCOUNT_PREFERENCES_WEIGHT)) {
            strWeight = sPref.getString(ACCOUNT_PREFERENCES_WEIGHT, "");
        } else strWeight = "";
        return strWeight;
    }

    String getAccountAge() {
        if (sPref.contains(ACCOUNT_PREFERENCES_AGE)) {
            strAge = sPref.getString(ACCOUNT_PREFERENCES_AGE, "");
        } else strAge = "";
        return strAge;
    }

    public String getQuestionnaireVersion() {
        if (sPref.contains(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION)) {
            strQuestionnaireVersion = sPref.getString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, "");
        } else strQuestionnaireVersion = "";
        return strQuestionnaireVersion;
    }

    public String getLastQuestionnairePassDate() {
        if (sPref.contains(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE)) {
            strLastQuestionnairePassDate = sPref.getString(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE, "");
        } else strLastQuestionnairePassDate = "";
        return strLastQuestionnairePassDate;
    }

    public String getPeriodPassServey() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PERIODPASSSERVEY)) {
            strPeriodPassServey = sPref.getString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, "");
        } else strPeriodPassServey = "";
        return strPeriodPassServey;
    }
}