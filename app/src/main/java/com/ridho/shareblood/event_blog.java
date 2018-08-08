package com.ridho.shareblood;

public class event_blog {

    private String nama, pj, id,lokasi, profile_image,tanggal,mulai,selesai,phone;
    public event_blog() {

    }
    public event_blog(String nama, String pj, String id, String lokasi, String profile_image, String tanggal, String mulai, String selesai,String phone) {
        this.nama = nama;
        this.pj = pj;
        this.id = id;
        this.lokasi = lokasi;
        this.profile_image = profile_image;
        this.tanggal = tanggal;
        this.mulai = mulai;
        this.selesai = selesai;
        this.phone = phone;

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPj() {
        return pj;
    }

    public void setPj(String pj) {
        this.pj = pj;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getMulai() {
        return mulai;
    }

    public void setMulai(String mulai) {
        this.mulai = mulai;
    }

    public String getSelesai() {
        return selesai;
    }

    public void setSelesai(String selesai) {
        this.selesai = selesai;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
