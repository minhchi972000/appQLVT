package com.example.managerappqlvt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import java.text.DecimalFormat;

public class ChiTietActivity extends AppCompatActivity {
    TextView tensp, giasp, mota, tencty, mahang, soluong, ngaynhap;
    Button btnthem;
    ImageView imghinhanh;
    Spinner spinner;
    Toolbar toolbar;
    VatTu vatTu;
    NotificationBadge badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);

        initView();
        ActionToolBar();
        initData();
        //   initControl();
    }

    private void initControl() {
        btnthem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                themgiohang();
                Toast.makeText(ChiTietActivity.this, "Thêm vào giỏ hàng thành công ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void themgiohang() {
        if (Utils.manggiohang.size() > 0) {
            boolean flag = false;
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());

            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                // dem so luong mat hang
                if (Utils.manggiohang.get(i).getId() == vatTu.getId()) {
                    Utils.manggiohang.get(i).setSoluongmua(soluong + Utils.manggiohang.get(i).getSoluongmua());
                    flag = true;
                }
            }
            if (flag == false) {
                long gia = Long.parseLong(vatTu.getGiaban());
                GioHang gioHang = new GioHang();
                gioHang.setGia(gia);
                gioHang.setSoluongmua(soluong);
                gioHang.setId(vatTu.getId());
                gioHang.setTenhang(vatTu.getTensp());
                gioHang.setHinhanh(vatTu.getHinhanh());
                gioHang.setMahang(vatTu.getMahang());
                gioHang.setNgaynhap(vatTu.getNgaynhap());
                Utils.manggiohang.add(gioHang);
            }
        } else {
            int soluong = Integer.parseInt(spinner.getSelectedItem().toString());
            long gia = Long.parseLong(vatTu.getGiaban());
            GioHang gioHang = new GioHang();
            gioHang.setGia(gia);
            gioHang.setSoluongmua(soluong);
            gioHang.setId(vatTu.getId());
            gioHang.setTenhang(vatTu.getTensp());
            gioHang.setMahang(vatTu.getMahang());
            gioHang.setNgaynhap(vatTu.getNgaynhap());
            gioHang.setHinhanh(vatTu.getHinhanh());
            Utils.manggiohang.add(gioHang);
        }
        // Hien thi so luong gio hang va duyet gio hang trong mang
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluongmua();// chay qua lay soluong gan vao total
        }
        badge.setText(String.valueOf(totalItem));
    }

    private void initData() {
        vatTu = (VatTu) getIntent().getSerializableExtra("chitiet");
        tensp.setText(vatTu.getTensp());
        mota.setText(vatTu.getMota());
        mahang.setText(vatTu.getMahang());
        tencty.setText(vatTu.getTencongty());
        ngaynhap.setText(vatTu.getNgaynhap());
        soluong.setText("Số lượng: " + vatTu.getSoluong());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        giasp.setText("Giá:" + decimalFormat.format(Double.parseDouble(vatTu.getGiaban())) + "đ");


        Glide.with(getApplicationContext()).load(vatTu.getHinhanh()).into(imghinhanh);
        if (vatTu.getHinhanh().contains("http")) {
            Glide.with(getApplicationContext()).load(vatTu.getHinhanh()).into(imghinhanh);
        } else {
            String hinh = Utils.BASE_URL + "images/" + vatTu.getHinhanh();
            Glide.with(getApplicationContext()).load(hinh).into(imghinhanh);

        }

    }

    private void initView() {
        tensp = findViewById(R.id.txttensp);
        tencty = findViewById(R.id.itemsp_tenct);
        mahang = findViewById(R.id.itemsp_mahang);
        ngaynhap = findViewById(R.id.itemsp_ngaynhap);
        soluong = findViewById(R.id.itemsp_soluong);
        giasp = findViewById(R.id.itemsp_giaban);
        mota = findViewById(R.id.txtmotachitiet);
        imghinhanh = findViewById(R.id.imgchitiet);
        toolbar = findViewById(R.id.toolbar);


    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}