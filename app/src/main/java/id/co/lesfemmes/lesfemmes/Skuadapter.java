package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Skuadapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<Skumodel> listData;
    private Context context;
    public Skuadapter(Context context, List<Skumodel> listData) {
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
        return (Skumodel)listData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Skuadapter.ViewHolder spinnerHolder;
        if(convertView == null){
            spinnerHolder = new Skuadapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.sku_spinner, parent, false);
            spinnerHolder.nosku   = convertView.findViewById(R.id.nosku_tv);
            convertView.setTag(spinnerHolder);
        }else{
            spinnerHolder = (Skuadapter.ViewHolder)convertView.getTag();
        }
        spinnerHolder.nosku.setText(String.valueOf(listData.get(position).getNoSku()));

        return convertView;
    }
    class ViewHolder{
        TextView nosku;
    }
}
