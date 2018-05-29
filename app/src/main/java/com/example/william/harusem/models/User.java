package com.example.william.harusem.models;

/**
 * Created by william on 5/17/2018.
 */

public class User {

    String name;
    String email;
    String connectionStatus;
    private long creationDate;

    private String id;
    private String country;

    public User() {
    }

    public User(String name, String email, String connectionStatus, long creationDate, String country) {
        this.name = name;
        this.email = email;
        this.connectionStatus = connectionStatus;
        this.creationDate = creationDate;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String reformatEmailAddress(String email) {
        return email.replace(".", "-");
    }

    public String createUniqueChatRef(long currentUserCreationDate, String currentUserEmail) {

        String uniqueChatRef = "";

        if (currentUserCreationDate > getCreationDate()) {
            uniqueChatRef = reformatEmailAddress(currentUserEmail) + "_" + reformatEmailAddress(getEmail());
        } else {
            uniqueChatRef = reformatEmailAddress(getEmail()) + "_" + reformatEmailAddress(currentUserEmail);
        }
        return uniqueChatRef;

    }


}
