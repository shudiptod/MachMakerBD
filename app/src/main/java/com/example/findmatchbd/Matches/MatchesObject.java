package com.example.findmatchbd.Matches;

public class MatchesObject {


    private String name;
    private String profileImageUrl;
    private String userId;

    public MatchesObject(String name,String profileImageUrl,String userId){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;

    }

    public String getUserId(){
        return userId;
    }
    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }



}
