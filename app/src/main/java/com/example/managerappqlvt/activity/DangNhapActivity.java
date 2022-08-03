package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.adapter.QuanLiAdapter;
import com.example.managerappqlvt.model.EventBus.QuanLiEvent;
import com.example.managerappqlvt.model.QuanLiFirebaseModel;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {

    TextView txtresetpass;
    EditText email, pass;
    AppCompatButton btndangnhap;
    ApiQuanLi apiQuanLi;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    FirebaseAuth firebaseAuth ;
    FirebaseUser quanli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();
    }

    private void initView() {
        Paper.init(this);
        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        txtresetpass = findViewById(R.id.txtresetpass);
        btndangnhap = findViewById(R.id.btndangnhap);

        firebaseAuth = FirebaseAuth.getInstance();

        // read data paper
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
            if (Paper.book().read("islogin") != null) {
                boolean flag = Paper.book().read("islogin");
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dangNhap(Paper.book().read("email"), Paper.book().read("pass"));
                        }
                    }, 1000);
                }
            }
        }
    }

    private void initControl() {

        txtresetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getApplicationContext(), ForgetResetPhone.class));

            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // kiem tra va add du lieu vao
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();
                if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(), "Bạn chưa nhập Password", Toast.LENGTH_SHORT).show();
                } else {

                    if (!str_email.contains("@gmail.com")) {
                        Toast.makeText(getApplicationContext(), "Email phải là @gmail.com", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (str_pass.length() < 4) {
                        Toast.makeText(getApplicationContext(), "Pass phải có ít nhất 4 kí tự", Toast.LENGTH_LONG).show();
                        return;
                    }
                    // svae dang nhap nguoi dung khi nguoi dung vao lai trang
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);


                    if (quanli == null) {
                        //user da co dang nhap firebase
                        dangNhap(str_email, str_pass);

                    } else {
                        // user da signout
                        firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                                .addOnCompleteListener(DangNhapActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        dangNhap(str_email, str_pass);
                                    }
                                });
                    }
                }
            }
        });
    }

    private void dangNhap(String email, String pass) {
        compositeDisposable.add(apiQuanLi.login(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        quanLiModel -> {
                            if (quanLiModel.isSuccess()) {
                                isLogin = true;
                                Utils.quanLi_current = quanLiModel.getResult().get(0);//phan tu dau tien cua list
                                Paper.book().write("login", quanLiModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                EventBus.getDefault().postSticky(new QuanLiEvent(Utils.quanLi_current));
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(this, "Password fail...", Toast.LENGTH_SHORT).show();
                            }

                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.quanLi_current.getEmail() != null && Utils.quanLi_current.getPass() != null) {
            email.setText(Utils.quanLi_current.getEmail());
            pass.setText(Utils.quanLi_current.getPass());
        }
    }


    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}