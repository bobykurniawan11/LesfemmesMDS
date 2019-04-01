package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Seller extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Seller_model> listData;
    private Context context;
    public Seller(Context context, List<Seller_model> listData) {
        this.context = context;
        layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listData = listData;
    }
    @Override
    public int getCount() {
        return listData.size();
    }
    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder spinnerHolder;
        if(convertView == null){
            spinnerHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.seller_spinner, parent, false);
            spinnerHolder.nip   = convertView.findViewById(R.id.nip);
            spinnerHolder.nama  = convertView.findViewById(R.id.nama);

            convertView.setTag(spinnerHolder);
        }else{
            spinnerHolder = (ViewHolder)convertView.getTag();
        }
        spinnerHolder.nip.setText(listData.get(position).getNip());
        spinnerHolder.nama.setText(listData.get(position).getFullname());

        return convertView;
    }
    class ViewHolder{
        TextView nip,nama;
    }
}
