package com.zohirdev.mystory.items;

public class UsersItem {
    String fullName;
    String userName;
    String uid;
    String token;
    String phone;
    String imageProfile;
    String imageCover;
    String links;
    String bio;
    String sex;
    String isBlocked;
    String deviceID;
    String verified;

    public UsersItem(String fullName, String userName, String uid, String token, String phone, String imageProfile, String imageCover, String links, String bio, String sex, String isBlocked, String deviceID, String verified) {
        this.fullName = fullName;
        this.userName = userName;
        this.uid = uid;
        this.token = token;
        this.phone = phone;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.links = links;
        this.bio = bio;
        this.sex = sex;
        this.isBlocked = isBlocked;
        this.deviceID = deviceID;
        this.verified = verified;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUid() {
        return uid;
    }

    public String getToken() {
        return token;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public String getImageCover() {
        return imageCover;
    }

    public String getLinks() {
        return links;
    }

    public String getBio() {
        return bio;
    }

    public String getSex() {
        return sex;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getVerified() {
        return verified;
    }
}
