package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by CardiaCareTeam on 20.04.2016.
 */
public class Feedback {

    private String uri;
    private String personUri;
    private String questionnaireUri;
    LinkedList<Response> responses;

    public Feedback(String uri, String personUri, String questionnaireUri) {
        this.uri = uri;
        this.personUri = personUri;
        this.questionnaireUri = questionnaireUri;
        this.responses = new LinkedList<Response>();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPersonUri(String person_uri) {
        this.personUri = personUri;
    }

    public void setQuestionnaireUri(String questionnaire_uri) {
        this.questionnaireUri = questionnaireUri;
    }

    public void setResponses(LinkedList<Response> responses) {
        this.responses = responses;
    }

    public void addResponse(Response newResponse) {
        this.responses.addLast(newResponse);
    }

    public LinkedList<Response> getResponses() {
        return responses;
    }

    public String getUri() {
        return uri;
    }

    public String getPersonUri() {
        return personUri;
    }

    public String getQuestionnaireUri() {
        return questionnaireUri;
    }
}
