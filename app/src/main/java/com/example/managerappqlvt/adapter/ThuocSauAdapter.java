package com.example.managerappqlvt.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ThuocSauAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<VatTu> array;

    //khai bao loading
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public ThuocSauAdapter(Context context, List<VatTu> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thuocsau, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            VatTu vatTu = array.get(position);
            myViewHolder.tensp.setText(vatTu.getTensp());
            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            myViewHolder.giasp.setText("Giá:" + decimalFormat.format(Double.parseDouble(vatTu.getGiaban())) + "đ");
            myViewHolder.tencongty.setText(vatTu.getTencongty());
            myViewHolder.soluong.setText("Số lượng: " + vatTu.getSoluong());
            myViewHolder.ngaynhap.setText("" + vatTu.getNgaynhap());
            myViewHolder.mahang.setText("Mã hàng: " + vatTu.getMahang());



            if (vatTu.getHinhanh().contains("http")) {
                Glide.with(context).load(vatTu.getHinhanh()).into(myViewHolder.hinhanh);
            } else {
                String hinh = Utils.BASE_URL + "images/" + vatTu.getHinhanh();
                Glide.with(context).load(hinh).into(myViewHolder.hinhanh);

            }

            // set cho ItemClicklistener
            myViewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if (!isLongClick) {
                        // click
                        Intent intent = new Intent(context, ChiTietActivity.class);
                        //error implements Serializable
                        intent.putExtra("chitiet", vatTu);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else {
                        Log.d("test", array.get(position).getId() + "");
                        EventBus.getDefault().postSticky(new DeleteEvent(vatTu));
                    }
                }
            });
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return array.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {
        TextView tensp, giasp, tencongty, soluong, ngaynhap, mahang;
        ImageView hinhanh;
        // khoi tao interface xong =>khai bao
        private ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tensp = itemView.findViewById(R.id.itemsp_ten);
            giasp = itemView.findViewById(R.id.itemsp_gia);
            tencongty = itemView.findViewById(R.id.itemsp_tenct);
            hinhanh = itemView.findViewById(R.id.itemsp_imge);
            soluong = itemView.findViewById(R.id.itemsp_soluong);
            ngaynhap = itemView.findViewById(R.id.itemsp_ngaynhap);
            mahang = itemView.findViewById(R.id.itemsp_mahang);

            // tao su kien cho interface
            itemView.setOnClickListener(this); // onClick
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
