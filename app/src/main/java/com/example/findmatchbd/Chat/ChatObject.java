package com.example.findmatchbd.Chat;

public class ChatObject {


    private String message;
    private Boolean currentUser;

    public ChatObject(String message,Boolean currentUser){
        this.currentUser = currentUser;
        this.message = message;

    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }


    public Boolean getCurrentUser(){
        return currentUser;
    }
    public void setCurrentUser(Boolean currentUser){
        this.currentUser = currentUser;
    }



}
