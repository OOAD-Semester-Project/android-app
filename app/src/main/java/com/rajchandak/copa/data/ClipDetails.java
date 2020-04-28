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
    @SerializedName("timestamp")
    private long timestamp;

    public ClipDetails() {}

    public ClipDetails(String fromType, String from, long timestamp, String clipboardText) {
        this.fromType = fromType;
        this.from = from;
        this.timestamp = timestamp;
        this.clipboardText = clipboardText;
    }

    public ClipDetails(String _id, String fromType, String from, long timestamp, String clipboardText) {
        this._id = _id;
        this.fromType = fromType;
        this.from = from;
        this.timestamp = timestamp;
        this.clipboardText = clipboardText;
    }

    public String getID() {
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

    public long getTimestamp() {
        return timestamp;
    }
}
