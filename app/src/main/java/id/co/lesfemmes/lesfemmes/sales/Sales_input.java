package id.co.lesfemmes.lesfemmes.sales;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.Outlet_adapter;
import id.co.lesfemmes.lesfemmes.Outlet_model;
import id.co.lesfemmes.lesfemmes.R;
import id.co.lesfemmes.lesfemmes.Seller;
import id.co.lesfemmes.lesfemmes.Seller_model;
import id.co.lesfemmes.lesfemmes.Session_model;

public class Sales_input extends Fragment implements  View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    private Calendar calendar;
    Session_model sess;
    FormEditText tanggal_transaksi_et;
    Button saveButton_btn;
    Button opendatedialog;
    private int mYear, mMonth, mDay;
    ProgressDialog progressDialog;
    protected List<Seller_model> spinnerData;
    protected List<Outlet_model> spinnerDataOutlet;
    Calendar c;
    Spinner spinner, spinnerOutlet;
    String OutletCodeInput, TanggalNotaInput, Seller, currentDateandTime;
    FloatingActionButton fab;
    String urlactive;
    Integer selectedOutletPos;
    Outlet_adapter spinnerOutletAdapter;
    Seller spinnerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sales_input, container, false);
        return  v;
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyiapkan Form");
        progressDialog.setCancelable(false);
        progressDialog.show();

        calendar = Calendar.getInstance();
        fab = getActivity().findViewById(R.id.fab);
        fab.hide();

        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Input Sales Header");

        sess = new Session_model(getActivity());
        tanggal_transaksi_et = v.findViewById(R.id.tanggal_transaksi_et);
        saveButton_btn = v.findViewById(R.id.saveButton_btn);
        opendatedialog = v.findViewById(R.id.opendialog);
        spinnerOutlet = v.findViewById(R.id.OutletCode_input_et);

        tanggal_transaksi_et.setEnabled(false);
        opendatedialog.setOnClickListener(this);
        saveButton_btn.setOnClickListener(this);

        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        requestOutlet();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            default:
                return false;
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
    @Override
    public void onClick(View v) {
        if (v == opendatedialog) {
            c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
            new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            tanggal_transaksi_et.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }, mYear, mMonth, mDay);
           datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            datePickerDialog.show();
        }

        if(v == saveButton_btn){
            progressDialog              = new ProgressDialog(getActivity());
            progressDialog.setMessage("Harap tunggu");
            progressDialog.setCancelable(true);
            progressDialog.show();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            TanggalNotaInput            = tanggal_transaksi_et.getText().toString();
            Seller                      = spinner.getSelectedItem().toString();
            Date currentTime            = Calendar.getInstance().getTime();
            currentDateandTime          = sdf.format(currentTime);
            checkSales();
        }

    }
    private void requestOutlet(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive+"Welcomebaru/getOutlet/" + sess.getUsername(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        spinnerDataOutlet = Arrays.asList(mGson.fromJson(response, Outlet_model[].class));
                        if(null != spinnerDataOutlet){
                            assert spinnerOutlet != null;
                            spinnerOutletAdapter = new Outlet_adapter(getActivity(), spinnerDataOutlet);
                            spinnerOutlet.setAdapter(spinnerOutletAdapter);
                            spinnerOutletAdapter.notifyDataSetChanged();
                        }
                        spinnerOutlet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                selectedOutletPos = arg2;
                                OutletCodeInput = spinnerDataOutlet.get(selectedOutletPos).getOutletCode();
                                requestSellerData();
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> arg0) {}
                        });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), " Ada kesalahan teknis, harap buka ulang menu", Toast.LENGTH_SHORT).show();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    private void requestSellerData(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive+"Welcomebaru/getListSeller/" + OutletCodeInput,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson  = builder.create();
                        spinnerData = Arrays.asList(mGson.fromJson(response, Seller_model[].class));
                        spinner = getActivity().findViewById(R.id.spinner);
                        if(null != spinnerDataOutlet) {
                            assert spinner != null;
                            spinner.setVisibility(View.VISIBLE);
                            spinnerAdapter = new Seller(getActivity(), spinnerData);
                            spinnerAdapter.notifyDataSetChanged();
                            spinner.setAdapter(spinnerAdapter);
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             progressDialog.dismiss();
                Toast.makeText(getActivity(), " " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
    private void checkSales(){
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"Welcomebaru/checkSales",
                response -> {
                    if(response.equals("Ok")){
                        SimpleDateFormat sdf        = new SimpleDateFormat("yyyy-MM-dd");
                        TanggalNotaInput            = tanggal_transaksi_et.getText().toString();
                        Seller                      = spinner.getSelectedItem().toString();
                        Date currentTime            = Calendar.getInstance().getTime();
                        currentDateandTime          = sdf.format(currentTime);

                        FormEditText[] allFields	= { tanggal_transaksi_et };
                        boolean allValid = true;
                        for (FormEditText field: allFields) {
                            allValid = field.testValidity() && allValid;
                        }

                        if (allValid) {
                            Bundle bundle=new Bundle();
                            bundle.putInt("Status", 1);
                            bundle.putString("OutletCode", OutletCodeInput);
                            bundle.putString("Tanggal", TanggalNotaInput);
                            bundle.putString("Seller", Seller);
                            progressDialog.dismiss();
                            Sales_detail fragobj=new Sales_detail();
                            fragobj.setArguments(bundle);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame).toString());
                            ft.replace(R.id.content_frame, fragobj);
                            ft.commit();
                        }
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Anda sudah input sales dengan tanggal yang sama.", Toast.LENGTH_LONG).show();
                        return;
                    }
                },
                error -> Log.d("ErrorResponse", error.toString())
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("OutletCode", OutletCodeInput);
                params.put("Tanggal", TanggalNotaInput);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}
