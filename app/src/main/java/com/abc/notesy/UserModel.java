package com.abc.notesy;

public class UserModel {
    private String userId;
    private String profileImageUrl;

    public UserModel() {
        // Default constructor required for Firestore
    }

    public UserModel(String userId, String profileImageUrl) {
        this.userId = userId;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

