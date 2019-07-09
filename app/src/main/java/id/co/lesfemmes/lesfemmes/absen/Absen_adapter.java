package id.co.lesfemmes.lesfemmes.absen;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import id.co.lesfemmes.lesfemmes.R;


public class Absen_adapter extends RecyclerView.Adapter<Absen_adapter.MyViewHolder> implements Filterable {
    private static ClickListener clickListener;
    List<Absen_model> absenlistCopy;
    Context ctx;
    RecyclerView.ViewHolder holder;
    private List<Absen_model> absenlist;
    private List<Absen_model> mArrayList;
    private List<Absen_model> mFilteredList;

    public Absen_adapter(List<Absen_model> absenlist, Context ctx) {
        this.absenlist = absenlist;
        this.ctx = ctx;

        mArrayList = new ArrayList<>();
        mFilteredList = new ArrayList<>();

        if (absenlist != null) {
            mArrayList.addAll(absenlist);
            mFilteredList.addAll(absenlist);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString().toLowerCase();
                if (charString.isEmpty()) {
                    mFilteredList = mArrayList;
                } else {
                    List<Absen_model> filteredList = new ArrayList<>();
                    for (Absen_model lmodel : mArrayList) {
                        if (lmodel.getTanggal().toLowerCase().contains(charString)) {
                            filteredList.add(lmodel);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<Absen_model>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.absen_detail_list, parent, false);
        holder = new MyViewHolder(itemView);
        return new Absen_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Absen_adapter.MyViewHolder holder, int position) {
        Absen_model as = mFilteredList.get(position);

        holder.Nama.setText(as.getNama());

        DateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd-mm-yyyy");
        String inputDateStr = as.getTanggal();
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDateStr = outputFormat.format(date);

        holder.Tanggal.setText(outputDateStr);
        holder.Jam.setText(as.getJamMasuk() + " - " + as.getJamKeluar());
        holder.no_urut.setText(String.valueOf(position + 1));
    }

    public void setOnItemClickListener(Absen_adapter.ClickListener clickListener) {
        Absen_adapter.clickListener = clickListener;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView Nama, Tanggal, Jam, no_urut;
        public CardView mainLayout;
        public ImageView status;

        public MyViewHolder(View view) {
            super(view);
            Nama = view.findViewById(R.id.Nama);
            Tanggal = view.findViewById(R.id.Tanggal);
            Jam = view.findViewById(R.id.Jam);
            no_urut = view.findViewById(R.id.no_urut);
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
}
