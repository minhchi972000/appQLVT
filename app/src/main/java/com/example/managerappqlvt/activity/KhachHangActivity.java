package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.KhachHangAdapter;
import com.example.managerappqlvt.model.EventBus.KhachHangEvent;
import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class KhachHangActivity extends AppCompatActivity {

    ImageView imgadd_KH;
    Toolbar toolbar;
    EditText edtsearch;
    TextView txt1;
    RecyclerView recyclerviewKH;
    EditText edtten, edtsdt, edtdiachi;
    KhachHangAdapter adapterKhachHang;
    List<KhachHang> khachHangList;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;
    KhachHang khachHangDelete = new KhachHang();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_khach_hang);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);

        initView();
        acTionBar();
        getData();
        initControl();

    }

    private void initControl() {
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0){
                        getData();
                }else {
                    getDataSearch(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getDataSearch(String name) {
        khachHangList.clear();
        compositeDisposable.add(apiQuanLi.searchkhachhang(name)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                khachHangModel -> {
                    if(khachHangModel.isSuccess()){
                        khachHangList =khachHangModel.getResult();
                        adapterKhachHang = new KhachHangAdapter(this,khachHangList);
                        recyclerviewKH.setAdapter(adapterKhachHang);
                    }else {
                        Toast.makeText(this, ""+khachHangModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                throwable -> {
                    Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
        ));
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        imgadd_KH = findViewById(R.id.imgadd_KH);
        edtsearch = findViewById(R.id.edtsearch);
        recyclerviewKH = findViewById(R.id.recyclerviewKH);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerviewKH.setLayoutManager(layoutManager);
        // list khachhang
        khachHangList = new ArrayList<>();



    }

    private void acTionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgadd_KH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertKhachHang();
            }
        });
    }

    private void getData(){
        compositeDisposable.add(apiQuanLi.getKhachHang()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        khachHangModel -> {
                            if(khachHangModel.isSuccess()){
                                khachHangList =khachHangModel.getResult();
                                adapterKhachHang = new KhachHangAdapter(this,khachHangList);
                                recyclerviewKH.setAdapter(adapterKhachHang);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void insertKhachHang() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_khach_hang, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(KhachHangActivity.this);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton("Nhập", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        edtten = view.findViewById(R.id.edtHoTen);
        edtsdt = view.findViewById(R.id.edtsdt);
        edtdiachi = view.findViewById(R.id.edtdiachi);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postData();
                    }
                });
            }
        });
        dialog.show();


    }

    private void postData() {
        String str_name = edtten.getText().toString().trim();
        String str_sdt = edtsdt.getText().toString().trim();
        String str_diachi = edtdiachi.getText().toString().trim();

        if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_sdt) || TextUtils.isEmpty(str_diachi)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            compositeDisposable.add(apiQuanLi.insertkhachhang(str_name, str_sdt, str_diachi)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            khachHangModel -> {
                                if (khachHangModel.isSuccess()) {
                                    Toast.makeText(this, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                                    getData();
                                    startActivity(new Intent(getApplicationContext(),KhachHangActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Error thêm khách hàng ", Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Log.d("log", throwable.getMessage());
                                Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Xóa")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set Title and Message:
            builder.setTitle("Delete").setMessage("Bạn muốn xóa khách hàng này không ?");
            //
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_delete);

            // Create "Yes" button with OnClickListener.
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (khachHangDelete != null) {
                        compositeDisposable.add(apiQuanLi.deletekhachhang(khachHangDelete.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {
                                            if (messageModel.isSuccess()) {
                                                getData();
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
                }
            });

            // Create "No" button with OnClickListener.
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            // Create AlertDialog:
            AlertDialog alert = builder.create();
            alert.show();



        } else if (item.getTitle().equals("Cập nhật")) {
            if (khachHangDelete != null) {
                // k editData(vatTuDelete);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_khach_hang, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(KhachHangActivity.this);
                builder.setView(view);
                builder.setCancelable(false);
                builder.setPositiveButton("Nhập", null);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                txt1 = view.findViewById(R.id.txt1);
                edtten = view.findViewById(R.id.edtHoTen);
                edtsdt = view.findViewById(R.id.edtsdt);
                edtdiachi = view.findViewById(R.id.edtdiachi);

                txt1.setText("Sửa thông tin khách hàng");
                edtten.setText(khachHangDelete.getHoten());
                edtsdt.setText(khachHangDelete.getSdt());
                edtdiachi.setText(khachHangDelete.getDiachi());

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateData();
                            }
                        });
                    }
                });
                dialog.show();
            }

        }
        return super.onContextItemSelected(item);
    }

    private void updateData() {
        String str_name = edtten.getText().toString().trim();
        String str_sdt = edtsdt.getText().toString().trim();
        String str_diachi = edtdiachi.getText().toString().trim();

        if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_sdt) || TextUtils.isEmpty(str_diachi)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            compositeDisposable.add(apiQuanLi.updatekh(khachHangDelete.getId(), str_name, str_sdt, str_diachi)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            khachHangModel -> {
                                if (khachHangModel.isSuccess()) {
                                    Toast.makeText(this, "Thêm khách hàng thành công", Toast.LENGTH_SHORT).show();
                                    getData();
                                    startActivity(new Intent(getApplicationContext(),KhachHangActivity.class));
                                    finish();

                                } else {
                                    Toast.makeText(this, "Error thêm khách hàng ", Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Log.d("log", throwable.getMessage());
                                Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        }

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
            khachHangDelete = event.getKhachHang();

        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}