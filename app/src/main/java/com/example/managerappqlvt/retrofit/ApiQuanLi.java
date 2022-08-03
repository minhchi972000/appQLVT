package com.example.managerappqlvt.retrofit;


import com.example.managerappqlvt.model.DonHangModel;
import com.example.managerappqlvt.model.KhachHangModel;
import com.example.managerappqlvt.model.MessageModel;
import com.example.managerappqlvt.model.NhomHangModel;
import com.example.managerappqlvt.model.QuanLiModel;
import com.example.managerappqlvt.model.VatTuModel;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

// tao class LoaiSpModel kiem tra lay ve
public interface ApiQuanLi {
    // GET DATA
    @GET("nhomget.php")
    Observable<NhomHangModel> getNhom();// Thuoc, phan bon, dung cu

    @GET("vattuget.php")
    Observable<VatTuModel> getSpMoi(); // tat ca

    @GET("khachhangget.php")
    Observable<KhachHangModel> getKhachHang(); // tat ca

    @POST("vattuchitietloai.php")
    @FormUrlEncoded
    Observable<VatTuModel> getSanPham(
            @Field("page") int page,
            @Field("loai") int loai
    );

    @POST("vattuinsert.php")
    @FormUrlEncoded
    Observable<MessageModel> insert(
            @Field("idnhom") int id,
            @Field("mahang") String mahang,
            @Field("tensp") String tensp,
            @Field("tencongty") String tencongty,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("gianhap") String gianhap,
            @Field("giaban") String giaban,
            @Field("soluong") int soluong,
            @Field("ngaynhap") String ngaynhap,
            @Field("loai") int loai
    );

    @POST("vattudelete.php")
    @FormUrlEncoded
    Observable<MessageModel> delete(
            @Field("id") int id
    );

    @POST("vattuupdate.php")
    @FormUrlEncoded
    Observable<MessageModel> updatevattu(
            @Field("id") int id,
            @Field("idnhom") int idnhom,
            @Field("mahang") String mahang,
            @Field("tensp") String tensp,
            @Field("tencongty") String tencongty,
            @Field("hinhanh") String hinhanh,
            @Field("mota") String mota,
            @Field("gianhap") String gianhap,
            @Field("giaban") String giaban,
            @Field("soluong") int soluong,
            @Field("ngaynhap") String ngaynhap,
            @Field("loai") int loai
    );

    @POST("vattuupdatesoluong.php")
    @FormUrlEncoded
    Observable<MessageModel> updateSoluong(
            @Field("id") int id,
            @Field("soluong") int soluong

    );


    @POST("vattusearch.php")
    @FormUrlEncoded
    Observable<VatTuModel> searchvattu(
            @Field("search") String search
    );


    @POST("khachhanginsert.php")
    @FormUrlEncoded
    Observable<KhachHangModel> insertkhachhang(
            @Field("hoten") String hoten,
            @Field("sdt") String sdt,
            @Field("diachi") String diachi
    );

    @POST("khachhangdelete.php")
    @FormUrlEncoded
    Observable<MessageModel> deletekhachhang(
            @Field("id") int id
    );

    @POST("khachhangupdate.php")
    @FormUrlEncoded
    Observable<MessageModel> updatekh(
            @Field("id") int id,
            @Field("hoten") String hoten,
            @Field("sdt") String sdt,
            @Field("diachi") String diachi
    );

    @POST("khachhangsearch.php")
    @FormUrlEncoded
    Observable<KhachHangModel> searchkhachhang(
            @Field("search") String search
    );

    @POST("donhang.php")
    @FormUrlEncoded
    Observable<MessageModel> createOder(

            @Field("idkhachhang") int id,
            @Field("diachigiao") String diachigiao,
            @Field("ngaygiao") String ngaygiao,
            @Field("sodienthoai") String sodienthoai,
            @Field("soluong") int soluong,
            @Field("tongtien") long tongtien,
            @Field("chitiet") String chitiet

    );

    @POST("donhangget.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemdonhang(
            @Field("idkhachhang") int id
    );

    @POST("donhangdelete.php")
    @FormUrlEncoded
    Observable<MessageModel> deletedonhang(
            @Field("id") int id
    );

    @POST("donhangtinhtrang.php")
    @FormUrlEncoded
    Observable<MessageModel> updatetinhtrang(
            @Field("id") int id,
            @Field("trangthai") int trangthai

    );

    @Multipart
    @POST("imageupload.php")
    Call<MessageModel> uploadFile(@Part MultipartBody.Part file);


    @POST("login.php")
    @FormUrlEncoded
    Observable<QuanLiModel> login(
            @Field("email") String hoten,
            @Field("pass") String pass
    );

    @POST("resetpass.php")
    @FormUrlEncoded
    Observable<QuanLiModel> resetPass(
            @Field("email") String email
    );

    @GET("quanliget.php")
    Observable<QuanLiModel> getQuanLi(); // tat ca

    @POST("quanliinsert.php")
    @FormUrlEncoded
    Observable<QuanLiModel> insertquanli(
            @Field("hoten") String hoten,
            @Field("sdt") String sdt,
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("uid") String uid
    );

    @POST("quanliupdate.php")
    @FormUrlEncoded
    Observable<QuanLiModel> updatequanli(
            @Field("id") int id,
            @Field("hoten") String hoten,
            @Field("sdt") String sdt,
            @Field("email") String email,
            @Field("pass") String pass
    );

    @POST("quanliupdatePassword.php")
    @FormUrlEncoded
    Observable<QuanLiModel> updatequanlipass(
            @Field("id") int id,
            @Field("pass") String pass
    );

    @POST("quanlidelete.php")
    @FormUrlEncoded
    Observable<MessageModel> deletequanli(
            @Field("id") int id
    );


}
