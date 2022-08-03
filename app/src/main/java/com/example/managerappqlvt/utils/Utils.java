package com.example.managerappqlvt.utils;


import com.example.managerappqlvt.model.GioHang;

import com.example.managerappqlvt.model.QuanLi;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    //public static final String link = "minhchi.name.vn";
//     public static final String link = "192.168.1.223";
//    public static final String BASE_URL = "http://" + link + "/qlvtvattu/";
    //http://minhchitv.tk/
    public static final String link = "minhchitv.tk";
    public static final String BASE_URL = "http://" + link + "/qlvtvattu/";
  //  public static final String url = "http://" + link + "/qlvtvattu/images/";

    public static List<GioHang> manggiohang = new ArrayList<>();
    public static List<GioHang> mangmuahang = new ArrayList<>();

    public static QuanLi quanLi_current = new QuanLi();

    public static String trangThaiDonHang(int status) {
        String result = "";
        switch (status) {
            case 0:
                result = "Đơn hàng đang được xử lý";
                break;
            case 1:
                result = "Đơn hàng đã chấp nhận";
                break;
            case 2:
                result = "Đơn hàng đã giao cho đơn vị vận chuyển";
                break;
            case 3:
                result = "Thành công";
                break;
            case 4:
                result = "Đơn hàng đã hủy";
                break;
        }
        return result;
    }


}
