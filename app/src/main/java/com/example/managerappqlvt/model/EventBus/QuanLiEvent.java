package com.example.managerappqlvt.model.EventBus;


import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.model.QuanLi;

public class QuanLiEvent {
    QuanLi quanLi;

    public QuanLiEvent(QuanLi quanLi) {
        this.quanLi = quanLi;
    }

    public QuanLi getQuanLi() {
        return quanLi;
    }

    public void setQuanLi(QuanLi quanLi) {
        this.quanLi = quanLi;
    }
}
