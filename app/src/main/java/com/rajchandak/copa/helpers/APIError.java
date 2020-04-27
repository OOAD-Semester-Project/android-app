package com.rajchandak.copa.helpers;

public class APIError {
    private int statusCode;
    private String endpoint;
    private String message = "Unknown error";

    public APIError() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
