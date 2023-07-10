package com.example.spendsaver;

// Java class to group user information retrieved from firebase database

public class userInfo {
    private String username;
    private String password;
    private String accType;

    public userInfo() {
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getAccType() {
        return accType;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setAccType(String accType) {
        this.accType = accType;
    }

}
