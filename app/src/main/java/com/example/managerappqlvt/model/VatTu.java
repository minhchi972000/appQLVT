package com.example.managerappqlvt.model;


import java.io.Serializable;

// nhom trong  va  adapter SanPhamMoiAdapter can dung hien thi id, ten,hinh anh trong list
public class VatTu implements Serializable {
    int id;
    int idnhom;
    String tensp;
    String mahang;
    String hinhanh;
    String giaban;
    String gianhap;
    String mota;
    String tencongty;
    int soluong;
    String ngaynhap;
    int loai; // nhom hang

    public int getIdnhom() {
        return idnhom;
    }

    public void setIdnhom(int idnhom) {
        this.idnhom = idnhom;
    }

    public String getMahang() {
        return mahang;
    }

    public void setMahang(String mahang) {
        this.mahang = mahang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getGiaban() {
        return giaban;
    }

    public void setGiaban(String giaban) {
        this.giaban = giaban;
    }

    public String getGianhap() {
        return gianhap;
    }

    public void setGianhap(String gianhap) {
        this.gianhap = gianhap;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getTencongty() {
        return tencongty;
    }

    public void setTencongty(String tencongty) {
        this.tencongty = tencongty;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public String getNgaynhap() {
        return ngaynhap;
    }

    public void setNgaynhap(String ngaynhap) {
        this.ngaynhap = ngaynhap;
    }

    public int getLoai() {
        return loai;
    }

    public void setLoai(int loai) {
        this.loai = loai;
    }
}
