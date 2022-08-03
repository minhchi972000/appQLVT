package com.example.managerappqlvt.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerappqlvt.Interface.ItemClickListener;
import com.example.managerappqlvt.R;
import com.example.managerappqlvt.model.DonHang;
import com.example.managerappqlvt.model.EventBus.DonHangEvent;
import com.example.managerappqlvt.model.EventBus.TinhTongEvent;
import com.example.managerappqlvt.model.Item;
import com.example.managerappqlvt.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    Context context;
    List<DonHang> listdonhang;

    public DonHangAdapter(Context context, List<DonHang> listdonhang) {
        this.context = context;
        this.listdonhang = listdonhang;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);

        holder.iddonhang.setText("Mã đơn hàng: " + donHang.getId());
        holder.tenuserdonhang.setText(donHang.getHoten());
        holder.txtdiachi.setText(donHang.getDiachigiao());
        holder.txtsdt.setText(donHang.getSodienthoai());
        holder.txtNgayGiao.setText("Ngày giao: " + donHang.getNgaygiao());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###" + "đ");
        holder.tongtiensp.setText(decimalFormat.format(donHang.getTongtien()));
        holder.tinhtrang.setText(Utils.trangThaiDonHang(donHang.getTrangthai()));
        //set Linearlayout
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.recyclerview_chitiet.getContext()
                , LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        //adapter ChiTietDonHang
        ChitietDonHangAdapter chitietDonHangAdapter = new ChitietDonHangAdapter(context, donHang.getItem());
        holder.recyclerview_chitiet.setLayoutManager(layoutManager);
        holder.recyclerview_chitiet.setAdapter(chitietDonHangAdapter);
        //Vì recyclerview nằm trong recyclerview
        holder.recyclerview_chitiet.setRecycledViewPool(viewPool);

        holder.setListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if (isLongClick) {
                    // Gửi toàn bộ đơn hàng sang xem đơn dùng EventBus
                    EventBus.getDefault().postSticky(new DonHangEvent(donHang));

                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener {
        TextView iddonhang, tenuserdonhang, tinhtrang, txtdiachi, txtsdt, txtNgayGiao,tongtiensp;
        RecyclerView recyclerview_chitiet;
        ItemClickListener listener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            iddonhang = itemView.findViewById(R.id.iddonhang);
            tenuserdonhang = itemView.findViewById(R.id.tenuserdonhang);
            txtdiachi = itemView.findViewById(R.id.txtdiachi);
            txtsdt = itemView.findViewById(R.id.txtsdt);
            txtNgayGiao = itemView.findViewById(R.id.txtNgayGiao);
            tinhtrang = itemView.findViewById(R.id.tinhtrang);
            tongtiensp = itemView.findViewById(R.id.tongtiensp);

            recyclerview_chitiet = itemView.findViewById(R.id.recyclerview_chitiet);

            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onClick(view, getAdapterPosition(), true);
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(0,0,getAdapterPosition(),"Cập nhật trạng thái giao hàng");
            contextMenu.add(0,1,getAdapterPosition(),"Xóa");
        }
    }

}
