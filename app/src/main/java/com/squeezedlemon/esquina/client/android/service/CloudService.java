package com.squeezedlemon.esquina.client.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.squeezedlemon.esquina.client.android.rest.HttpEntityBuilder;
import com.squeezedlemon.esquina.client.android.util.ResponseEntityUtils;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This service is used to exchange data between client and server.
 * To invoke this service, create Intent object and fill it with following fields:
 * URI - full path to resource (type: String)
 * RECEIVER - ResultReceiver object (providing callbacks)
 * METHOD_TYPE - type of request (type: String)
 * BODY - body of request (type: Object)
 * QUERY_PARAMS - uri parameters (type: HashMap<String, Object>)
 * ACCEPT_LANGUAGE - language (default: en) (type: String)
 * CUSTOM_HEADERS - request's custom headers (type: HashMap<String, String>)
 * USERNAME - username is used for authentication (type: String)
 * PASSWORD - password is used for authentication
 *
 * Service returns (in bundle):
 * RESPONSE_MAP - If data exchange was successfull -> Map contains response from server (see ResponseEntityUtils) (type: HashMap<String, Object>)
 * EXCEPTION_MESSAGE - If cannot connect to cloud -> exception message (type: String)
 */
public class CloudService extends IntentService {

    public static final String TAG = "CloudService";

    public static final String RECEIVER = "receiver";
    public static final String CODE = "code";
    public static final String URI = "uri";
    public static final String METHOD_TYPE = "methodType";
    public static final String BODY = "body";
    public static final String QUERY_PARAMS = "queryParams";
    public static final String ACCEPT_LANGUAGE = "acceptLanguage";
    public static final String CUSTOM_HEADERS = "customHeaders";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public static final String RESPONSE_MAP = "responseMap";
    public static final String EXCEPTION_MESSAGE = "message";

    public static final int STATUS_OK = 0;
    public static final int STATUS_CONNECTION_ERROR = 1;

    public CloudService() {
        super(CloudService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
        Bundle bundle = new Bundle();
        bundle.putString(CODE, intent.getStringExtra(CODE));
        ResponseEntity<JsonNode> response = null;

        try {
            Map<String, Object> uriVariablesMap = Collections.emptyMap();
            RestTemplate rest = new RestTemplate();

            rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory() {
                @Override
                protected HttpUriRequest createHttpRequest(HttpMethod httpMethod, java.net.URI uri) {
                    if (HttpMethod.DELETE == httpMethod) {
                        return new HttpEntityEnclosingDeleteRequest(uri);
                    }
                    return super.createHttpRequest(httpMethod, uri);
                }
            });

            rest.setErrorHandler(new DefaultResponseErrorHandler() {
                @Override
                protected boolean hasError(HttpStatus statusCode) {
                    return false;
                }
            });

            HttpEntityBuilder requestEntityBuilder = new HttpEntityBuilder();
            HttpMethod methodType = HttpMethod.valueOf(intent.getStringExtra(METHOD_TYPE));

            if (intent.hasExtra(ACCEPT_LANGUAGE)) {
                requestEntityBuilder.setAcceptLanguage(intent.getStringExtra(ACCEPT_LANGUAGE));
            }
            if (intent.hasExtra(USERNAME) && intent.hasExtra(PASSWORD)) {
                requestEntityBuilder.setAuthorization(intent.getStringExtra(USERNAME),
                        intent.getStringExtra(PASSWORD));
            }
            if (methodType != HttpMethod.GET && intent.hasExtra(BODY)) {
                requestEntityBuilder.setBody(intent.getSerializableExtra(BODY));
            }
            if (intent.hasExtra(CUSTOM_HEADERS)) {
                requestEntityBuilder.setHeaders( (HashMap<String, String>) intent.getSerializableExtra(CUSTOM_HEADERS));
            }
            if (intent.hasExtra(QUERY_PARAMS)) {
                uriVariablesMap = (HashMap<String, Object>) intent.getSerializableExtra(QUERY_PARAMS);
            }

            response = rest.exchange(intent.getStringExtra(URI), methodType,
                    requestEntityBuilder.build(), JsonNode.class, uriVariablesMap);

            HashMap<String, Object> responseMap = (HashMap<String, Object>) ResponseEntityUtils.convertToMap(response);
            bundle.putSerializable(RESPONSE_MAP, responseMap);
            Log.d(TAG, responseMap.toString());
            receiver.send(STATUS_OK, bundle);

        } catch (Exception e) {
            e.printStackTrace();
            bundle.putString(EXCEPTION_MESSAGE, e.getMessage());
            receiver.send(STATUS_CONNECTION_ERROR, bundle);
        }

        stopSelf();
    }

    public static class HttpEntityEnclosingDeleteRequest extends HttpEntityEnclosingRequestBase {

        public HttpEntityEnclosingDeleteRequest(final java.net.URI uri) {
            super();
            setURI(uri);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }
}
