package ru.cardiacare.cardiacare;

import org.json.JSONObject;

/* Разбор JSON-файлов */
// Не используется

public class JSONParser {

    final String LOG_TAG = "myLogs JSONParser";

    public void readAuthResponce(JSONObject reader) {
        try {
            Integer user_id = reader.getInt("user_id");
            String code = reader.getString("code");
            Integer created_at = reader.getInt("created_at");
            Integer type = reader.getInt("type");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}