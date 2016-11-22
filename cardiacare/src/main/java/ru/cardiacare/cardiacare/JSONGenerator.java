package ru.cardiacare.cardiacare;

import org.json.JSONObject;

/* Создание JSON-файлов */

public class JSONGenerator {

    public JSONObject generateAuthJSON(String username, String password) {

        JSONObject json = new JSONObject();
        try {
//            JSONObject json = new JSONObject();
            json.put("email", username);
            json.put("password", password);
//            JSONObject url = new JSONObject();
//            url.put("url", "api.cardiacare.ru/index.php/user/auth");
//
//            jsonMain.put("json",json);
//            jsonMain.put("url", url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
