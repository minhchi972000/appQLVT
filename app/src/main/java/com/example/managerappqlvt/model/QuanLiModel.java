package com.example.managerappqlvt.model;

import java.util.List;


public class QuanLiModel {
    boolean success;
    String message;
    List<QuanLi> result;

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

    public List<QuanLi> getResult() {
        return result;
    }

    public void setResult(List<QuanLi> result) {
        this.result = result;
    }
}
