package id.co.lesfemmes.lesfemmes.sales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.Item_model;
import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.Promotion_adapter;
import id.co.lesfemmes.lesfemmes.Promotion_model;
import id.co.lesfemmes.lesfemmes.R;
import id.co.lesfemmes.lesfemmes.Session_model;

public class Sales_form_detail extends Fragment {
    AutoCompleteTextView itemcode,skuInputField;
    private ArrayList<Item_model> itemList;
    RequestQueue queue;
    String TanggalNota, OutletCode;
    String usingSpecial,usingLifetime,lifetimepromotipe,specialpromotipe,subtotal;
    Integer specialDisc, lifetimeDisc;
    TextView hargaItemTV,subtotalTV,tagihanTV;
    EditText quantityTV;
    Button populateHarga;
    String HargaItem,Seller,NoSku,StatusPosting;
    Integer selectedPromotion,QuantityVal;
    protected List<Promotion_model> spinnerData;
    Spinner promotionSpinner;
    ProgressDialog progressDialog;
    Button simpanDetail;
    String TipePromosi;
    String saveStatus;
    int i;
    double newsubtotal,tagihan,discAmount;
    String urlactive;
    Session_model sess_model;
    ImageView iconedit;
    TextView tvPromo;

    Integer isEditTotal;
    TextView catatanTV;
    EditText catatan;
    String catatanvalue;
    CheckBox gwpCheckbox;
    boolean isGwp;

