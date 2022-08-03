package com.example.managerappqlvt.model.EventBus;


import com.example.managerappqlvt.model.VatTu;

public class DeleteEvent {
    VatTu vatTu;

    public DeleteEvent(VatTu vatTu) {
        this.vatTu = vatTu;
    }

    public VatTu getVatTu() {
        return vatTu;
    }

    public void setVatTu(VatTu vatTu) {
        this.vatTu = vatTu;
    }
}
