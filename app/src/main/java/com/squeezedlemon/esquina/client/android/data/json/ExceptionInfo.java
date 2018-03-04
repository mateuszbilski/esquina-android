package com.squeezedlemon.esquina.client.android.data.json;

import java.io.Serializable;

public class ExceptionInfo implements Serializable {

    private String exceptionClass;
    private String exceptionMessage;
    private String localizedMessage;

    public ExceptionInfo() {

    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getLocalizedMessage() {
        return localizedMessage;
    }

    public void setLocalizedMessage(String localizedMessage) {
        this.localizedMessage = localizedMessage;
    }
}
