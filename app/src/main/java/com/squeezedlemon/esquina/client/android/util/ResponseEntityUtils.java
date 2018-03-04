package com.squeezedlemon.esquina.client.android.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squeezedlemon.esquina.client.android.exception.JsonParsingException;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts response from server into map.
 * Class is used in CloudService.java for preparing result
 * Map consist of three elements:
 * body -> json response from server (stored as String)
 * status -> response status from server (stored as HttpStatus)
 * headers -> response headers from server (stored as Map<String, String>)
 */
public class ResponseEntityUtils {

    public static final String RESPONSE_MAP_BODY = "body";
    public static final String RESPONSE_MAP_STATUS = "status";
    public static final String RESPONSE_MAP_HEADERS = "headers";

    public static <T extends JsonNode> Map<String, Object> convertToMap(ResponseEntity<T> responseEntity) throws JsonParsingException {
        Map<String, Object> map = new HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            map.put(RESPONSE_MAP_BODY, mapper.writeValueAsString(responseEntity.getBody()));
            map.put(RESPONSE_MAP_STATUS, responseEntity.getStatusCode());
            map.put(RESPONSE_MAP_HEADERS, responseEntity.getHeaders().toSingleValueMap());
        } catch (IOException ex) {
            throw new JsonParsingException(ex);
        }
        return map;
    }
}
