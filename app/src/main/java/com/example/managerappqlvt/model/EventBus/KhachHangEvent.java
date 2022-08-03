package com.example.managerappqlvt.model.EventBus;


import com.example.managerappqlvt.model.KhachHang;

public class KhachHangEvent {
    KhachHang khachHang;

    public KhachHangEvent(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }
}
