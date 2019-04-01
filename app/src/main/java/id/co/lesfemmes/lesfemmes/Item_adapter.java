package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import id.co.lesfemmes.lesfemmes.custom.MatchableArrayAdapter;

public class Item_adapter extends MatchableArrayAdapter<Item_model> {

    public Item_adapter(Context context, int resource, ArrayList<Item_model> m_member) {
        super(context, resource, m_member);
    }

    @Override
    protected void onBind(Item_model item, final View itemView, final int position) {
        TextView text1 = itemView.findViewById(R.id.ItemCode);
        text1.setText(item.getItemCode());
    }

    @Override
    protected boolean matches(Item_model value, CharSequence prefix, CharSequence lowerCasePrefix) {
        return value.getItemCode().toLowerCase().contains(lowerCasePrefix);
    }

}
