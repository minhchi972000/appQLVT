package com.example.managerappqlvt.model;

import java.util.List;

public class VatTuModel {
    boolean success;
    String message;
    List<VatTu> result;

    public VatTuModel(boolean success, String message, List<VatTu> result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<VatTu> getResult() {
        return result;
    }

    public void setResult(List<VatTu> result) {
        this.result = result;
    }
}
