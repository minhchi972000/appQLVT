package com.example.managerappqlvt.model.EventBus;


import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.KhachHang;

public class GioHangEvent {
   GioHang gioHang;

    public GioHangEvent(GioHang gioHang) {
        this.gioHang = gioHang;
    }

    public GioHang getGioHang() {
        return gioHang;
    }

    public void setGioHang(GioHang gioHang) {
        this.gioHang = gioHang;
    }
}
