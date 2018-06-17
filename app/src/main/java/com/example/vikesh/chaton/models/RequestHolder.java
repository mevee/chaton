package com.example.vikesh.chaton.models;

public class RequestHolder {
    String uid ,requestType;

    public RequestHolder() {
    }

    public RequestHolder(String uid, String requestType) {
        this.uid = uid;
        this.requestType = requestType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
