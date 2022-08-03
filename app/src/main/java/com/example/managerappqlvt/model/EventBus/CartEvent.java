package com.example.managerappqlvt.model.EventBus;

import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.VatTu;

public class CartEvent {
    VatTu vatTu;

    public CartEvent(VatTu vatTu) {
        this.vatTu = vatTu;
    }

    public VatTu getVatTu() {
        return vatTu;
    }

    public void setVatTu(VatTu vatTu) {
        this.vatTu = vatTu;
    }
}
