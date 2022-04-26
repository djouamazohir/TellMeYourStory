package com.zohirdev.mystory.items;

public class NotesItem {
    String noteID;
    String user;
    String story;

    public NotesItem(String noteID, String user, String story) {
        this.noteID = noteID;
        this.user = user;
        this.story = story;
    }

    public String getNoteID() {
        return noteID;
    }

    public String getUser() {
        return user;
    }

    public String getStory() {
        return story;
    }
}
