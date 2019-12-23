package com.example.projectapp;

public class UserFormular {
    //userID is generated when user is using the app and it is unique for each user
    int userID;
    //userSex is defining sex of user. 0 is for male, 1 is for female
    boolean userSex;
    //userRole is defining if user is calling a drone for himself or for somebody else. 1 is for
    //himself and 0 is for somebody else
    boolean userRole;
    //userEnvironment is additional variable to help securities to locate him with some trademarks
    // around him
    String userEnvironment;
    public UserFormular(int UserID,boolean UserSex, boolean UserRole, String UserEnvironment){
        userID=UserID;
        userSex=UserSex;
        userRole=UserRole;
        userEnvironment=UserEnvironment;
    }
    public int getUserID() {
        return userID;
    }

    public boolean isUserSex() {
        return userSex;
    }

    public boolean isUserRole() {
        return userRole;
    }

    public String getUserEnvironment() {
        return userEnvironment;
    }


}
