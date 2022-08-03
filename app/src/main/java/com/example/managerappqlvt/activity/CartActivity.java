package com.example.managerappqlvt.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.CartAdapter;
import com.example.managerappqlvt.adapter.VatTuAdapter;
import com.example.managerappqlvt.model.EventBus.CartEvent;
import com.example.managerappqlvt.model.EventBus.KhachHangEvent;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    Toolbar toolbar;
    NotificationBadge badge;
    RecyclerView recyclerviewCart;
    TextView txtHoten;

    KhachHang khachHangCart;
    CartAdapter adapterCart;
    List<VatTu> listCart;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;
    VatTu vatTu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);

        initView();
        acTionBar();
        getSpMoi();

    }

    private void getSpMoi() {
        compositeDisposable.add(apiQuanLi.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        vatTuModel -> {
                            if (vatTuModel.isSuccess()) {
                                listCart = vatTuModel.getResult();
                                adapterCart = new CartAdapter(getApplicationContext(), listCart);
                                recyclerviewCart.setAdapter(adapterCart);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever", Toast.LENGTH_SHORT).show();
                        }
                ));
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


    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        txtHoten = findViewById(R.id.txtHoten);
        badge = findViewById(R.id.menu_sl);
        recyclerviewCart = findViewById(R.id.recyclerviewCart);
        recyclerviewCart.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerviewCart.setLayoutManager(layoutManager);
        FrameLayout frameLayout = findViewById(R.id.framegiohang);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                EventBus.getDefault().postSticky(new KhachHangEvent(khachHangCart));
                EventBus.getDefault().postSticky(new CartEvent(vatTu));
                startActivity(giohang);
            }
        });

        listCart = new ArrayList<>();

        // cap nhat gio hang
        if (Utils.manggiohang != null) {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluongmua();// chay qua lay soluong gan vao total
            }
            badge.setText(String.valueOf(totalItem));
        }

    }

    // bat su kien event bus
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventKHCart(CartEvent event) {
        if (event != null) {
            int totalItem = 0;
            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                totalItem = totalItem + Utils.manggiohang.get(i).getSoluongmua();

            }
            badge.setText(String.valueOf(totalItem));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for (int i = 0; i < Utils.manggiohang.size(); i++) {
            totalItem = totalItem + Utils.manggiohang.get(i).getSoluongmua();
        }
        badge.setText(String.valueOf(totalItem));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventKhachHang(KhachHangEvent event) {
        if (event != null) {
            khachHangCart = event.getKhachHang();
            txtHoten.setText("Tên khách hàng: " + khachHangCart.getHoten());
            toolbar.setSubtitle(khachHangCart.getHoten());
            toolbar.setSubtitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}