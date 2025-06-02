package ru.ryanreymorris.instagramsalesbot.instagram.dto;

import com.github.instagram4j.instagram4j.responses.IGResponse;

public class SendMessageResult {

    private boolean isMessageSent;

    private String errorMessage;

    private IGResponse response;

    public boolean isMessageSent() {
        return isMessageSent;
    }

    public void setMessageSent(boolean messageSent) {
        isMessageSent = messageSent;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public IGResponse getResponse() {
        return response;
    }

    public void setResponse(IGResponse response) {
        this.response = response;
    }
}
