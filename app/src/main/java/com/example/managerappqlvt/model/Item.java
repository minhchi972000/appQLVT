package com.example.managerappqlvt.model;

public class Item {
    int idvattu;
    String tensp;
    int soluongmua;
    int soluongkho;
    long giaban;
    String hinhanh;

    public Item(int idvattu, String tensp, int soluongmua, int soluongkho, long giaban, String hinhanh) {
        this.idvattu = idvattu;
        this.tensp = tensp;
        this.soluongmua = soluongmua;
        this.soluongkho = soluongkho;
        this.giaban = giaban;
        this.hinhanh = hinhanh;
    }

    public int getSoluongkho() {
        return soluongkho;
    }

    public void setSoluongkho(int soluongkho) {
        this.soluongkho = soluongkho;
    }

    public int getIdvattu() {
        return idvattu;
    }

    public void setIdvattu(int idvattu) {
        this.idvattu = idvattu;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public int getSoluongmua() {
        return soluongmua;
    }

    public void setSoluongmua(int soluongmua) {
        this.soluongmua = soluongmua;
    }

    public long getGiaban() {
        return giaban;
    }

    public void setGiaban(long giaban) {
        this.giaban = giaban;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }
}