    String jammenit;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sales_detail_form, container, false);
        isEditTotal = 0;
        return v;
    }
    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        newsubtotal = 0;
        TanggalNota     = getArguments().getString("TanggalNota");
        OutletCode      = getArguments().getString("OutletCode");
        Seller          = getArguments().getString("Seller");

        StatusPosting   = getArguments().getString("StatusPosting");
        selectedPromotion = 0;
        QuantityVal = 0;
        gwpCheckbox = v.findViewById(R.id.isGWP);
        gwpCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                progressDialog.setMessage("Mencari Info Item");
                progressDialog.setCancelable(false);
                progressDialog.show();
                    isGwp = isChecked;
                    quantityTV.setText("0");
                    populateItemPrice();
            }
        });
        populateHarga = v.findViewById(R.id.populateHarga);
        iconedit = v.findViewById(R.id.iconedit);


        subtotalTV = v.findViewById(R.id.subtotal);
        promotionSpinner = v.findViewById(R.id.promotion);
        promotionSpinner.setVisibility(View.GONE);
        itemList    =  new ArrayList<>();
        hargaItemTV   = v.findViewById(R.id.ItemPrice);
        tagihanTV = v.findViewById(R.id.tagihan);
        quantityTV = v.findViewById(R.id.quantity);
        simpanDetail = v.findViewById(R.id.simpanDetail);
        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Form Input Sales Detail");
        sess_model = new Session_model(getContext());
        tvPromo = v.findViewById(R.id.tvPromo);

        tvPromo.setVisibility(View.GONE);
        subtotalTV.setVisibility(View.GONE);

        catatanTV   = v.findViewById(R.id.catatanTV);
        catatan     = v.findViewById(R.id.catatan);

        catatanTV.setVisibility(View.GONE);
        catatan.setVisibility(View.GONE);

        tagihanTV.setVisibility(View.GONE);
        quantityTV.setVisibility(View.GONE);
        simpanDetail.setVisibility(View.GONE);
        hargaItemTV.setVisibility(View.GONE);
        jammenit = "00:00";
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();

        queue = Volley.newRequestQueue(getActivity());
        itemcode = v.findViewById(R.id.ItemCodeInput);
        itemcode.setThreshold(3);
        itemcode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        skuInputField = v.findViewById(R.id.skuInputField);
        skuInputField.setThreshold(1);
        skuInputField.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        progressDialog = new ProgressDialog(getActivity());
        iconedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Ubah Total Penjualan");
                final EditText input = new EditText(getContext());
                input.setInputType( InputType.TYPE_CLASS_NUMBER );
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(input.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Harap isi field yang tersedia !!!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            tagihan = Double.parseDouble(input.getText().toString());
                            tagihanTV.setText(currencyFormat(input.getText().toString()));
                            isEditTotal = 1;
                            catatanTV.setVisibility(View.VISIBLE);
                            catatan.setVisibility(View.VISIBLE);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isEditTotal = 0;
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        String[] from1 = { "name"};
        int[] to1 = { android.R.id.text1 };
        SimpleCursorAdapter a1 = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, null, from1, to1, 0);
        a1.setStringConversionColumn(1);
        FilterQueryProvider provider1 = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                String[] columnNames = { BaseColumns._ID, "name" };
                MatrixCursor c = new MatrixCursor(columnNames);
                try {
                    String urlString = urlactive+"Welcomebaru/getsku/"+constraint;
                    URL url = new URL(urlString);
                    InputStream stream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String jsonStr = reader.readLine();

                    JSONObject result = new JSONObject(jsonStr);
                    JSONArray jArray =  result.getJSONArray("dataItem");
                    for(int i = 0; i < jArray.length();i++) {
                        JSONObject detail = jArray.getJSONObject(i);
                        c.newRow().add(i).add(detail.getString("SKU"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        };
        a1.setFilterQueryProvider(provider1);
        skuInputField.setAdapter(a1);

        String[] from = { "name", "description" };
        int[] to = { android.R.id.text1, android.R.id.text2 };
        SimpleCursorAdapter a = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, null, from, to, 0);
        a.setStringConversionColumn(1);
        FilterQueryProvider provider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null) {
                    subtotalTV.setText("");
                    return null;
                }
                String[] columnNames = { BaseColumns._ID, "name", "description" };
                MatrixCursor c = new MatrixCursor(columnNames);
                try {
                    String urlString = urlactive+"Welcomebaru/getItem/"+constraint+"/"+OutletCode;
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
        populateHarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemcode.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Harap Isi Kode Item !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setMessage("Mencari Info Item");
                progressDialog.setCancelable(false);
                progressDialog.show();
                hideSoftKeyboard(getActivity(), v);
                populateItemPrice();
            }
        });
        promotionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Promotion_model pmodel = (Promotion_model) arg0.getSelectedItem();
                double subtotal = (Integer.parseInt(HargaItem) / 100.0f) * Float.parseFloat(pmodel.getDiscountPromotion());
                TipePromosi = pmodel.getPromotionCode();
                newsubtotal =  Integer.parseInt(HargaItem) - subtotal;
                subtotalTV.setText("Setelah Diskon Rp. " + currencyFormat(String.valueOf(newsubtotal)));
                discAmount = Double.valueOf(pmodel.getDiscountPromotion());
                hitungTotal();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        itemcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvPromo.setVisibility(View.GONE);
                promotionSpinner.setVisibility(View.GONE);
                subtotalTV.setText("");
                hargaItemTV.setText("");
                quantityTV.setText("");
                catatan.setText("");
                newsubtotal = 0;
                simpanDetail.setVisibility(View.GONE);
                tagihanTV.setText("");
                tagihanTV.setVisibility(View.GONE);
                discAmount = 0;
                tagihanTV.setVisibility(View.GONE);
                quantityTV.setVisibility(View.GONE);
                simpanDetail.setVisibility(View.GONE);
                catatanTV.setVisibility(View.GONE);
                catatan.setVisibility(View.GONE);
                hargaItemTV.setVisibility(View.GONE);
                isEditTotal = 0;
                catatanTV.setVisibility(View.GONE);
                catatan.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        quantityTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEditTotal = 0;
                hitungTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        simpanDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoSku = skuInputField.getText().toString();

                if(NoSku.equals("")){
                    Toast.makeText(getActivity(), "Harap Masukan SKU", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveDetail();

            }
        });
        checkAllowed();
    }
    public void hitungTotal(){
        try{
            i = Integer.parseInt(quantityTV.getText().toString());
        }catch(NumberFormatException ex){
            i = 0;
        }
        QuantityVal = i;
        if(newsubtotal != 0) {
            tagihan = QuantityVal * newsubtotal;
            tagihanTV.setText(currencyFormat(String.valueOf(tagihan)));
        }
    }
    public static void hideSoftKeyboard (Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    private void populateItemPrice(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"Welcomebaru/populateItemPrice/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        hargaItemTV.setVisibility(View.VISIBLE);
                        tagihanTV.setVisibility(View.VISIBLE);
                        quantityTV.setVisibility(View.VISIBLE);
                        simpanDetail.setVisibility(View.VISIBLE);
                        subtotalTV.setVisibility(View.VISIBLE);
                        catatanTV.setVisibility(View.VISIBLE);
                        catatan.setVisibility(View.VISIBLE);
                        tagihanTV.setText("");
                        subtotalTV.setText("");
                        hargaItemTV.setText("");
                        HargaItem               = "";
                        usingSpecial            = "No";
                        specialpromotipe        = "No";
                        usingLifetime           = "";
                        lifetimepromotipe       = "";
                        specialDisc             = 0;
                        lifetimeDisc            = 0;
                        QuantityVal             = 0;
                        newsubtotal             = 0;
                        HargaItem               = "0";
                        TipePromosi             = "";
                        discAmount              = 0;

                        subtotalTV.setOnClickListener(null);
                        try {
                            JSONObject jsonObj      = new JSONObject(response);
                            HargaItem               = jsonObj.getString("Price");
                            usingSpecial            = jsonObj.getString("use_specialItem_Konsinyasi");
                            specialpromotipe        = jsonObj.getString("specialpromotipe");
                            usingLifetime           = jsonObj.getString("use_lifetimediscount");
                            lifetimepromotipe       = jsonObj.getString("lifetimepromotipe");
                            specialDisc             = jsonObj.getInt("specialDisc");
                            lifetimeDisc            = jsonObj.getInt("lifetimeDisc");
                            hargaItemTV.setText("Harga : Rp. " +currencyFormat(HargaItem));
                            if(!usingSpecial.equalsIgnoreCase("No")){
                                TipePromosi = "SpecialPrice";
                                tvPromo.setVisibility(View.GONE);
                                if(specialpromotipe.equalsIgnoreCase("nominal")){
                                    double sub = specialDisc;
                                    newsubtotal = sub;
                                }else if(specialpromotipe.equalsIgnoreCase("persen")){
                                    double subtotal = (Integer.parseInt(HargaItem) / 100.0f) * specialDisc;
                                    newsubtotal =  Integer.parseInt(HargaItem) - subtotal;
                                }
                                subtotalTV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText( getActivity() , "Info : Spesial Item , Total Discount : " + specialDisc  +" " +  specialpromotipe , Toast.LENGTH_LONG).show();
                                    }
                                });
                                discAmount = specialDisc;
                                progressDialog.dismiss();
                            }

                            if(!usingLifetime.equalsIgnoreCase("No")){
                                tvPromo.setVisibility(View.GONE);
                                TipePromosi = "Lifetime";
                                if(lifetimepromotipe.equalsIgnoreCase("nominal")){
                                    Toast.makeText( getActivity() , "NOMINAL", Toast.LENGTH_SHORT).show();
                                    double sub = Double.parseDouble(HargaItem) - lifetimeDisc;
                                    newsubtotal = sub;
                                }else if(lifetimepromotipe.equalsIgnoreCase("persen")){
                                    double subtotal = (Integer.parseInt(HargaItem) / 100.0f) * lifetimeDisc;
                                    newsubtotal =  Integer.parseInt(HargaItem) - subtotal;
                                }
                                subtotalTV.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText( getActivity() , "Info : Lifetime  , Total Discount : " + lifetimeDisc  +" " +  lifetimepromotipe , Toast.LENGTH_LONG).show();
                                    }
                                });
                                discAmount = lifetimeDisc;
                                progressDialog.dismiss();
                            }
                            if(usingLifetime.equalsIgnoreCase("No") && usingSpecial.equalsIgnoreCase("No")){
                                tvPromo.setVisibility(View.VISIBLE);
                                requestPromotion();
                                promotionSpinner.setVisibility(View.VISIBLE);
                            }else{
                                promotionSpinner.setOnItemSelectedListener(null);
                            }

                            if(isGwp){
                                tvPromo.setVisibility(View.GONE);
                                promotionSpinner.setVisibility(View.GONE);
                                hargaItemTV.setText("Harga : Rp. " +currencyFormat(String.valueOf(0)));
                                usingSpecial    = "ya";
                                TipePromosi     = "SpecialPrice";
                                newsubtotal     = 0;
                                QuantityVal     = 0;
                                quantityTV.setText(String.valueOf(1));
                                tagihan         = 0;
                                tagihanTV.setText(currencyFormat(String.valueOf(tagihan)));
                            }
                            subtotalTV.setText("Setelah Diskon Rp. " + currencyFormat(String.valueOf(newsubtotal)));
                            simpanDetail.setVisibility(View.VISIBLE);
                            quantityTV.setEnabled(true);
                            tagihanTV.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            simpanDetail.setVisibility(View.GONE);
                            quantityTV.setEnabled(false);
                            tagihanTV.setVisibility(View.GONE);
                            tvPromo.setVisibility(View.GONE);
                            hargaItemTV.setText("");
                            subtotalTV.setText("");
                            Toast.makeText(getActivity(), "Harap Cek Item Kembali", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), " Wops, ada kesalahkan. Silahkan di coba kembali", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.d("ErrorResponse", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("ItemCode", itemcode.getText().toString());
                params.put("OutletCode", OutletCode);
                params.put("Tanggal", TanggalNota);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0.00");
        return formatter.format(Double.parseDouble(amount));
    }
    private void requestPromotion(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive+"Welcomebaru/getpromotion/" + TanggalNota + "/" +OutletCode ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        spinnerData = Arrays.asList(mGson.fromJson(response, Promotion_model[].class));
                        if(null != spinnerData){
                            assert promotionSpinner != null;
                            Promotion_adapter spinnerAdapter = new Promotion_adapter(getActivity(), spinnerData);
                            promotionSpinner.setAdapter(spinnerAdapter);
                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), " Ada kesalahan teknis, harap buka ulang halaman ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    @Override public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
    public void saveDetail(){
        try{
            i = Integer.parseInt(quantityTV.getText().toString());
        }catch(NumberFormatException ex){
            i = 0;
        }
        QuantityVal = i;
        if(QuantityVal == 0) {
            Toast.makeText(getActivity(), " Harap masukan Quantity !!!", Toast.LENGTH_SHORT).show();
            return;
        }
        catatanvalue = "";
        catatanvalue = catatan.getText().toString();
        if(isEditTotal == 1){
            if(catatanvalue.equals("") || TextUtils.isEmpty(catatanvalue)){
                Toast.makeText(getActivity(), " Harap mengisi kolom catatan !!! ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyimpan ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //yang dibutuhkan
        // OutletCode, No Nota, tanggalNota, ItemCode, KodePromosi ,harga asli, harga setelah discount,Qty
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive + "Welcomebaru/savesalesDetail/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            saveStatus              = jsonObj.getString("Status");
                            progressDialog.dismiss();
                            if(saveStatus.equalsIgnoreCase("OK")){
                                Bundle bundle=new Bundle();
                                bundle.putInt("Status", 0);
                                bundle.putString("OutletCode", OutletCode);
                                bundle.putString("Seller", Seller);
                                bundle.putString("TransactionDate", TanggalNota);
                                bundle.putString("StatusPosting", StatusPosting);
                                Sales_detail fragobj=new Sales_detail();
                                fragobj.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                getActivity().getSupportFragmentManager().popBackStack();
                                ft.replace(R.id.content_frame, fragobj).addToBackStack(null);
                                ft.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            new LovelyStandardDialog(getActivity(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTitle("Proses Simpan Gagal")
                                    .setMessage("Apakah anda ingin mencoba kembali ?. \n ")
                                    .setCancelable(false)
                                    .setIcon(R.drawable.frown)
                                    .setNegativeButton("Tidak", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle=new Bundle();
                                    bundle.putInt("Status", 0);
                                    bundle.putString("OutletCode", OutletCode);
                                    bundle.putString("TanggalNota", TanggalNota);
                                    bundle.putString("Seller", Seller);
                                    bundle.putString("StatusPosting", StatusPosting);
                                    Sales_form_detail fragobj=new Sales_form_detail();
                                    fragobj.setArguments(bundle);
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    ft.replace(R.id.content_frame, fragobj);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    ft.commit();
                                }
                            })
                                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            saveDetail();
                                        }
                                    }).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        new LovelyStandardDialog(getActivity(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTitle("Proses Simpan Gagal")
                                .setMessage("Apakah anda ingin mencoba kembali ?. \n ")
                                .setCancelable(false)
                                .setIcon(R.drawable.frown)
                                .setNegativeButton("Tidak", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle=new Bundle();
                                        bundle.putInt("Status", 0);
                                        bundle.putString("OutletCode", OutletCode);
                                        bundle.putString("TanggalNota", TanggalNota);
                                        bundle.putString("StatusPosting", StatusPosting);
                                        Sales_form_detail fragobj=new Sales_form_detail();
                                        fragobj.setArguments(bundle);
                                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.content_frame, fragobj);
                                        getActivity().getSupportFragmentManager().popBackStack();

                                        ft.commit();
                                    }
                                }).setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveDetail();
                                    }
                                }).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("OutletCode", OutletCode);
                params.put("TanggalNota", TanggalNota);
                params.put("ItemCode", itemcode.getText().toString());
                params.put("PromotionCode", TipePromosi);
                params.put("HargaItem", HargaItem);
                params.put("HargaSetelahPromo", String.valueOf(newsubtotal));
                params.put("Quantity",String.valueOf(QuantityVal));
                params.put("Tagihan",String.valueOf(tagihan));
                params.put("DiscountAmount",String.valueOf(discAmount));
                params.put("Seller",Seller);
                params.put("CreatedBy",sess_model.getUsername());
                params.put("NoSku",NoSku);
                params.put("Catatan",catatanvalue);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    private void checkAllowed() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive + "Welcomebaru/checkAllowed/" + OutletCode + "/" + TanggalNota,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            Integer IsError = jsonObj.getInt("Status");
                            if (IsError == 1) {
                                iconedit.setVisibility(View.VISIBLE);
                            }
                            progressDialog.hide();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}