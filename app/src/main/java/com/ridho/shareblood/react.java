package com.ridho.shareblood;

public class react {
    String goldar,id,id_post;

    public react(){

    }
    public react(String goldar, String id, String id_post) {
        this.goldar = goldar;
        this.id = id;
        this.id_post = id_post;
    }

    public String getGoldar() {
        return goldar;
    }

    public void setGoldar(String goldar) {
        this.goldar = goldar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }
}
