package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationAttributes;
import android.view.View;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.ThuocSauAdapter;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ThuocSauActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ApiQuanLi apiQuanLi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int page = 1;
    int loai;
    ThuocSauAdapter adapterTS;
    List<VatTu> mangVatTuList;
    LinearLayoutManager linearLayoutManager;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thuoc_sau);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);
        loai = getIntent().getIntExtra("loai", 1);
        AnhXa();
        ActionToolBar();
        getData(page);
        addEventLoad();
    }


    private void AnhXa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview_tc);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        mangVatTuList = new ArrayList<>();
    }

    private void ActionToolBar() {
        if (loai == 1) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (loai == 2) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thuốc trừ cỏ"); //Thiết lập tiêu đề nếu muốn
        } else if (loai == 3) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Phân bón"); //Thiết lập tiêu đề nếu muốn
        } else if (loai == 4) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Dụng cụ"); //Thiết lập tiêu đề nếu muốn
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData(int page) {
        compositeDisposable.add(apiQuanLi.getSanPham(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        vatTuModel -> {
                            if (vatTuModel.isSuccess()) {
                                if (adapterTS == null) {
                                    mangVatTuList = vatTuModel.getResult();
                                    adapterTS = new ThuocSauAdapter(getApplicationContext(), mangVatTuList);
                                    recyclerView.setAdapter(adapterTS);
                                } else {
                                    int vitri = mangVatTuList.size() - 1;
                                    int soluongadd = vatTuModel.getResult().size();
                                    for (int i = 0; i < soluongadd; i++) {
                                        // sau khi co dlieu, duyet qua dlieu lay ve them vap mangsanphamMoiList
                                        mangVatTuList.add(vatTuModel.getResult().get(i));
                                    }
                                    // thong bao adapter add vao vi tri va so luong
                                    adapterTS.notifyItemRangeInserted(vitri, soluongadd);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Hết dữ liệu rồi", Toast.LENGTH_SHORT).show();
                                isLoading = true;
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever", Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void addEventLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isLoading == false) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == mangVatTuList.size() - 1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });

    }

    private void loadMore() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // add null
                mangVatTuList.add(null);
                adapterTS.notifyItemInserted(mangVatTuList.size() - 1);
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // remover null
                mangVatTuList.remove(mangVatTuList.size() - 1);
                adapterTS.notifyItemRemoved(mangVatTuList.size());
                page = page + 1;
                getData(page);
                adapterTS.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);
    }

}