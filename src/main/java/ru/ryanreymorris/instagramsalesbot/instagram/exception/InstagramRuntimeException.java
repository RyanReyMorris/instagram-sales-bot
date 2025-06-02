package ru.ryanreymorris.instagramsalesbot.instagram.exception;

import com.xcoder.easyinsta.exceptions.Reasons;

public class InstagramRuntimeException extends RuntimeException {

    private Reasons reason;

    public InstagramRuntimeException(String message) {
        super(message);
    }

    public InstagramRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstagramRuntimeException(String message, Reasons reason, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public Reasons getReason() {
        return reason;
    }

    public void setReason(Reasons reason) {
        this.reason = reason;
    }
}
