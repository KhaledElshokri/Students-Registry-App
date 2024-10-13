package com.example.student_registry_app;

public class Access {
    private int accessID;
    private int profileID;
    private String accessType;
    private String timestamp;

    // Constructor
    public Access(int accessID, int profileID, String accessType, String timestamp)
    {
        this.accessID = accessID;
        this.profileID = profileID;
        this.accessType = accessType;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getAccessID() { return accessID; }
    public void setAccessID(int accessID) { this.accessID = accessID; }

    public int getProfileID() { return profileID; }
    public void setProfileID(int profileID) { this.profileID = profileID; }

    public String getAccessType() { return accessType; }
    public void setAccessType(String accessType) { this.accessType = accessType; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}

