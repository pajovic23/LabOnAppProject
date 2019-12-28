package com.example.projectapp;

public class UserFormular {
    //userSex is defining sex of user. 0 is for male, 1 is for female
    private boolean userSex;
    //userRole is defining if user is calling a drone for himself or for somebody else. 1 is for
    //himself and 0 is for somebody else
    private boolean userWalk;
    private boolean userSpeak;

    public UserFormular(boolean UserSex, boolean UserWalk, boolean UserSpeak){
        userSex=UserSex;
        userWalk=UserWalk;
        userSpeak=UserSpeak;
    }

    public boolean isUserSex() {
        return userSex;
    }

    public boolean isUserRWalk() {
        return userWalk;
    }

    public boolean isUserSpeak() {
        return userSpeak;
    }


}
