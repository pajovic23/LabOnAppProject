package com.example.projectapp;

public class UserProfile {
    //User Name it could be edited
    private String userName;
    private int userID;
    public UserProfile(int UserID){
        userID=UserID;
        // when creating new user, it will have the name as his ID
        userName=new Integer(UserID).toString();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }
}
