package com.example.managerappqlvt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.GioHangAdapter;
import com.example.managerappqlvt.model.EventBus.CartEvent;
import com.example.managerappqlvt.model.EventBus.DonHangEvent;
import com.example.managerappqlvt.model.EventBus.GioHangEvent;
import com.example.managerappqlvt.model.EventBus.KhachHangEvent;
import com.example.managerappqlvt.model.EventBus.TinhTongEvent;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GioHangActivity extends AppCompatActivity {

    TextView giohangtrong, tongtien, ngaygiao, txtTenKhachHang, txtsodienthoai, nhapngaygiao, txtdiachi;
    EditText edtdiachi;
    LinearLayout layoutngay;
    Toolbar toolbar;
    Button btnmuahang;
    RecyclerView recyclerView;
    GioHangAdapter adapter;
    KhachHang khachHangCart;
    long tongtiensp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;
    GioHang gioHang;
    VatTu vatTu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gio_hang);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);
        Intent intent = getIntent();


        initView();
        initControl();
        Tinhtongtien();

    }


    private void Tinhtongtien() {
        tongtiensp = 0;
        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
            tongtiensp = tongtiensp + (Utils.mangmuahang.get(i).getGia() * Utils.mangmuahang.get(i).getSoluongmua());
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###" + "đ");
        tongtien.setText(decimalFormat.format(tongtiensp));
    }


    private void initControl() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //set ngay thang năm
        Calendar calendar = Calendar.getInstance();
        int dt = calendar.get(Calendar.DATE);
        int mt = calendar.get(Calendar.MONTH);
        int ye = calendar.get(Calendar.YEAR);
        calendar.set(ye, mt, dt);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        ngaygiao.setText("" + simpleDateFormat.format(calendar.getTime()));

        // neu gio hang trong
        if (Utils.manggiohang.size() == 0) {
            giohangtrong.setVisibility(View.VISIBLE);
        } else {
            adapter = new GioHangAdapter(getApplicationContext(), Utils.manggiohang); // list gio hang: Utils.manggiohang
            recyclerView.setAdapter(adapter);
        }

        //click dat hang
        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", new Gson().toJson(Utils.mangmuahang));
                String str_diachi = edtdiachi.getText().toString().trim();
                String str_ngay = ngaygiao.getText().toString().trim();
                int totalItem = 0;
                for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                    totalItem = totalItem + Utils.mangmuahang.get(i).getSoluongmua();
                }


                Log.d("logGson", new Gson().toJson(Utils.mangmuahang));
                Log.d("logtotalItem", String.valueOf(totalItem));


                if (TextUtils.isEmpty(str_diachi)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập địa chỉ ", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_ngay)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập ngày giao ", Toast.LENGTH_SHORT).show();
                } else {
                    compositeDisposable.add(apiQuanLi.createOder(khachHangCart.getId(), str_diachi, str_ngay, khachHangCart.getSdt(),
                                    totalItem, tongtiensp, new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if (messageModel.isSuccess()) {
                                            updateSL();
                                            EventBus.getDefault().postSticky(new KhachHangEvent(khachHangCart));
                                            EventBus.getDefault().postSticky(new TinhTongEvent());
                                            EventBus.getDefault().postSticky(new GioHangEvent(gioHang));
                                            Toast.makeText(getApplicationContext(), "Tạo đơn thành công", Toast.LENGTH_SHORT).show();
                                            Intent donhang = new Intent(getApplicationContext(), DonHangActivity.class);
                                            startActivity(donhang);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    }, throwable -> {
                                        Log.d("logoderget", throwable.getMessage());
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));


                }


            }
        });
        //click ngay
        layoutngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate();
            }
        });


    }

    private void updateSL() {
        int totalidvatu = 0;
        int totalsoluongkho = 0;
        int totalsoluongmua = 0;

        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
            totalidvatu = totalidvatu + Utils.mangmuahang.get(i).getId();
            totalsoluongmua = totalsoluongmua + Utils.mangmuahang.get(i).getSoluongmua();
            totalsoluongkho = totalsoluongkho + Utils.mangmuahang.get(i).getSoluongkho();

            int idvattu = totalidvatu;
            int slmua = totalsoluongmua;
            int slkho = totalsoluongkho;

            totalidvatu = 0;
            totalsoluongkho = 0;
            totalsoluongmua = 0;

            Log.d("log12", idvattu + "");
            Log.d("log12", slmua + "");
            Log.d("log12", slkho + "");

            compositeDisposable.add(apiQuanLi.updateSoluong(idvattu, (slkho - slmua))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()) {
                                    //   Toast.makeText(getApplicationContext(), "update số lượng thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Không thành công", Toast.LENGTH_SHORT).show();
                                }
                            }, throwable -> {
                                Log.d("log1", throwable.getMessage());
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));

        }

    }


    private void addDate() {
        Calendar calendar = Calendar.getInstance();
        int dt = calendar.get(Calendar.DATE);
        int mt = calendar.get(Calendar.MONTH);
        int ye = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                ngaygiao.setText("" + simpleDateFormat.format(calendar.getTime()));
            }
        }, ye, mt, dt);
        datePickerDialog.show();
    }


    public void initView() {
        giohangtrong = findViewById(R.id.txtgiohangtrong);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerviewgiohang);
        btnmuahang = findViewById(R.id.btnmuahang);
        ngaygiao = findViewById(R.id.ngaygiao);
        nhapngaygiao = findViewById(R.id.nhapngaygiao);
        layoutngay = findViewById(R.id.layoutngay);
        edtdiachi = findViewById(R.id.edtdiachi);
        txtTenKhachHang = findViewById(R.id.txtTenKhachHang);
        txtsodienthoai = findViewById(R.id.txtsodienthoai);
        txtdiachi = findViewById(R.id.txtdiachi);
    }

    // su dung thu vien eventBus
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event) {
        if (event != null) {
            Tinhtongtien();
        }
    }

    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventGiohnag(GioHangEvent event) {
        if (event != null) {
            gioHang = event.getGioHang();
        }
    }


    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventKhachHang(KhachHangEvent event) {
        if (event != null) {
            khachHangCart = event.getKhachHang();
            setInfoKhachHang();
        }
    }

    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventCart(CartEvent event) {
        if (event != null) {
            vatTu = event.getVatTu();
            int idvattu = vatTu.getId();
            Log.d("logidvt", idvattu + "");

        }
    }


    private void setInfoKhachHang() {
        txtTenKhachHang.setText("Khách Hàng: " + khachHangCart.getHoten());
        txtsodienthoai.setText("Sdt: " + khachHangCart.getSdt());
        txtdiachi.setText("Địa chỉ: " + khachHangCart.getDiachi());
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}