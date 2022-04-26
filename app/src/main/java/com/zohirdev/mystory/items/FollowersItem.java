package com.zohirdev.mystory.items;

public class FollowersItem {
    String user;
    String follow;

    public FollowersItem(String user, String follow) {
        this.user = user;
        this.follow = follow;
    }

    public String getUser() {
        return user;
    }

    public String getFollow() {
        return follow;
    }
}
