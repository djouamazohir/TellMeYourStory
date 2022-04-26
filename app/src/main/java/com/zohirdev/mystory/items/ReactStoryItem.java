package com.zohirdev.mystory.items;

public class ReactStoryItem {


    String storyID;
    String userID;
    String reactType;

    public ReactStoryItem(String storyID, String userID, String reactType) {
        this.storyID = storyID;
        this.userID = userID;
        this.reactType = reactType;
    }

    public String getStoryID() {
        return storyID;
    }

    public String getUserID() {
        return userID;
    }

    public String getReactType() {
        return reactType;
    }
}
