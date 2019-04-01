package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


    public class Promotion_adapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Promotion_model> listData;
        private Context context;

        public Promotion_adapter(Context context, List<Promotion_model> listData) {
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
            return (Promotion_model)listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Promotion_adapter.ViewHolder spinnerHolder;
            if(convertView == null){
                spinnerHolder = new Promotion_adapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.promotion_spinner, parent, false);
                spinnerHolder.promocode  = convertView.findViewById(R.id.PromotionCode);
                convertView.setTag(spinnerHolder);
            }else{
                spinnerHolder = (Promotion_adapter.ViewHolder)convertView.getTag();
            }
            spinnerHolder.promocode.setText(listData.get(position).getPromotionCode() + " - " + listData.get(position).getPromotionDescription());
            return convertView;
        }
        class ViewHolder{
            TextView promocode;
        }
    }

