package com.squeezedlemon.esquina.client.android.rest;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;

public class HttpEntityBuilder {

    public static final String DEFAULT_LANGUAGE = "en";
    private HttpHeaders headers;
    private Object body;

    public HttpEntityBuilder() {
        headers = new HttpHeaders();
        headers.setAcceptLanguage(DEFAULT_LANGUAGE);
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    public HttpEntityBuilder setAcceptLanguage(String lang) {
        headers.setAcceptLanguage(lang);
        return this;
    }

    public HttpEntityBuilder setContentType(MediaType mediaType) {
        headers.setContentType(mediaType);
        return this;
    }

    public HttpEntityBuilder setAuthorization(String accountName, String password) {
        headers.setAuthorization(new HttpBasicAuthentication(accountName, password));
        return this;
    }

    public HttpEntityBuilder setHeader(String headerName, String headerValue) {
        headers.set(headerName, headerValue);
        return this;
    }

    public HttpEntityBuilder setHeaders(Map<String, String> customHeaders) {
        for (Map.Entry<String, String> item : customHeaders.entrySet()) {
            headers.set(item.getKey(), item.getValue());
        }
        return this;
    }

    public HttpEntityBuilder setBody(Object body) {
        this.body = body;
        return this;
    }

    public HttpEntity<Object> build() {
        if (body == null) {
            return new HttpEntity<Object>(headers);
        } else {
            return new HttpEntity<Object>(body, headers);
        }
    }
}
