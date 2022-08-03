package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerappqlvt.Interface.ItemClickListener;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.activity.CartActivity;
import com.example.managerappqlvt.activity.ChiTietActivity;
import com.example.managerappqlvt.model.EventBus.DonHangEvent;
import com.example.managerappqlvt.model.EventBus.KhachHangEvent;
import com.example.managerappqlvt.model.KhachHang;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class KhachHangAdapter extends RecyclerView.Adapter<KhachHangAdapter.MyViewHolder> {
    Context context;
    List<KhachHang> listKhachHang;

    public KhachHangAdapter(Context context, List<KhachHang> listKhachHang) {
        this.context = context;
        this.listKhachHang = listKhachHang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khach_hang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        KhachHang khachHang = listKhachHang.get(position);
        holder.tenkhachhang.setText("Họ Tên : "+khachHang.getHoten());
        holder.sodienthoai.setText("Sdt: "+khachHang.getSdt());
        holder.diachi.setText("Địa chỉ: "+khachHang.getDiachi());
        holder.hinhanh.setImageResource(R.drawable.user);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    Toast.makeText(context, "Toast", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().postSticky(new KhachHangEvent(khachHang));
                    Intent intent = new Intent(context, CartActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Utils.manggiohang.clear();
                    context.startActivity(intent);
                }else {
                    // edit khach hang: sua xoa khach hang
                    EventBus.getDefault().postSticky(new KhachHangEvent(khachHang));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listKhachHang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        ImageView hinhanh;
        TextView tenkhachhang, sodienthoai, diachi;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hinhanh = itemView.findViewById(R.id.item_hinhanh);
            tenkhachhang = itemView.findViewById(R.id.item_tenkhachhang);
            sodienthoai = itemView.findViewById(R.id.item_sdt);
            diachi = itemView.findViewById(R.id.item_diachi);

            //item click
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getBindingAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

                contextMenu.add(0,0,getAdapterPosition(),"Cập nhật");
                contextMenu.add(0,1,getAdapterPosition(),"Xóa");

        }


    }
}
