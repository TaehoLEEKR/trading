package com.trade.common.util;

import com.trade.common.constant.ErrorCode;
import com.trade.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CallClient {

    private final RestTemplate restTemplate;

    public record CallResult(
            int statusCode,
            Map<String, List<String>> headers,
            String body
    ) {}

    public String GET(String uri, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return resultHandler(response);
    }

    public CallResult GET_WITH_HEADERS(String uri, Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        String body = resultHandler(response);
        return new CallResult(
                response.getStatusCode().value(),
                response.getHeaders(),
                body
        );
    }

    public String POST(String uri, Map<String, String> headers, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            return resultHandler(response);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public CallResult POST_WITH_HEADERS(String uri, Map<String, String> headers, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        HttpEntity<Object> entity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        String responseBody = resultHandler(response);
        return new CallResult(
                response.getStatusCode().value(),
                response.getHeaders(),
                responseBody
        );
    }

    // 폼 데이터 전송을 위한 메서드
    public String POST(String uri, Map<String, String> headers, MultiValueMap<String, String> formData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        if (!headers.containsKey("Content-Type")) {
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        return resultHandler(response);
    }

    // String 형태의 JSON을 전송하기 위한 메서드
    public String POST(String uri, Map<String, String> headers, String jsonBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);
        httpHeaders.add("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        return resultHandler(response);
    }

    // 바이너리 데이터를 전송하기 위한 메서드
    public String POST(String uri, Map<String, String> headers, byte[] byteBody) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        HttpEntity<byte[]> entity = new HttpEntity<>(byteBody, httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);

        return resultHandler(response);
    }

    private String resultHandler(ResponseEntity<String> response) {
        try {
            if (!response.getStatusCode().is2xxSuccessful()) {
                String msg = "Http " + response.getStatusCode().value() + ": " +
                        (response.getBody() != null ? response.getBody() : "Unknown Error");
                throw new CustomException(ErrorCode.FAILED_TO_CALL_CLIENT, msg);
            }
            return response.getBody() != null ? response.getBody() : ErrorCode.CALL_REQUEST_BODY_NULL.getMessage();
        } catch (Exception ex) {
            throw new CustomException(ErrorCode.CALL_REQUEST_BODY_NULL, ex.getMessage());
        }
    }

}
