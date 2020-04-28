package com.rajchandak.copa.data;


import com.google.gson.annotations.SerializedName;

public class ClipDetails {

    @SerializedName("from")
    private String from;
    @SerializedName("fromType")
    private String fromType;
    @SerializedName("clipboardText")
    private String clipboardText;
    @SerializedName("timestamp")
    private long timestamp;

    public ClipDetails() {}

    public ClipDetails(String fromType, String from, long timestamp, String clipboardText) {
        this.fromType = fromType;
        this.from = from;
        this.timestamp = timestamp;
        this.clipboardText = clipboardText;
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

    public long getTimestamp() {
        return timestamp;
    }
}
