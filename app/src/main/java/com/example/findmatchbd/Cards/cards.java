package com.example.findmatchbd.Cards;

public class cards {

    private String userId;
    private String name;
    private String profileImageUrl;
    private String profession;

    public cards(String userId, String name, String profileImageUrl, String profession){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.profession = profession;
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

    public String getProfession(){
        return profession;
    }
    public void setProfession(String profession){
        this.profession = profession;
    }


    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }

}
