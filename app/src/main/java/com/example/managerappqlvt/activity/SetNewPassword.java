package com.example.managerappqlvt.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.managerappqlvt.R;
import com.example.managerappqlvt.retrofit.ApiQuanLi;
import com.example.managerappqlvt.retrofit.Retrofitclient;
import com.example.managerappqlvt.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SetNewPassword extends AppCompatActivity {
    EditText confirmPassword, newPassword;
    Button btnUpdate;
    String phone;
    String str_pass;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiQuanLi apiQuanLi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);

        apiQuanLi = Retrofitclient.getInstance(Utils.BASE_URL).create(ApiQuanLi.class);

        confirmPassword = findViewById(R.id.confirmPassword);
        newPassword = findViewById(R.id.newPassword);
        btnUpdate = findViewById(R.id.btnUpdate);


        phone = getIntent().getStringExtra("phone");

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdatePassword();
            }
        });

    }

    private void UpdatePassword() {

        String newPass = newPassword.getText().toString().trim();
        String conPass = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPass) || TextUtils.isEmpty(conPass)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin...", Toast.LENGTH_SHORT).show();
        } else if (!newPass.equals(conPass)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
        } else {

            Log.d("phone", phone);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Quanli");
            ref.child(phone).child("pass").setValue(newPass);

            Query query = ref.orderByChild("sdt").equalTo(phone);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        str_pass = "" + ds.child("pass").getValue();
                        getData();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            Intent intent = new Intent(getApplicationContext(), ForgetPasswordSuccessMessage.class);
            startActivity(intent);
        }
    }

    private void getData() {
        Log.d("idquanli", str_pass + "");
        compositeDisposable.add(apiQuanLi.getQuanLi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        quanLiModel -> {
                            if (quanLiModel.isSuccess()) {
                                int totalId;
                                for (int i = 0; i < quanLiModel.getResult().size(); i++) {
                                    if (quanLiModel.getResult().get(i).getSdt().equals(phone)) {
                                        totalId = quanLiModel.getResult().get(i).getId();
                                        Log.d("idquanli", totalId + "");
                                        updatePassword(totalId, str_pass);
                                    }

                                }
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    private void updatePassword(int totalId, String str_pass) {
        compositeDisposable.add(apiQuanLi.updatequanlipass(totalId, str_pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        quanLiModel -> {
                            if (quanLiModel.isSuccess()) {
                                Toast.makeText(this, "Update password successfuly...", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}