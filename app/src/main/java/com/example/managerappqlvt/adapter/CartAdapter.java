package com.example.managerappqlvt.adapter;

import android.content.Context;
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
import com.example.managerappqlvt.model.EventBus.CartEvent;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.model.VatTu;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {
    Context context;
    List<VatTu> array;

    public CartAdapter(Context context, List<VatTu> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VatTu vatTu = array.get(position);

        holder.txtten.setText("" + vatTu.getTensp());
        holder.txttenct.setText("Sản xuất: " + vatTu.getTencongty());
        holder.txtsoluong.setText("Số lượng: " + vatTu.getSoluong());
        holder.txtngaynhap.setText("" + vatTu.getNgaynhap());
        holder.txtmahang.setText("Mã hàng: " + vatTu.getMahang());
        holder.imagcart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCart(vatTu);
                EventBus.getDefault().postSticky(new CartEvent(vatTu));
            }
        });

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtgia.setText("Giá:" + decimalFormat.format(Double.parseDouble(vatTu.getGiaban())) + "đ");

        if (vatTu.getHinhanh().contains("http")) {
            Glide.with(context).load(vatTu.getHinhanh()).into(holder.imghinhanh);
        } else {
            String hinh = Utils.BASE_URL + "images/" + vatTu.getHinhanh();
            Glide.with(context).load(hinh).into(holder.imghinhanh);

        }

    }

    private void addCart(VatTu vatTu) {
        if (Utils.manggiohang.size() > 0) {
            boolean flag = false;
            int soluong = 1;
            for (int i = 0; i < Utils.manggiohang.size(); i++) {
                if (Utils.manggiohang.get(i).getId() == vatTu.getId()) {

                    if (Utils.manggiohang.get(i).getSoluongmua() < vatTu.getSoluong()) {
                        Utils.manggiohang.get(i).setSoluongmua(soluong + Utils.manggiohang.get(i).getSoluongmua());

                    } else {
                        Toast.makeText(context, "Hết hàng", Toast.LENGTH_LONG).show();
                    }
                    flag = true;
                }
            }
            if (flag == false) {

                GioHang gioHang = new GioHang();
                long gia = Long.parseLong(vatTu.getGiaban());
                gioHang.setGia(gia);
                gioHang.setSoluongmua(soluong);
                gioHang.setId(vatTu.getId());
                gioHang.setTenhang(vatTu.getTensp());
                gioHang.setSoluongkho(vatTu.getSoluong());

                gioHang.setHinhanh(vatTu.getHinhanh());
                gioHang.setMahang(vatTu.getMahang());
                gioHang.setNgaynhap(vatTu.getNgaynhap());
                Utils.manggiohang.add(gioHang);
            }
        } else {
            int soluong = 1;
            long gia = Long.parseLong(vatTu.getGiaban());
            GioHang gioHang = new GioHang();
            gioHang.setGia(gia);
            gioHang.setSoluongmua(soluong);
            gioHang.setId(vatTu.getId());
            gioHang.setTenhang(vatTu.getTensp());
            gioHang.setSoluongkho(vatTu.getSoluong());

            gioHang.setMahang(vatTu.getMahang());
            gioHang.setNgaynhap(vatTu.getNgaynhap());
            gioHang.setHinhanh(vatTu.getHinhanh());
            Utils.manggiohang.add(gioHang);
        }


    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtgia, txtten, txttenct, txtsoluong, txtngaynhap, txtmahang;
        ImageView imghinhanh, imagcart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imghinhanh = itemView.findViewById(R.id.itemsp_imge);
            txtten = itemView.findViewById(R.id.itemsp_ten);
            txttenct = itemView.findViewById(R.id.itemsp_tenct);
            txtgia = itemView.findViewById(R.id.itemsp_gia);
            txtsoluong = itemView.findViewById(R.id.itemsp_soluong);
            txtngaynhap = itemView.findViewById(R.id.itemsp_ngaynhap);
            txtmahang = itemView.findViewById(R.id.itemsp_mahang);
            imagcart = itemView.findViewById(R.id.item_cart);
        }
    }
}
