package com.ridho.shareblood;

public class post_blog {
    private String name, pesan, id_post, profile_image, golongan,id,tanggal;

    public post_blog() {

    }

    public post_blog(String name, String pesan, String id_post, String profile_image, String golongan, String id,String tanggal) {
        this.name = name;
        this.pesan = pesan;
        this.id_post = id_post;
        this.profile_image = profile_image;
        this.golongan = golongan;
        this.id = id;
        this.tanggal = tanggal;
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
}
