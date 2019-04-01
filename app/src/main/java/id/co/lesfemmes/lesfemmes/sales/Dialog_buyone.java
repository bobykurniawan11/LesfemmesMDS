package id.co.lesfemmes.lesfemmes.sales;

import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;

public class Dialog_buyone extends DialogFragment{
    String urlactive;
    String NoNota,TanggalNota,Seller,OutletCode,selectedSku,itemPrice;
    AutoCompleteTextView itemcode;
    ImageView deleteIcon;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_buyone_layout, container, false);
        MainActivity mYourActiviy = (MainActivity) getActivity();
        urlactive = mYourActiviy.geturlactive();

        NoNota          = getArguments().getString("NoNota");
        TanggalNota     = getArguments().getString("Tanggal");
        Seller          = getArguments().getString("Seller");
        OutletCode      = getArguments().getString("OutletCode");
        selectedSku     = getArguments().getString("NoSku");
        itemPrice       = getArguments().getString("itemprice");
        itemcode        = v.findViewById(R.id.ItemCodeInput);
        deleteIcon      = v.findViewById(R.id.deleteIcon);

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        String[] from = { "name", "description" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter a = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, null, from, to, 0);
        a.setStringConversionColumn(1);
        FilterQueryProvider provider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                String[] columnNames = { BaseColumns._ID, "name", "description" };
                MatrixCursor c = new MatrixCursor(columnNames);
                try {
                    String urlString = urlactive+"welcome/getItem/"+constraint+"/"+OutletCode;
                    URL url = new URL(urlString);
                    InputStream stream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String jsonStr = reader.readLine();
                    JSONObject result = new JSONObject(jsonStr);
                    JSONArray jArray =  result.getJSONArray("dataItem");
                    for(int i = 0; i < jArray.length();i++) {
                        JSONObject detail = jArray.getJSONObject(i);
                        c.newRow().add(i).add(detail.getString("ItemCode")).add(detail.getString("ItemName"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        };
        a.setFilterQueryProvider(provider);
        itemcode.setAdapter(a);

        return v;
    }

}
