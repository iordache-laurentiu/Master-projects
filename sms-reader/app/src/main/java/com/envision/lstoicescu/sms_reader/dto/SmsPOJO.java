package com.envision.lstoicescu.sms_reader.dto;

/**
 * Created by lstoicescu on 5/3/2018.
 */

public class SmsPOJO {
    private String sender;
    private String message;
    private String date;

    public SmsPOJO() {
    }

    public SmsPOJO(String sender, String message, String date) {
        this.sender = sender;
        this.message = message;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
