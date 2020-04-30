package com.rajchandak.copa.helpers;

/**
 * Throws an error when retrofit fails to connect with the server.
 */
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
