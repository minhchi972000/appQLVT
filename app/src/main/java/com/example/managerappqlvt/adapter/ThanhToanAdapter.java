package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerappqlvt.Interface.IImageClickListenner;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.model.EventBus.TinhTongEvent;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class ThanhToanAdapter extends RecyclerView.Adapter<ThanhToanAdapter.MyViewHolder> {

    Context context;
    List<GioHang> gioHangList;


    public ThanhToanAdapter(Context context, List<GioHang> gioHangList) {
        this.context = context;
        this.gioHangList = gioHangList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTenhang());
        // so luong Interger loi hay gap phai
        holder.item_giohang_soluong.setText(gioHang.getSoluongmua() + "");
        Glide.with(context).load(gioHang.getHinhanh()).into(holder.item_giohang_image);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_giohang_gia.setText(decimalFormat.format((gioHang.getGia())));


    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_giohang_image;
        TextView item_giohang_tensp, item_giohang_gia, item_giohang_soluong;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);





        }


    }
}
