package com.zohirdev.mystory.items;

public class StorySavedItem {
    String storyID;
    String userID;
    String publisher;

    public StorySavedItem(String storyID, String userID, String publisher) {
        this.storyID = storyID;
        this.userID = userID;
        this.publisher = publisher;
    }

    public String getStoryID() {
        return storyID;
    }

    public String getUserID() {
        return userID;
    }

    public String getPublisher() {
        return publisher;
    }
}
