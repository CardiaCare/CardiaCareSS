package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by CardiaCareTeam on 20.04.2016.
 */
public class ResponseItem {
    private String uri;
    private LinkedList<AnswerItem> linkedItems;

    private String textItem;
    private String fileUri;

    public ResponseItem(String uri,  String textItem, String fileUri) {
        this.uri = uri;
        this.textItem = textItem;
        this.fileUri = fileUri;
        this.linkedItems = new LinkedList<AnswerItem>();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void addLinkedAnswerItem(AnswerItem newAnswerItem) {
        this.linkedItems.addLast(newAnswerItem);
    }

    public void setTextItem(String text) {
        this.textItem = text;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getUri() {
        return uri;
    }

    public String getTextItem() {
        return textItem;
    }

    public String getFileUri() {
        return fileUri;
    }

    public LinkedList<AnswerItem> getLinkedItems() {
        return linkedItems;
    }
}
