package id.co.lesfemmes.lesfemmes.notification;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.List;

import id.co.lesfemmes.lesfemmes.R;

public class Notification_adapter extends RecyclerView.Adapter<Notification_adapter.MyViewHolder> implements Filterable {
    private List<Notification_model> notiflist;
    List<Notification_model> notiflistCopy;

    private static ClickListener clickListener;
    Context ctx;
    RecyclerView.ViewHolder holder;

    private List<Notification_model> mArrayList;
    private List<Notification_model> mFilteredList;

    public Notification_adapter(List<Notification_model> notiflist,Context ctx) {
        this.notiflist = notiflist;
        this.ctx = ctx;

        mArrayList = new ArrayList<>();
        mFilteredList = new ArrayList<>();

        if (notiflist != null) {
            mArrayList.addAll(notiflist);
            mFilteredList.addAll(notiflist);
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
                    List<Notification_model> filteredList = new ArrayList<>();
                    for (Notification_model lmodel : mArrayList) {
                        if (lmodel.getCustomerCode().toLowerCase().contains(charString)) {
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
                mFilteredList = (List<Notification_model>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }


    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView  outlet, judul;
        public CardView mainLayout;
        public ImageView status;

        public MyViewHolder(View view) {
            super(view);
            outlet    = view.findViewById(R.id.outlet);
            judul     = view.findViewById(R.id.judul_notifikasi);
            mainLayout = view.findViewById(R.id.mainLayout);
            status = view.findViewById(R.id.status);
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_list, parent, false);
        holder = new MyViewHolder(itemView);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification_model as = mFilteredList.get(position);
        holder.outlet.setText(as.getCustomerCode());

        if(as.getNotificationType() == 1) {
            holder.outlet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        }else{
            holder.outlet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle, 0, 0, 0);
        }
        Drawable myDrawable = ctx.getResources().getDrawable(R.drawable.circle_danger);

        if(as.getNotificationStatus() == 0) {
            holder.status.setImageDrawable(myDrawable);
        }

        holder.judul.setText(as.getNotificationTitle());
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        Notification_adapter.clickListener = clickListener;
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
