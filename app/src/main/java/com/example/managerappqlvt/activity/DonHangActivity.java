package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.DonHangAdapter;
import com.example.managerappqlvt.adapter.GioHangAdapter;
import com.example.managerappqlvt.model.DonHang;
import com.example.managerappqlvt.model.EventBus.CartEvent;
import com.example.managerappqlvt.model.EventBus.DonHangEvent;
import com.example.managerappqlvt.model.EventBus.GioHangEvent;
import com.example.managerappqlvt.model.EventBus.KhachHangEvent;
import com.example.managerappqlvt.model.EventBus.TinhTongEvent;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.Item;
import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DonHangActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView img_addDonHang;
    RecyclerView recyclerview_donhang;
    KhachHang khachHangCart;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;
    DonHang donHangDelete;


    TextView txtTenKhachHang;
    int tinhtrang, vatTuid;
    GioHang gioHang;
    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_don_hang);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);

        initView();
        acTionBar();
        getOder();

    }


    private void getOder() {
        compositeDisposable.add(apiQuanLi.xemdonhang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            DonHangAdapter donHangAdapter = new DonHangAdapter(getApplicationContext(), donHangModel.getResult());
                            recyclerview_donhang.setAdapter(donHangAdapter);
                            // donHangAdapter.notifyDataSetChanged();
                        }, throwable -> {
                            Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));

    }

    private void acTionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        img_addDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DonHangActivity.this, "Thêm đơn hàng", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), KhachHangActivity.class));
                Utils.mangmuahang.clear();
                finish();
            }
        });
    }

    public void initView() {

        toolbar = findViewById(R.id.toolbar);
        img_addDonHang = findViewById(R.id.img_addDonHang);

        recyclerview_donhang = findViewById(R.id.recyclerview_donhang);
        recyclerview_donhang.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview_donhang.setLayoutManager(layoutManager);


    }


    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = view.findViewById(R.id.spinnner_dialog);
        AppCompatButton btnDongy = view.findViewById(R.id.dongy_dialog);

        List<String> list = new ArrayList<>();
        list.add("Đơn hàng đang được xử lý");
        list.add("Đơn hàng đã chấp nhận");
        list.add("Đơn hàng đã giao cho đơn vị vận chuyển");
        list.add("Thành công");
        list.add("Đơn hàng đã hủy");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(Utils.trangThaiDonHang(donHangDelete.getTrangthai())));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tinhtrang = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDongy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capNhatDonHang();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

    }

    private void capNhatDonHang() {
        //donHang duoc bat o eventDonHang
        compositeDisposable.add(apiQuanLi.updatetinhtrang(donHangDelete.getId(), tinhtrang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOder();
                            // startActivity(new Intent(get));
                            dialog.dismiss();
                            //gui thong bAO
                            //  pushNotiToUser();
                        }, throwable -> {
                            Log.d("logError", throwable.getMessage());
                        }

                ));
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Xóa")) {
            if (donHangDelete != null) {
                compositeDisposable.add(apiQuanLi.deletedonhang(donHangDelete.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    if (messageModel.isSuccess()) {
                                        getOder();
                                        Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },

                                throwable -> {
                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                        ));
            }

        } else if (item.getTitle().equals("Cập nhật trạng thái giao hàng")) {
            if (donHangDelete != null) {
                // k editData(vatTuDelete);
                showCustomDialog();
            }

        }
        return super.onContextItemSelected(item);
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
    public void eventKhachHang(KhachHangEvent event) {
        if (event != null) {
            khachHangCart = event.getKhachHang();
            txtTenKhachHang.setText("Ten KH: " + khachHangCart.getHoten());
            vatTuid = khachHangCart.getId();
            updateSoluong();

        }
    }

    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDonHang(DonHangEvent event) {
        if (event != null) {
            donHangDelete = event.getDonHang();


        }
    }


    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventgiohang(GioHangEvent event) {
        if (event != null) {
            gioHang = event.getGioHang();

        }
    }


    private void updateSoluong() {
        for (int i = 0; i < Utils.mangmuahang.size(); i++) {
            for(int j=0;j<Utils.mangmuahang.get(i).getId();j++){
                if (Utils.mangmuahang.get(i).getId() == vatTuid) {
                    int soluongmua = Utils.mangmuahang.get(i).getSoluongmua();
                    int soluongkho = Utils.manggiohang.get(i).getSoluongkho();
                    Log.d("log1", String.valueOf(vatTuid));
                    Log.d("log1", String.valueOf(soluongmua));
                    Log.d("log1", String.valueOf(soluongkho));
                    compositeDisposable.add(apiQuanLi.updateSoluong(vatTuid, (soluongkho - soluongmua))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                                    }, throwable -> {
                                    }
                            ));
                }
            }
        }
    }


    //bat su kien
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TinhTongEvent event) {
        if (event != null) {

        }
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}