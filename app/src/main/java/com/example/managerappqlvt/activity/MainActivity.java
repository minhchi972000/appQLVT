package com.example.managerappqlvt.activity;

import static com.example.managerappqlvt.R.color.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.KhachHangAdapter;
import com.example.managerappqlvt.adapter.VatTuAdapter;
import com.example.managerappqlvt.model.EventBus.DeleteEvent;

import com.example.managerappqlvt.model.NhomHang;
import com.example.managerappqlvt.model.QuanLi;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    ImageView imgadd;
    EditText edtsearch;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    int idnhom;

    //array nhom hang
    List<String> nhomhang = new ArrayList<>();
    //array spiner nhom hang
    List<NhomHang> nhomList = new ArrayList<>();
    //array spiner loai
    List<String> list = new ArrayList<>();
    TextView txtnhap;
    EditText edtngay, edtmahang, edttenhang, edtgianhap, edtgiaban, edtsoluong, edttencongty, mota;
    ImageView imgcamera;


    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;

    VatTuAdapter adapterVatTu;
    List<VatTu> mangVatTu;
    int loai;

    VatTu vatTuDelete;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://managerappqlvt.appspot.com");
    final int REQUEST_CODE_FOLDER = 456;
    String realpath = "";
    String str_link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);

        AnhXa();
        ActionBar();
        if (isConnected(this)) {
            Toast.makeText(getApplicationContext(), "Kết nối thành công !!", Toast.LENGTH_SHORT).show();
            getDataNhom();
            getSpMoi();
            initControl();

        } else {
            Toast.makeText(getApplicationContext(), "Không có Wifi/4G !!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initControl() {
        imgadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //flag = false;
                insertNewData();
            }
        });

        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    getSpMoi();
                } else {
                    getDataSearch(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // initview
    private void AnhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        imgadd = findViewById(R.id.imgadd);
        edtsearch = findViewById(R.id.edtsearch);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerViewManHinhChinh = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        //khoi tao mang cho Recyclerview=> getSpMoi lay du lieu ra
        mangVatTu = new ArrayList<>();

        View headview = navigationView.getHeaderView(0);
        TextView email = headview.findViewById(R.id.txtEmailQuanLi);
        TextView name = headview.findViewById(R.id.txtTenQuanLi);
        QuanLi quanLi = Paper.book().read("login");
        email.setText(quanLi.getEmail() + " ");
        name.setText(quanLi.getHoten() + "");


    }

    //actionbar
    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.icon_menu_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    //get Nhom hang
    private void getDataNhom() {
        nhomhang.add("Chọn nhóm");
        compositeDisposable.add(apiQuanLi.getNhom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        nhomModel -> {
                            if (nhomModel.isSuccess()) {
                                nhomList = nhomModel.getResult();
                                for (int i = 0; i < nhomModel.getResult().size(); i++) {
                                    nhomhang.add(nhomModel.getResult().get(i).getNhomhang());
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Không get được data", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Log.d("test", "getDataNhom: " + throwable.getMessage());
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                ));

    }

    // get sanpham
    private void getSpMoi() {
        compositeDisposable.add(apiQuanLi.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        vatTuModel -> {
                            if (vatTuModel.isSuccess()) {
                                mangVatTu = vatTuModel.getResult();
                                adapterVatTu = new VatTuAdapter(getApplicationContext(), mangVatTu);
                                recyclerViewManHinhChinh.setAdapter(adapterVatTu);
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với sever", Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void getDataSearch(String name) {
        mangVatTu.clear();
        compositeDisposable.add(apiQuanLi.searchvattu(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        vatTuModel -> {
                            if (vatTuModel.isSuccess()) {
                                mangVatTu = vatTuModel.getResult();
                                adapterVatTu = new VatTuAdapter(getApplicationContext(), mangVatTu);
                                recyclerViewManHinhChinh.setAdapter(adapterVatTu);
                            } else {
                                Toast.makeText(this, "" + vatTuModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    //insert data
    private void insertNewData() {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add, null);
        Spinner spinnerNhomHang = view.findViewById(R.id.spinnhomhang);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nhomhang);
        spinnerNhomHang.setAdapter(adapter);
        spinnerNhomHang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    idnhom = 0;
                } else {
                    idnhom = nhomList.get(i - 1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //spinner loai
        Spinner spinnerLoai = view.findViewById(R.id.spinloai);
        list.add("Vui lòng chọn loại hàng");
        list.add("Loại 1: Thuốc trừ sâu");
        list.add("Loại 2: Thuốc trừ cỏ");
        list.add("Loại 3: Phân bón");
        list.add("Loại 4: Dụng cụ");
        ArrayAdapter<String> adapterloai = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinnerLoai.setAdapter(adapterloai);
        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //date
        edtngay = view.findViewById(R.id.edtngaynhap);
        edtngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate();
            }
        });
        //init view
        edtmahang = view.findViewById(R.id.edtmahang);
        edttenhang = view.findViewById(R.id.edttenhang);
        edtgianhap = view.findViewById(R.id.edtgianhap);
        edtgiaban = view.findViewById(R.id.edtgiaban);
        edtsoluong = view.findViewById(R.id.edtsoluong);
        edttencongty = view.findViewById(R.id.edttencongty);
        mota = view.findViewById(R.id.mota);
        imgcamera = view.findViewById(R.id.imgcamera);

        imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);// laays hinh ra
                intent.setType("image/*");// mo lay file noi chuwa hinh moi lay
                startActivityForResult(intent, REQUEST_CODE_FOLDER);
            }
        });


        //alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Nhập", null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
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

    // add ngay
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
                edtngay.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, ye, mt, dt);
        datePickerDialog.show();
    }

    // post Insert data
    private void postData() {

        String str_mahang = edtmahang.getText().toString().trim();
        String str_tenhang = edttenhang.getText().toString().trim();
        String str_cty = edttencongty.getText().toString().trim();
        String str_mota = mota.getText().toString().trim();
        String str_giaban = edtgiaban.getText().toString().trim();
        String str_gianhap = edtgianhap.getText().toString().trim();
        String str_soluong = edtsoluong.getText().toString().trim();
        String str_ngay = edtngay.getText().toString().trim();
        if (TextUtils.isEmpty(str_mahang)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mã hàng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_tenhang)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên hàng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_cty)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập nơi sản xuất", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_mota)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mô tả", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_giaban)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập giá bán", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_gianhap)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập giá nhập", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_soluong)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số lượng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_ngay)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập ngày", Toast.LENGTH_SHORT).show();
        } else if (idnhom == 0) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn nhóm", Toast.LENGTH_SHORT).show();
        } else if (loai == 0) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn loại hàng", Toast.LENGTH_SHORT).show();
        } else if ((idnhom == 1) != (loai == 1 || loai == 2)) {
            Toast.makeText(getApplicationContext(), "Nhóm Thuốc không trùng với loại hàng", Toast.LENGTH_SHORT).show();
        } else if ((idnhom == 2) != (loai == 3)) {
            Toast.makeText(getApplicationContext(), "Nhóm Phân bón không trùng với loại hàng", Toast.LENGTH_SHORT).show();
        } else if ((idnhom == 3) != (loai == 4)) {
            Toast.makeText(getApplicationContext(), "Nhóm dụng cụ không trùng với loại hàng", Toast.LENGTH_SHORT).show();
        } else {
            compositeDisposable.add(apiQuanLi.insert(idnhom, str_mahang, str_tenhang, str_cty, str_link,
                            str_mota, str_gianhap, str_giaban, Integer.parseInt(str_soluong), str_ngay, loai)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "Thêm vật tư thành công", Toast.LENGTH_SHORT).show();
                                    getSpMoi();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    Log.d("logmess", messageModel.getMessage());
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Log.d("logth", throwable.getMessage());
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));

        }
    }

    //edit data
    private void editData(VatTu vatTu) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add, null);
        Spinner spinnerNhomHang = view.findViewById(R.id.spinnhomhang);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nhomhang);
        spinnerNhomHang.setAdapter(adapter);
        spinnerNhomHang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    idnhom = 0;
                } else {
                    idnhom = nhomList.get(i - 1).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //spinner loai
        Spinner spinnerLoai = view.findViewById(R.id.spinloai);
        List<String> list = new ArrayList<>();
        list.add("Vui lòng chọn loại hàng");
        list.add("Loại 1: Thuốc trừ sâu");
        list.add("Loại 2: Thuốc trừ cỏ");
        list.add("Loại 3: Phân bón");
        list.add("Loại 4: Dụng cụ");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinnerLoai.setAdapter(adapter1);
        spinnerLoai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loai = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //date
        edtngay = view.findViewById(R.id.edtngaynhap);
        edtngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate();
            }
        });
        //init view
        txtnhap = view.findViewById(R.id.txtnhap);
        edtmahang = view.findViewById(R.id.edtmahang);
        edttenhang = view.findViewById(R.id.edttenhang);
        edtgianhap = view.findViewById(R.id.edtgianhap);
        edtgiaban = view.findViewById(R.id.edtgiaban);
        edtsoluong = view.findViewById(R.id.edtsoluong);
        edttencongty = view.findViewById(R.id.edttencongty);
        mota = view.findViewById(R.id.mota);
        imgcamera = view.findViewById(R.id.imgcamera);


        //set data
        txtnhap.setText("Sửa sản phẩm");
        edtmahang.setText(vatTu.getMahang());
        edttenhang.setText(vatTu.getTensp());
        edtgianhap.setText(vatTu.getGianhap() + "");
        edtgiaban.setText(vatTu.getGiaban() + "");
        edtsoluong.setText(vatTu.getSoluong() + "");
        edttencongty.setText(vatTu.getTencongty() + "");
        mota.setText(vatTu.getMota() + "");
        edtngay.setText(vatTu.getNgaynhap());
        spinnerLoai.setSelection(vatTu.getLoai());

        for (int i = 0; i < nhomList.size(); i++) {
            if (vatTu.getIdnhom() == nhomList.get(i).getId()) {
                spinnerNhomHang.setSelection(adapter.getPosition(nhomList.get(i).getNhomhang()));
            }
        }

        imgcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);// laays hinh ra
                intent.setType("image/*");// mo lay file noi chuwa hinh moi lay
                startActivityForResult(intent, REQUEST_CODE_FOLDER);
            }
        });

        Glide.with(getApplicationContext()).load(vatTu.getHinhanh()).into(imgcamera);


        //alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Nhập", null);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setBackgroundColor(teal);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateData(vatTu);
                    }
                });
            }
        });
        dialog.show();

    }

    // upload data
    private void updateData(VatTu vatTu) {

        String str_mahang = edtmahang.getText().toString().trim();
        String str_tenhang = edttenhang.getText().toString().trim();
        String str_cty = edttencongty.getText().toString().trim();
        String str_mota = mota.getText().toString().trim();
        String str_giaban = edtgiaban.getText().toString().trim();
        String str_gianhap = edtgianhap.getText().toString().trim();
        String str_soluong = edtsoluong.getText().toString().trim();
        String str_ngay = edtngay.getText().toString().trim();


        if (TextUtils.isEmpty(str_mahang)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mã hàng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_tenhang)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập tên hàng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_cty)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập nơi sản xuất", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_mota)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập mô tả", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_giaban)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập giá bán", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_gianhap)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập giá nhập", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_soluong)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập số lượng", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_ngay)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập ngày", Toast.LENGTH_SHORT).show();
        } else if (idnhom == 0) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn nhóm", Toast.LENGTH_SHORT).show();
        } else if (loai == 0) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn loại hàng", Toast.LENGTH_SHORT).show();
        } else if ((idnhom == 1) != (loai == 1 || loai == 2)) {
            Toast.makeText(getApplicationContext(), "Nhóm Thuốc không trùng với loại hàng", Toast.LENGTH_SHORT).show();
        } else if ((idnhom == 2) != (loai == 3)) {
            Toast.makeText(getApplicationContext(), "Nhóm Phân bón không trùng với loại hàng", Toast.LENGTH_SHORT).show();
        } else if ((idnhom == 3) != (loai == 4)) {
            Toast.makeText(getApplicationContext(), "Nhóm dụng cụ không trùng với loại hàng", Toast.LENGTH_SHORT).show();
        } else {
            compositeDisposable.add(apiQuanLi.updatevattu(vatTu.getId(), idnhom, str_mahang, str_tenhang, str_cty, str_link,
                            str_mota, str_gianhap, str_giaban, Integer.parseInt(str_soluong), str_ngay, loai)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                if (messageModel.isSuccess()) {
                                    Toast.makeText(getApplicationContext(), "Update thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    Log.d("logmess", messageModel.getMessage());
                                    Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> {
                                Log.d("logth", throwable.getMessage());
                                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    ));
        }

    }

    //get Uri
    private String getPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    //upLoad Image
    private void uploadMultipleFiles() {
        Calendar calendar = Calendar.getInstance();
        StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");
        // Get the data from an ImageView as bytes
        imgcamera.setDrawingCacheEnabled(true);
        imgcamera.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgcamera.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> result = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String photoStringLink = uri.toString();
                        str_link = photoStringLink;
                        Log.d("TTT", photoStringLink);
                        Toast.makeText(MainActivity.this, "Thêm thành công !!!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    // kiemtra co internet
    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Xóa")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set Title and Message:
            builder.setTitle("Delete").setMessage("Bạn muốn xóa sản phẩm này không ?");
            //
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_delete);

            // Create "Yes" button with OnClickListener.
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (vatTuDelete != null) {
                        compositeDisposable.add(apiQuanLi.delete(vatTuDelete.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {
                                            if (messageModel.isSuccess()) {
                                                getSpMoi();
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






        } else if (item.getTitle().equals("Sửa")) {
            if (vatTuDelete != null) {
                //flag = true;
                editData(vatTuDelete);
            }

        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent((Intent.ACTION_GET_CONTENT));
            // intent.setType("image/*");// mo lay file noi chuwa hinh moi lay
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_FOLDER);

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            realpath = getPath(uri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgcamera.setImageBitmap(bitmap);

                uploadMultipleFiles();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(trangchu);
                break;
            case R.id.nav_trusau:
                Intent trusau = new Intent(getApplicationContext(), ThuocSauActivity.class);
                trusau.putExtra("loai", 1);
                startActivity(trusau);
                break;
            case R.id.nav_truco:
                Intent truco = new Intent(getApplicationContext(), ThuocSauActivity.class);
                truco.putExtra("loai", 2);
                startActivity(truco);
                break;
            case R.id.nav_phanbon:
                Intent phanbon = new Intent(getApplicationContext(), ThuocSauActivity.class);
                phanbon.putExtra("loai", 3);
                startActivity(phanbon);
                break;
            case R.id.nav_dungcu:
                Intent dungcu = new Intent(getApplicationContext(), ThuocSauActivity.class);
                dungcu.putExtra("loai", 4);
                startActivity(dungcu);
                break;
            case R.id.nav_nhapdon:
                insertNewData();
                break;
            case R.id.nav_khachhang:
                startActivity(new Intent(getApplicationContext(), KhachHangActivity.class));
                break;
            case R.id.nav_donhang:
                startActivity(new Intent(getApplicationContext(), DonHangActivity.class));
                break;
            case R.id.nav_quanli:
                startActivity(new Intent(getApplicationContext(), QuanLiActivity.class));
                break;
            case R.id.nav_dangxuat:
                Paper.book().delete("login");
                FirebaseAuth.getInstance().signOut();
                Intent dangxuat = new Intent(getApplicationContext(), DangNhapActivity.class);
                startActivity(dangxuat);
                finish();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // bat su kien eventbus
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDelete(DeleteEvent event) {
        if (event != null) {
            vatTuDelete = event.getVatTu();
            Log.d("test", vatTuDelete.getId() + " nhan duoc");
        }

    }

    // khoi tao
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}