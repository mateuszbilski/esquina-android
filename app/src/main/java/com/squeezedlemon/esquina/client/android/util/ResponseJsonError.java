package com.squeezedlemon.esquina.client.android.util;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squeezedlemon.esquina.client.android.R;
import com.squeezedlemon.esquina.client.android.data.json.ExceptionInfo;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ResponseJsonError {

    private static final String TAG = "ResponseJsonError";

    private ObjectMapper mapper = new ObjectMapper();

    private Context context;

    private JsonNode json;
    private ResponseErrorType type;
    private Integer responseStatus;
    private String requestUri;
    private String extraInfo;
    private ExceptionInfo exceptionInfo;

    /**
     * Throws JsonParsingException if json has invalid format
     */
    public ResponseJsonError(Context context, JsonNode json) throws JsonParsingException {
        this.context = context;
        this.json = json;
        decodeResponseError();
    }

    public ResponseJsonError(Context context, String json) throws JsonParsingException {
        try {
            this.context = context;
            ObjectMapper mapper = new ObjectMapper();
            this.json = mapper.readValue(json, JsonNode.class);
            decodeResponseError();
        } catch (IOException e) {
            throw new JsonParsingException(e);
        }
    }

    private void decodeResponseError() throws JsonParsingException {
        try {
            type = mapper.convertValue(json.path("type"), ResponseErrorType.class);
            responseStatus = mapper.convertValue(json.path("statusCode"), Integer.class);
            requestUri = mapper.convertValue(json.path("requestUri"), String.class);
            extraInfo = mapper.convertValue(json.path("extraInfo"), String.class);
            if (json.get("exception") != null) {
                exceptionInfo = mapper.convertValue(json.path("exception"), ExceptionInfo.class);
            }

            if (type == null || responseStatus == null || requestUri == null) {
                throw new JsonParsingException("Invalid error response");
            }
        } catch (IllegalArgumentException ex) {
            throw new JsonParsingException(ex);
        }
    }

    public ResponseErrorType getType() {
        return type;
    }

    public void setType(ResponseErrorType type) {
        this.type = type;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public ExceptionInfo getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(ExceptionInfo exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    public Map<String, String[]> getValidationErrors() {
        try {
            return mapper.convertValue(json.path("validationErrors"),
                    mapper.getTypeFactory().constructMapType(HashMap.class, String.class, String[].class));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserFriendlyMessage() {
        if (type == ResponseErrorType.APPLICATION) {
            if (exceptionInfo != null && exceptionInfo.getLocalizedMessage() != null) {
                return String.format("%s (code: %d)", exceptionInfo.getLocalizedMessage(), responseStatus);
            } else if (responseStatus.equals(422) && !json.path("validationErrors").isMissingNode()) {
                return String.format("%s (code: %d)", context.getString(R.string.validation_error), responseStatus);
            }
            else {
                return String.format("%s (code: %d)", context.getString(R.string.application_error), responseStatus);
            }
        } else if (responseStatus.equals(HttpStatus.UNAUTHORIZED.value())) {
            return context.getString(R.string.unauthorized_message);
        } else {
            return context.getString(R.string.critical_error);
        }
    }

    public enum ResponseErrorType {
        APPLICATION, MVC, RUNTIME
    }
}
