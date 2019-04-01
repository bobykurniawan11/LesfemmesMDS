package id.co.lesfemmes.lesfemmes.sales;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import id.co.lesfemmes.lesfemmes.R;


public class Sales_header_adapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Sales_header_model> salesheaderlist;

    private static ClickListener clickListener;

    public Sales_header_adapter(List<Sales_header_model> salesheaderlist) {
        this.salesheaderlist = salesheaderlist;
    }
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(amount));
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View VHItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_header_rec, parent, false);
        return new VHItem(VHItem);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Sales_header_model as = salesheaderlist.get(position);
            VHItem VHitem = (VHItem) holder;
            VHitem.nonota.setText( " --- ");
            VHitem.tanggal.setText(as.getTanggalTransaksi());
            VHitem.total.setText("Rp." + currencyFormat(as.getTotalharga()));

            if(as.getStatusPosting() == 0){
                VHitem.iconStatus.setImageResource(R.drawable.circle_danger);
            }
            if(as.getStatusPosting() == 1){
                VHitem.iconStatus.setImageResource(R.drawable.circle_warning);
            }
            if(as.getStatusPosting() == 2){
                VHitem.iconStatus.setImageResource(R.drawable.circle_success);
            }
            if(as.getStatusPosting() == 3){
                VHitem.iconStatus.setImageResource(R.drawable.circle_cancel);
            }
    }

    @Override
    public int getItemCount() {
        return salesheaderlist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position  == 0;
    }

    class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
         TextView  tanggal, total,nonota;
         ImageView iconStatus;
         VHItem(View itemView) {
            super(itemView);
            tanggal         =  itemView.findViewById(R.id.tanggalnota);
            total           =  itemView.findViewById(R.id.total);
            nonota          =  itemView.findViewById(R.id.nonota);
            iconStatus      = itemView.findViewById(R.id.statusicon);
             itemView.setOnClickListener(this);
             itemView.setOnLongClickListener(this);
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
    public void setOnItemClickListener(ClickListener clickListener) {
        Sales_header_adapter.clickListener = clickListener;
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