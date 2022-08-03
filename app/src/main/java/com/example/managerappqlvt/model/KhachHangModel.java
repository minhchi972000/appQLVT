package com.example.managerappqlvt.model;

import java.util.List;

public class KhachHangModel {
    boolean success;
    String message;
    List<KhachHang> result;

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

    public List<KhachHang> getResult() {
        return result;
    }

    public void setResult(List<KhachHang> result) {
        this.result = result;
    }
}
