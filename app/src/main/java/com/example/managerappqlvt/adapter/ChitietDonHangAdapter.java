package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.model.Item;
import com.example.managerappqlvt.utils.Utils;


import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ChitietDonHangAdapter extends RecyclerView.Adapter<ChitietDonHangAdapter.MyViewHolder> {
    Context context;
    List<Item> itemList;

    public ChitietDonHangAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang_chitiet, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Item item = itemList.get(position);
        holder.txtten.setText(item.getTensp() + "");
        holder.txtsoluong.setText("Số lượng:" + item.getSoluongmua() + "");
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgiaspchitiet.setText("Giá:" + decimalFormat.format(item.getGiaban()) + "đ");


        if (item.getHinhanh().contains("http")) {
            Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);
        } else {
            String hinh = Utils.BASE_URL + "images/" + item.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imagechitiet);

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagechitiet;
        TextView txtten, txtsoluong, txtgiaspchitiet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagechitiet = itemView.findViewById(R.id.item_imgchitiet);
            txtten = itemView.findViewById(R.id.item_tenspchitiet);
            txtsoluong = itemView.findViewById(R.id.item_soluongchitiet);
            txtgiaspchitiet = itemView.findViewById(R.id.item_giaspchitiet);


        }
    }
}
