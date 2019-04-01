package id.co.lesfemmes.lesfemmes.sales;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import id.co.lesfemmes.lesfemmes.R;

public class Sales_detail_adapter extends RecyclerView.Adapter<Sales_detail_adapter.MyViewHolder> {

    private List<Sales_detail_model> saleddetaillist;
    Context ctx;
    private static ClickListener clickListener;


    @Override
    public int getItemCount() {
        return saleddetaillist.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView itemcode, harga, disc,total,qty,hargaafterdisc,note,NoSKU;
        public MyViewHolder(View view) {
            super(view);
            itemcode    =  view.findViewById(R.id.itemcode);
            harga       =  view.findViewById(R.id.harga);
            disc        =  view.findViewById(R.id.disc);
            total       = view.findViewById(R.id.total);
            qty         =   view.findViewById(R.id.qty);
            note        = view.findViewById(R.id.note);
            NoSKU       = view.findViewById(R.id.NoSKU);
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
    public Sales_detail_adapter(Context ctx, List<Sales_detail_model> saleddetaillist) {
        this.ctx = ctx;
        this.saleddetaillist = saleddetaillist;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sales_detail_rec, parent, false);
        final RecyclerView.ViewHolder holder = new MyViewHolder(itemView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Sales_detail_model as = saleddetaillist.get(position);
        holder.itemcode.setText(as.getItemCode());
        holder.harga.setText("Rp." + currencyFormat(as.getHarga()));
        holder.disc.setText(as.getDisc());
        holder.total.setText("Rp." + currencyFormat(as.getTotalHarga()));
        holder.qty.setText(as.getQuantity());
        holder.note.setText(as.getNote());
        holder.NoSKU.setText(as.getNoSKU());
    }
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        Sales_detail_adapter.clickListener = clickListener;
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }
}
