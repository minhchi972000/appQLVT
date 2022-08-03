package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.managerappqlvt.Interface.IImageClickListenner;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.activity.GioHangActivity;
import com.example.managerappqlvt.model.EventBus.GioHangEvent;
import com.example.managerappqlvt.model.EventBus.TinhTongEvent;
import com.example.managerappqlvt.model.GioHang;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder> {

    Context context;
    List<GioHang> gioHangList;


    public GioHangAdapter(Context context, List<GioHang> gioHangList) {
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

        if (gioHang.getHinhanh().contains("http")) {
            Glide.with(context).load(gioHang.getHinhanh()).into(holder.item_giohang_image);
        } else {
            String hinh = Utils.BASE_URL + "images/" + gioHang.getHinhanh();
            Glide.with(context).load(hinh).into(holder.item_giohang_image);

        }
        Glide.with(context).load(gioHang.getHinhanh()).into(holder.item_giohang_image);

        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.item_giohang_gia.setText(decimalFormat.format((gioHang.getGia())));
        long gia = gioHang.getSoluongmua() * gioHang.getGia();
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia) + "đ");

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Utils.mangmuahang.add(gioHang);
                    EventBus.getDefault().postSticky(new TinhTongEvent());
                } else {
                    for (int i = 0; i < Utils.mangmuahang.size(); i++) {
                        if (Utils.mangmuahang.get(i).getId() == gioHang.getId()) {
                            Utils.mangmuahang.remove(i);
                            EventBus.getDefault().postSticky(new TinhTongEvent());
                        }
                    }
                }
            }
        });


        holder.setListenner(new IImageClickListenner() {
            @Override
            public void onImageClick(View view, int pos, int giatri) {
                if (giatri == 1) {
                    if (gioHang.getSoluongmua() > 1) {
                        int soluongmoi = gioHang.getSoluongmua() - 1;
                        gioHangList.get(pos).setSoluongmua(soluongmoi);
                        holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluongmua() + " ");
                        long gia = gioHangList.get(pos).getSoluongmua() * gioHangList.get(pos).getGia();
                        holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());


                    } else if (gioHangList.get(pos).getSoluongmua() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                        builder.setTitle("Thông báo");
                        builder.setMessage("Bạn có muốn xóa sản phẩm khỏi giỏ hàng");
                        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utils.manggiohang.remove(pos);
                                Utils.mangmuahang.remove(pos);
                                notifyDataSetChanged();
                                // gui tinh tong tien
                                EventBus.getDefault().postSticky(new TinhTongEvent());
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    }

                } else if (giatri == 2) {
                    if (gioHangList.get(pos).getSoluongmua() < gioHang.getSoluongkho()) {
                        int soluongmoi = gioHangList.get(pos).getSoluongmua() + 1;
                        gioHangList.get(pos).setSoluongmua(soluongmoi);
                        holder.item_giohang_soluong.setText(gioHangList.get(pos).getSoluongmua() + " ");
                        long gia = gioHangList.get(pos).getSoluongmua() * gioHangList.get(pos).getGia();
                        holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
                        EventBus.getDefault().postSticky(new TinhTongEvent());

                    } else {
                        Toast.makeText(context, "Hết hàng", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView item_giohang_image, img_tru, img_cong;
        TextView item_giohang_tensp, item_giohang_gia, item_giohang_soluong, item_giohang_giasp2;
        IImageClickListenner listenner;
        CheckBox checkBox;
        Spinner spinnner;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
            img_tru = itemView.findViewById(R.id.item_giohang_tru);
            img_cong = itemView.findViewById(R.id.item_giohang_cong);
            checkBox = itemView.findViewById(R.id.item_giohang_check);
            spinnner = itemView.findViewById(R.id.spinnner);

            // event click
            img_cong.setOnClickListener(this);
            img_tru.setOnClickListener(this);


        }

        public void setListenner(IImageClickListenner listenner) {
            this.listenner = listenner;
        }

        @Override
        public void onClick(View view) {
            if (view == img_tru) {
                listenner.onImageClick(view, getAdapterPosition(), 1);
                // 1 la tru

            } else if (view == img_cong) {
                listenner.onImageClick(view, getAdapterPosition(), 2);
                // 2 la cong
            }

        }

    }
}
