package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.managerappqlvt.adapter.QuanLiAdapter;
import com.example.managerappqlvt.model.EventBus.KhachHangEvent;
import com.example.managerappqlvt.model.EventBus.QuanLiEvent;
import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.model.QuanLi;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuanLiActivity extends AppCompatActivity {
    ImageView imgadd_KH;
    Toolbar toolbar;
    RecyclerView recyclerviewKH;
    EditText edtten, edtsdt, edtdiachi, edtemail, edtpass;
    CountryCodePicker ccp;
    QuanLiAdapter adapterQuanLi;
    List<QuanLi> quanliList;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;
    QuanLi quanLiDelete;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    int totalId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_li);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);

        initView();
        acTionBar();
        getData();

    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        imgadd_KH = findViewById(R.id.imgadd_KH);
        recyclerviewKH = findViewById(R.id.recyclerviewKH);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerviewKH.setLayoutManager(layoutManager);
        // list khachhang
        quanliList = new ArrayList<>();


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

    private void getData() {
        compositeDisposable.add(apiQuanLi.getQuanLi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        quanLiModel -> {
                            if (quanLiModel.isSuccess()) {
                                quanliList = quanLiModel.getResult();
                                adapterQuanLi = new QuanLiAdapter(this, quanliList);
                                recyclerviewKH.setAdapter(adapterQuanLi);
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void insertKhachHang() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_quan_li, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(QuanLiActivity.this);
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
        edtemail = view.findViewById(R.id.edtemail);
        edtpass = view.findViewById(R.id.edtpass);

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
        String str_email = edtemail.getText().toString().trim();
        String str_pass = edtpass.getText().toString().trim();

        if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_sdt) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_email)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (!str_email.contains("@gmail.com")) {
                Toast.makeText(getApplicationContext(), "Email phải là @gmail.com", Toast.LENGTH_LONG).show();
                return;
            }
            if (str_pass.length() < 4) {
                Toast.makeText(getApplicationContext(), "Pass phải có ít nhất 4 kí tự", Toast.LENGTH_LONG).show();
                return;
            }

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(str_email, str_pass)
                    .addOnCompleteListener(QuanLiActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    postData(str_name, str_sdt, str_email, str_pass, user.getUid());

                                }
                            } else {
                                Toast.makeText(QuanLiActivity.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    private void postData(String str_name, String str_sdt, String str_email, String str_pass, String uid) {
        // post data
        compositeDisposable.add(apiQuanLi.insertquanli(str_name, str_sdt, str_email, str_pass, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        quanLiModel -> {
                            if (quanLiModel.isSuccess()) {
                                // Successfull, update Ui with the sign-in user's information
                                // using HashMap

                                HashMap<Object, String> hashMap = new HashMap<>();
                                // put info trong hasmap
                                hashMap.put("name", str_name);//will add later in edit profile
                                hashMap.put("sdt", str_sdt);//will add later in edit profile
                                hashMap.put("pass", str_pass);//will add later in edit profile
                                hashMap.put("email", str_email);
                                hashMap.put("uid", uid);

                                // firebasedatabase instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Quanli");
                                // put data within hashmap in database
                                reference.child(str_sdt).setValue(hashMap);

                                Utils.quanLi_current.setEmail(str_email);
                                Utils.quanLi_current.setPass(str_pass);
                                Toast.makeText(this, "Thêm quản lí thành công", Toast.LENGTH_SHORT).show();
                                getData();
                                startActivity(new Intent(getApplicationContext(), QuanLiActivity.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), quanLiModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Xóa")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set Title and Message:
            builder.setTitle("Xóa quản lí").setMessage("Bạn muốn xóa quản lí này không ?");
            //
            builder.setCancelable(true);
            builder.setIcon(R.drawable.ic_delete);

            // Create "Yes" button with OnClickListener.
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (quanLiDelete != null) {
                        compositeDisposable.add(apiQuanLi.deletequanli(quanLiDelete.getId())
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
            if (quanLiDelete != null) {
                // k editData(vatTuDelete);
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_quan_li, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(QuanLiActivity.this);
                builder.setView(view);
                builder.setCancelable(false);
                builder.setPositiveButton("Nhập", null);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                TextView txt1 = view.findViewById(R.id.txt1);
                edtten = view.findViewById(R.id.edtHoTen);
                edtsdt = view.findViewById(R.id.edtsdt);
                edtemail = view.findViewById(R.id.edtemail);
                edtpass = view.findViewById(R.id.edtpass);


                txt1.setText("Sửa thông tin quản lí");
                edtten.setText(quanLiDelete.getHoten());
                edtsdt.setText(quanLiDelete.getSdt());
                edtemail.setText(quanLiDelete.getEmail());
                edtpass.setText(quanLiDelete.getPass());

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
        String str_email = edtemail.getText().toString().trim();
        String str_pass = edtpass.getText().toString().trim();

        if (TextUtils.isEmpty(str_name) || TextUtils.isEmpty(str_sdt) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_email)) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            if (!str_email.contains("@gmail.com")) {
                Toast.makeText(getApplicationContext(), "Email phải là @gmail.com", Toast.LENGTH_LONG).show();
                return;
            }
            if (str_pass.length() < 4) {
                Toast.makeText(getApplicationContext(), "Pass phải có ít nhất 4 kí tự", Toast.LENGTH_LONG).show();
                return;
            }
            compositeDisposable.add(apiQuanLi.updatequanli(quanLiDelete.getId(), str_name, str_sdt, str_email, str_pass)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            khachHangModel -> {
                                if (khachHangModel.isSuccess()) {
                                    Toast.makeText(this, "Thêm quản lí thành công", Toast.LENGTH_SHORT).show();
                                    getData();
                                    startActivity(new Intent(getApplicationContext(), QuanLiActivity.class));
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
    public void eventQuanLi(QuanLiEvent event) {
        if (event != null) {
            quanLiDelete = event.getQuanLi();

        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


}