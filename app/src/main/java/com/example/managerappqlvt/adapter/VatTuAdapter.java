package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerappqlvt.Interface.ItemClickListener;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.activity.ChiTietActivity;
import com.example.managerappqlvt.model.EventBus.DeleteEvent;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class VatTuAdapter extends RecyclerView.Adapter<VatTuAdapter.MyViewHolder> {

    Context context;
    List<VatTu> array;

    public VatTuAdapter(Context context, List<VatTu> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sp_moi, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VatTu vattu = array.get(position); // lay vitri trong mang list SanPhamMoi
        holder.txtten.setText("" + vattu.getTensp());
        holder.txttenct.setText("Sản xuất: " + vattu.getTencongty());
        holder.txtsoluong.setText("Số lượng: " + vattu.getSoluong());
        holder.txtngaynhap.setText("" + vattu.getNgaynhap());
        holder.txtmahang.setText("Mã hàng: " + vattu.getMahang());

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgia.setText("Giá:" + decimalFormat.format(Double.parseDouble(vattu.getGiaban())) + "đ");

        Glide.with(context).load(vattu.getHinhanh()).into(holder.imghinhanh);

        if (vattu.getHinhanh().contains("http")) {
            Glide.with(context).load(vattu.getHinhanh()).into(holder.imghinhanh);
        } else {
            String hinh = Utils.BASE_URL + "images/" + vattu.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imghinhanh);

        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!isLongClick) {
                    //click
                    Intent intent = new Intent(context, ChiTietActivity.class);
                    intent.putExtra("chitiet", vattu);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    EventBus.getDefault().postSticky(new DeleteEvent(vattu));

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener, View.OnClickListener {

        TextView txtgia, txtten, txttenct, txtsoluong, txtngaynhap, txtmahang;
        ImageView imghinhanh;
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imghinhanh = itemView.findViewById(R.id.itemsp_imge);
            txtten = itemView.findViewById(R.id.itemsp_ten);
            txttenct = itemView.findViewById(R.id.itemsp_tenct);
            txtgia = itemView.findViewById(R.id.itemsp_gia);
            txtsoluong = itemView.findViewById(R.id.itemsp_soluong);
            txtngaynhap = itemView.findViewById(R.id.itemsp_ngaynhap);
            txtmahang = itemView.findViewById(R.id.itemsp_mahang);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0, 0, getAdapterPosition(), "Sửa");
            contextMenu.add(0, 1, getAdapterPosition(), "Xóa");
        }


        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return false;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }
    }
}
