package com.zohirdev.mystory.items;

public class CommentsItem {
    String storyID;
    String publisher;
    String commentID;
    String userID;
    String comment;
    String time;
    String date;

    public CommentsItem(String storyID, String publisher, String commentID, String userID, String comment, String time, String date) {
        this.storyID = storyID;
        this.publisher = publisher;
        this.commentID = commentID;
        this.userID = userID;
        this.comment = comment;
        this.time = time;
        this.date = date;
    }

    public String getStoryID() {
        return storyID;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getCommentID() {
        return commentID;
    }

    public String getUserID() {
        return userID;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
