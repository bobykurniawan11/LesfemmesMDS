package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.co.lesfemmes.lesfemmes.sales.EditPrice_model;

public class EditPrice_adapter extends RecyclerView.Adapter<EditPrice_adapter.MyViewHolder> {
    private List<EditPrice_model> allowedList;

    private static EditPrice_adapter.ClickListener clickListener;
    Context ctx;
    RecyclerView.ViewHolder holder;

    public EditPrice_adapter(List<EditPrice_model> allowedList, Context ctx) {
        this.allowedList = allowedList;
        this.ctx = ctx;
    }

    @Override
    public int getItemCount() {
        return allowedList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView outlet, tanggal;
        public ImageView status;

        public MyViewHolder(View view) {
            super(view);
            outlet = view.findViewById(R.id.CustomerCode);
            tanggal = view.findViewById(R.id.tanggal);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    @Override
    public EditPrice_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.editprice_list, parent, false);
        holder = new EditPrice_adapter.MyViewHolder(itemView);
        return new EditPrice_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EditPrice_adapter.MyViewHolder holder, int position) {
        EditPrice_model as = allowedList.get(position);
        holder.outlet.setText(as.getCustomerCode());
        holder.tanggal.setText(as.getTransactionDate());
    }

    public void setOnItemClickListener(EditPrice_adapter.ClickListener clickListener) {
        EditPrice_adapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
