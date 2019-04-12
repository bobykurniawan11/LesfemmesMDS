package id.co.lesfemmes.lesfemmes.sales;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;

public class Sales_detail extends Fragment  {

FloatingActionButton fab;
String NoNota,TanggalNota,Seller,OutletCode,selectedIdHeader,selectedSku;
Integer Status;
TextView tv_TanggalNota,tv_Seller,tv_OutletCode,grandtotal;
int selectedPosition;
String selectedItemCode,selectedNoNota,StatusPosting;
Button posting;

    private List<Sales_detail_model> detailList;
    private RecyclerView recyclerView;
    private Sales_detail_adapter mAdapter;
    ProgressDialog progressDialog;
    String urlactive;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sales_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        setHasOptionsMenu(true);

        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Input Sales Detail");


        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        tv_TanggalNota = v.findViewById(R.id.tanggal_nota);
        tv_Seller = v.findViewById(R.id.seller);
        tv_OutletCode = v.findViewById(R.id.outletcode);

        grandtotal = v.findViewById(R.id.grandtotal);
        detailList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.sales_detail);
        mAdapter = new Sales_detail_adapter(getActivity(),detailList);
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyiapkan Halaman");
        progressDialog.show();
        recyclerView.setNestedScrollingEnabled(false);
        posting = v.findViewById(R.id.posting);

        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        if (getArguments() != null)
        {
            Bundle args = getArguments();
            Status = getArguments().getInt("Status");
            StatusPosting =  getArguments().getString("StatusPosting");
            if(Status == 0) {
                OutletCode  = getArguments().getString("OutletCode");
                TanggalNota     = getArguments().getString("TransactionDate");
                getSalesHeader();
                posting.setEnabled(true);
            }else if(Status == 1){
                TanggalNota     = getArguments().getString("Tanggal");
                Seller          = getArguments().getString("Seller");
                OutletCode      = getArguments().getString("OutletCode");
                StatusPosting   = "0";
                tv_TanggalNota.setText(TanggalNota);
                tv_Seller.setText(Seller);
                tv_OutletCode.setText(OutletCode);
                progressDialog.hide();
                posting.setEnabled(false);
            }
        }
        mAdapter.setOnItemClickListener(new Sales_detail_adapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                selectedPosition = position;
                String thePromotion,selectedItem;
                thePromotion = detailList.get(position).getDisc();
                selectedItem = detailList.get(position).getHarga();
                if(thePromotion.equalsIgnoreCase("BUY 1 GET 1")){
                    Bundle bundle=new Bundle();
                    bundle.putInt("Status", 0);
                    bundle.putString("OutletCode", OutletCode);
                    bundle.putString("TanggalNota", TanggalNota);
                    bundle.putString("Seller", Seller);
                    bundle.putString("NoSku", selectedSku);
                    bundle.putString("itemprice", selectedItem);
                    FragmentManager ft = getActivity().getFragmentManager();
                    Dialog_buyone dialogFragment = new Dialog_buyone();
                    dialogFragment.setCancelable(false);
                    dialogFragment.setArguments(bundle);
                    dialogFragment.show(ft, "dialog");
                }
            }

            @Override
            public void onItemLongClick(int position, View v) {
                if( (StatusPosting.equals(1)) || (StatusPosting.equals("1")) || (StatusPosting.equals(2)) ||(StatusPosting.equals("2")) ){
                    Toast.makeText(getActivity(), "Data Sudah di Posting , Tidak dapat Menghapus !!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedPosition = position;
                selectedItemCode = detailList.get(position).getItemCode();
                selectedIdHeader = detailList.get(position).getIdHeader();

               // return;

                new LovelyStandardDialog(getActivity(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTitle("Hapus Data")
                        .setMessage("Apa anda ingin menghapus data ini ? \n ")
                        .setCancelable(false)
                        .setNegativeButton("Tidak", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        })
                        .setPositiveButton("Ya", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDetail();
                                mAdapter.notifyItemRemoved(selectedPosition);
                                mAdapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        });
        posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyStandardDialog(getActivity(), LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTitle("Posting Data")
                        .setMessage("Apakah anda yakin ? \n ")
                        .setCancelable(false)
                        .setNegativeButton("Tidak", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        })
                        .setPositiveButton("Ya", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                posting();
                            }
                        }).show();
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sales_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(amount));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            if( (StatusPosting.equals(1)) || (StatusPosting.equals("1")) || (StatusPosting.equals(2)) ||(StatusPosting.equals("2")) ){
            Toast.makeText(getActivity(), "Data Sudah di Posting , Tidak dapat Menambah Item !!!", Toast.LENGTH_SHORT).show();
            return false;
        }
            Bundle bundle=new Bundle();
            bundle.putInt("Status", 0);
            bundle.putString("OutletCode", OutletCode);
            bundle.putString("TanggalNota", TanggalNota);
            bundle.putString("Seller", Seller);
            bundle.putString("StatusPosting", String.valueOf(StatusPosting));

            Sales_form_detail fragobj=new Sales_form_detail();
            fragobj.setArguments(bundle);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame).toString());
            ft.replace(R.id.content_frame, fragobj);

            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onDestroy() {
        super.onDestroy();
    }
    private void getSalesHeader(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"Welcomebaru/getSalesHeaderlatest/",
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj      = new JSONObject(response);
                            Seller              = jsonObj.getJSONObject("Sales").getString("Nik");
                            OutletCode          = jsonObj.getJSONObject("Sales").getString("CustomerCode");
                            StatusPosting       = jsonObj.getJSONObject("Sales").getString("Status");
                            tv_TanggalNota.setText(TanggalNota);
                            tv_Seller.setText(Seller);
                            tv_OutletCode.setText(OutletCode);
                            grandtotal.setText("Rp. "+currencyFormat(jsonObj.getJSONObject("Sales").getString("TotalHarga")));
                            if(StatusPosting.equals("1")){
                                posting.setText("SUDAH POSTING");
                                posting.setEnabled(false);
                            }
                            if(StatusPosting.equals("2")){
                                posting.setText("SUDAH PROSES");
                                posting.setEnabled(false);
                            }
                            if(StatusPosting.equals("3")){
                                posting.setText("Sales Di Cancel");
                                posting.setEnabled(false);
                            }
                            getSalesDetail();
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Woops, sepertinya ada kesalahan teknis. Silahkan buka ulang halaman ini", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Log.d("ErrorResponse", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("OutletCode", OutletCode);
                params.put("TransactionDate", TanggalNota);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    private void getSalesDetail(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"/Welcomebaru/getSalesDetail/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObjd         = new JSONObject(response);
                            JSONArray arrayDetail       = jsonObjd.getJSONArray("Salesd");
                            for(int i = 0; i < arrayDetail.length();i++){
                                JSONObject detail = arrayDetail.getJSONObject(i);
                                Sales_detail_model sdn = new Sales_detail_model(
                                            detail.getString("IdHeader"),
                                            detail.getString("ItemCode"),
                                            detail.getString("HargaItem"),
                                            detail.getString("PromotionID"),
                                            "0",
                                            detail.getString("Quantity"),
                                            detail.getString("HargaSetelahPromosi"),
                                            detail.getString("TotalHarga"),
                                            detail.getString("NoNota"),
                                            detail.getInt("Status"),
                                            detail.getString("Note"),
                                            detail.getString("NomerSKU")
                                            );
                                            detailList.add(sdn);
                            }
                            mAdapter = new Sales_detail_adapter(getActivity(),detailList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            progressDialog.hide();
                        } catch (JSONException e) {
                            progressDialog.hide();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorResponse", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("TanggalNota", TanggalNota);
                params.put("OutletCode", OutletCode);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    private void deleteDetail(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"Welcomebaru/deleteSalesdetail/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObjd         = new JSONObject(response);
                            String saveStatus              = jsonObjd.getString("Status");
                            if(saveStatus.equalsIgnoreCase("OK")){
                                detailList.remove(selectedPosition);
                                mAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), " Hapus Item Berhasil ", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getActivity(), " Hapus Item Gagal ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), " Hapus Item Gagal ", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorResponse", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("IdHeader", selectedIdHeader);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    private void posting(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"/Welcomebaru/posting/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObjd         = new JSONObject(response);
                            String saveStatus              = jsonObjd.getString("Status");
                            if(saveStatus.equalsIgnoreCase("OK")){
                                Toast.makeText(getActivity(), " Posting Berhasil ", Toast.LENGTH_SHORT).show();
                                Bundle bundle=new Bundle();
                                bundle.putInt("Status", 0);
                                bundle.putString("OutletCode", OutletCode);
                                bundle.putString("TransactionDate", TanggalNota);
                                bundle.putString("Seller", Seller);
                                bundle.putString("NoSku", selectedSku);
                                Sales_detail fragobj=new Sales_detail();
                                fragobj.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                ft.addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame).toString());
                                ft.replace(R.id.content_frame, fragobj);
                                ft.commit();
                            }else{
                                Toast.makeText(getActivity(), " Posting Gagal ", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), " Posting Gagal ", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorResponse", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();
                params.put("TanggalNota", TanggalNota);
                params.put("OutletCode", OutletCode);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        progressDialog.dismiss();
    }
}