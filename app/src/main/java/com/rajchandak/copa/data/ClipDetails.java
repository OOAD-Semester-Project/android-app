package com.rajchandak.copa.data;


import com.google.gson.annotations.SerializedName;

public class ClipDetails {
    @SerializedName("_id")
    private String _id;
    @SerializedName("from")
    private String from;
    @SerializedName("fromType")
    private String fromType;
    @SerializedName("clipboardText")
    private String clipboardText;
    @SerializedName("userId")
    private String userId;

    public String get_id() {
        return _id;
    }

    public String getFrom() {
        return from;
    }

    public String getFromType() {
        return fromType;
    }

    public String getClipboardText() {
        return clipboardText;
    }

    public String getUserId() {
        return userId;
    }
}
