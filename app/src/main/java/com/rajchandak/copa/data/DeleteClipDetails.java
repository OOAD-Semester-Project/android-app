package com.rajchandak.copa.data;


import com.google.gson.annotations.SerializedName;

public class DeleteClipDetails {

    @SerializedName("_id")
    private String _id;
    @SerializedName("fromType")
    private String fromType;

    public DeleteClipDetails() {}


    public DeleteClipDetails(String _id, String fromType) {
        this._id = _id;
        this.fromType = fromType;
    }

    public String getID() {
        return _id;
    }

    public String getFromType() {
        return fromType;
    }

}
