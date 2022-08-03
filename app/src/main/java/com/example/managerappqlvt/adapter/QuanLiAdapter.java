package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerappqlvt.Interface.ItemClickListener;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.model.EventBus.QuanLiEvent;
import com.example.managerappqlvt.model.QuanLi;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class QuanLiAdapter extends RecyclerView.Adapter<QuanLiAdapter.MyViewHolder> {
    Context context;
    List<QuanLi> listQuanLi;

    public QuanLiAdapter(Context context, List<QuanLi> listQuanLi) {
        this.context = context;
        this.listQuanLi = listQuanLi;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quan_li, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        QuanLi quanLi = listQuanLi.get(position);
        holder.tenkhachhang.setText("Họ Tên : "+quanLi.getHoten());
        holder.sodienthoai.setText("Sdt: "+quanLi.getSdt());
        holder.item_email.setText("Địa chỉ: "+quanLi.getEmail());
        holder.hinhanh.setImageResource(R.drawable.user);


        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    EventBus.getDefault().postSticky(new QuanLiEvent(quanLi));
//                    Intent intent= new Intent(context, ChiTietThongTinVatTu.class);
//                    context.startActivity(intent);
                }else {
                    // edit khach hang: sua xoa khach hang
                    EventBus.getDefault().postSticky(new QuanLiEvent(quanLi));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listQuanLi.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        ImageView hinhanh;
        TextView tenkhachhang, sodienthoai, diachi,item_email;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            hinhanh = itemView.findViewById(R.id.item_hinhanh);
            tenkhachhang = itemView.findViewById(R.id.item_tenkhachhang);
            sodienthoai = itemView.findViewById(R.id.item_sdt);
            item_email = itemView.findViewById(R.id.item_email);

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
