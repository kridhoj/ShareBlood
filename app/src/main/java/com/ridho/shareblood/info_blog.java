package com.ridho.shareblood;

public class info_blog {
    private String Uid,name,profile_image;
    public info_blog(){

    }

    public info_blog(String uid, String name, String profile_image) {
        Uid = uid;
        this.name = name;
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        this.Uid = uid;
    }
}
