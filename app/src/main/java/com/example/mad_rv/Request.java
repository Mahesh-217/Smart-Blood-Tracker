package com.example.mad_rv;

public class Request {
    private String requestId;
    private String recipientName;
    private String bloodGroup;
    private String contactNumber;
    private String location;
    private String requesterUid;

    public Request() {
        // Default constructor required for Firebase
    }

    public Request(String requestId, String recipientName, String bloodGroup, String contactNumber, String location, String requesterUid) {
        this.requestId = requestId;
        this.recipientName = recipientName;
        this.bloodGroup = bloodGroup;
        this.contactNumber = contactNumber;
        this.location = location;
        this.requesterUid = requesterUid;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRequesterUid() {
        return requesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        this.requesterUid = requesterUid;
    }
}
