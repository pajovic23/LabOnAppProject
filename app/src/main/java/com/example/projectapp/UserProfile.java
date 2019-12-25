package com.example.projectapp;

public class UserProfile {
    //User Name it could be edited
    private String userName;
    private String userID;
    private String userPassword;


    public UserProfile(String UserID){
        userID=UserID;
        // when creating new user, it will have the name as his ID
        userName=userID;
    }
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }
}
