package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 09.03.16.
 */
public class AnswerItem {
    private String uri;
    private String itemScore;
    private String itemText;
    LinkedList<Answer> subAnswers;


    public AnswerItem(String uri_from_sib,String itemScore_from_sib, String itemText_from_sib){
        uri = uri_from_sib;
        itemScore = itemScore_from_sib;
        itemText = itemText_from_sib;
        subAnswers = new LinkedList<Answer>();
    }

    public AnswerItem(AnswerItem item){
        this.uri = item.uri;
        this.itemScore = item.itemScore;
        this.itemText = item.itemText;
        this.subAnswers = item.subAnswers;
    }

    // Добавить подответ в конец списка подответов
    public void addSubAnswer(Answer answer){
        Answer subAnswer = new Answer(answer);
        subAnswers.addLast(subAnswer);
    }

    // Получение списка подответов
    public LinkedList<Answer> getSubAnswers() {
        return subAnswers;
    }
    // Получение идентификатора варианта ответа в ИП
    public String getUri() {
        return uri;
    }
    // Получение значимости варианта ответа
    public String getItemScore() {
        return itemScore;
    }
    // Получение текста варианта ответа
    public String getItemText() {
        return itemText;
    }
}
