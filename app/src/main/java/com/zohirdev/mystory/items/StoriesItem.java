package com.zohirdev.mystory.items;


public class StoriesItem {
    String storyID;
    String story;
    String time;
    String date;
    String publisher;

    public StoriesItem(String storyID, String story, String time, String date, String publisher) {
        this.storyID = storyID;
        this.story = story;
        this.time = time;
        this.date = date;
        this.publisher = publisher;
    }

    public String getStoryID() {
        return storyID;
    }

    public String getStory() {
        return story;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getPublisher() {
        return publisher;
    }
}
