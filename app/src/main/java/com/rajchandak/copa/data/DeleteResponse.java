package com.rajchandak.copa.data;

import com.google.gson.annotations.SerializedName;

public class DeleteResponse {
    @SerializedName("success")
    private Boolean success;
    @SerializedName("message")
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
