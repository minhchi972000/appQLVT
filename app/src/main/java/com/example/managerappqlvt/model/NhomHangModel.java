package com.example.managerappqlvt.model;

import java.util.List;

public class NhomHangModel {
    boolean success;
    String message;
    List<NhomHang> result;

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

    public List<NhomHang> getResult() {
        return result;
    }

    public void setResult(List<NhomHang> result) {
        this.result = result;
    }
}
