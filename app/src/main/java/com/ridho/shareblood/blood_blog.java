package com.ridho.shareblood;

/**
 * Created by Ridho on 3/29/2018.
 */

public class blood_blog {

    private String name, pesan, id_post, profile_image, golongan,id,tanggal,notif;

    public blood_blog() {

    }

    public blood_blog(String name, String pesan, String id_post, String profile_image, String golongan, String id,String tanggal,String notif) {
        this.name = name;
        this.pesan = pesan;
        this.id_post = id_post;
        this.profile_image = profile_image;
        this.golongan = golongan;
        this.id = id;
        this.tanggal = tanggal;
        this.notif = notif;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getGolongan() {
        return golongan;
    }

    public void setGolongan(String golongan) {
        this.golongan = golongan;
    }

    public String getId(){return id;}

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal(){return tanggal;}

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNotif(){return notif;}

    public void setNotif(String notif) {
        this.notif = notif;
    }
}
