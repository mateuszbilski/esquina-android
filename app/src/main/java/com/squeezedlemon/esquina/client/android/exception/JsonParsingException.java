package com.squeezedlemon.esquina.client.android.exception;

public class JsonParsingException extends Exception {

    public JsonParsingException() {
    }

    public JsonParsingException(String detailMessage) {
        super(detailMessage);
    }

    public JsonParsingException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public JsonParsingException(Throwable throwable) {
        super(throwable);
    }
}
