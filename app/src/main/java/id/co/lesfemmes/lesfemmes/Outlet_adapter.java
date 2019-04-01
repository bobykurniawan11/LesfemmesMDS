package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Outlet_adapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Outlet_model> listData;
    private Context context;

    public Outlet_adapter(Context context, List<Outlet_model> listData) {
        this.context = context;
        layoutInflater =(android.view.LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        Outlet_adapter.ViewHolder spinnerHolder;
        if(convertView == null){
            spinnerHolder = new Outlet_adapter.ViewHolder();
            convertView = layoutInflater.inflate(R.layout.outlet_spinner, parent, false);
            spinnerHolder.outlet  = convertView.findViewById(R.id.outlet);
            convertView.setTag(spinnerHolder);
        }else{
            spinnerHolder = (Outlet_adapter.ViewHolder)convertView.getTag();
        }
        spinnerHolder.outlet.setText(listData.get(position).getOutletCode());
        return convertView;
    }
    class ViewHolder{
        TextView outlet;
    }
}
