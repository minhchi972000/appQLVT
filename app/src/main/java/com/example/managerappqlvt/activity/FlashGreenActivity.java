package com.example.managerappqlvt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.managerappqlvt.R;

import io.paperdb.Paper;

public class FlashGreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_green);
        Paper.init(this);
        // cai dat thoi gian chuyen man hinh
        Thread thread= new Thread(){
            public  void run(){
                try {
                    sleep(3000);
                }catch(Exception e){

                }finally {
                    Intent intent= new Intent(getApplicationContext(),DangNhapActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}