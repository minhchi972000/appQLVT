package com.example.managerappqlvt.model;

import android.hardware.lights.LightState;

import java.util.List;

public class DonHang {
    int id;
    int idkhachhang;
    String hoten;
    String diachigiao;
    String ngaygiao;
    String sodienthoai;
    int soluong;
    long tongtien;
    int trangthai;
    List<Item> item;

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdkhachhang() {
        return idkhachhang;
    }

    public void setIdkhachhang(int idkhachhang) {
        this.idkhachhang = idkhachhang;
    }

    public String getDiachigiao() {
        return diachigiao;
    }

    public void setDiachigiao(String diachigiao) {
        this.diachigiao = diachigiao;
    }

    public String getNgaygiao() {
        return ngaygiao;
    }

    public void setNgaygiao(String ngaygiao) {
        this.ngaygiao = ngaygiao;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public long getTongtien() {
        return tongtien;
    }

    public void setTongtien(long tongtien) {
        this.tongtien = tongtien;
    }

    public int getTrangthai() {
        return trangthai;
    }

    public void setTrangthai(int trangthai) {
        this.trangthai = trangthai;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }
}
