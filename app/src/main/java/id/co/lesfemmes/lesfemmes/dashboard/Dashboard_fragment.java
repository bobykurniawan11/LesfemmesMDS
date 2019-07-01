package id.co.lesfemmes.lesfemmes.dashboard;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.Outlet_adapter;
import id.co.lesfemmes.lesfemmes.Outlet_model;
import id.co.lesfemmes.lesfemmes.R;
import id.co.lesfemmes.lesfemmes.Session_model;
import id.co.lesfemmes.lesfemmes.sales.Sales_detail;
import id.co.lesfemmes.lesfemmes.sales.Sales_header_adapter;
import id.co.lesfemmes.lesfemmes.sales.Sales_header_model;
import id.co.lesfemmes.lesfemmes.sales.Sales_input;


public class Dashboard_fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FloatingActionButton fab;
    View v;
    Fragment fragment = null;
    Spinner spinner;

    private List<Sales_header_model> headerList;
    private RecyclerView recyclerView;
    private Sales_header_adapter mAdapter;
    String OutletCode;
    Session_model sess_model;
    private int mYear, mMonth, mDay;
    EditText tanggal_et;
    Button searchBtn;
    protected List<Outlet_model> spinnerData;
    ProgressDialog progressDialog;
    String userLogin;
    String tanggal_input;
    Integer selectedOutletPos;

    String ClickValueOutletCode,clickedStatusPosting,ClickValueTransactionDate;
    String urlactive;
    Button clearTanggal;
    DatePickerDialog datePickerDialog;

    String requestFrom;
    SwipeRefreshLayout mSwipeRefreshLayout;
    MainActivity mYourActiviy;
    String sess_username;

    public Dashboard_fragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (sess_username.equals("0601338.")) {
            fab.hide();
        } else {
            fab.show();
        }
        mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Dashboard");
    }
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();

        requestFrom = "";
        fab = getActivity().findViewById(R.id.fab);
        searchBtn = v.findViewById(R.id.searchBtn);
        recyclerView = v.findViewById(R.id.monthlyomset);
        headerList = new ArrayList<>();
        sess_model = new Session_model(getContext());
        mAdapter = new Sales_header_adapter(headerList);
        progressDialog = new ProgressDialog(getActivity());

        sess_username = sess_model.getUsername();

        spinner = v.findViewById(R.id.outlet);
        tanggal_et = v.findViewById(R.id.tanggal_et);
        clearTanggal = v.findViewById(R.id.clearTanggal);

        clearTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggal_et.setText("");
                tanggal_input = "";
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new Sales_input();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame).toString());
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });

        if (sess_username.equals("0601338.")) {
            fab.hide();
        } else {
            fab.show();
        }

        mAdapter.setOnItemClickListener(new Sales_header_adapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                fab.hide();
                ClickValueOutletCode        = headerList.get(position).getOutletCode();
                ClickValueTransactionDate   = headerList.get(position).getTanggalTransaksi();
                clickedStatusPosting        = String.valueOf(headerList.get(position).getStatusPosting());

                Bundle bundle=new Bundle();
                bundle.putInt("Status", 0);
                bundle.putString("OutletCode", ClickValueOutletCode);
                bundle.putString("TransactionDate", ClickValueTransactionDate);
                bundle.putString("StatusPosting", clickedStatusPosting);

                Sales_detail fragobj=new Sales_detail();
                fragobj.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame).toString());
                ft.replace(R.id.content_frame, fragobj);
                ft.commit();
            }
            @Override
            public void onItemLongClick(int position, View v) {
                Toast.makeText(getActivity(), " " + headerList.get(position).getStatusPosting(), Toast.LENGTH_SHORT).show();
            }
        });
        tanggal_input = "";
        OutletCode = sess_model.getOutlet();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        progressDialog.setMessage("Menyiapkan Halaman");
        progressDialog.setCancelable(false);
        progressDialog.show();
        userLogin = sess_model.getUsername();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggal_input = tanggal_et.getText().toString();
                if (tanggal_input.matches("")) {
                    tanggal_input = "";
                }
                OutletCode = spinner.getSelectedItem().toString();
                if (OutletCode.matches("")) {
                    OutletCode = sess_model.getOutlet();
                }
                headerList.clear();
                progressDialog.setMessage("Mencari Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
                requestFrom = "searchBtn";
                getSalesHeader();
            }
        });
        tanggal_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tanggal_et.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                                tanggal_et.setText("");
                        }
                    }
                });
                datePickerDialog.show();
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                selectedOutletPos = arg2;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });
        getSalesHeader();
        requestOutlet();
        mSwipeRefreshLayout =  v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return  v;
    }
    @Override
    public void onRefresh() {
        headerList.clear();
        getSalesHeader();
        mAdapter.notifyDataSetChanged();
    }
    private void getSalesHeader(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"Welcomebaru/getSalesHeaderAll/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        try {
                            JSONObject jsonObjd      = new JSONObject(response);
                            JSONArray arrayDetail = jsonObjd.getJSONArray("Sales");
                            for(int i = 0; i < arrayDetail.length();i++){
                                JSONObject detail = arrayDetail.getJSONObject(i);
                                Sales_header_model sdn = new Sales_header_model(
                                        detail.getString("CustomerCode"),
                                        detail.getString("TransactionDate"),
                                        detail.getString("TransactionDate"),
                                        detail.getInt("SendStatus"),
                                        detail.getString("TotalHarga"),
                                        detail.getInt("Status")
                                        );
                                headerList.add(sdn);
                            }
                            mAdapter = new Sales_header_adapter(headerList);
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            if(requestFrom.equals("searchBtn")){
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        progressDialog.dismiss();
                        Log.d("ErrorResponse", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("OutletCode", OutletCode);
                params.put("Tanggal", tanggal_input);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    private void requestOutlet(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive+"Welcomebaru/getOutlet/" + userLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        spinnerData = Arrays.asList(mGson.fromJson(response, Outlet_model[].class));
                        if(null != spinnerData){
                            assert spinner != null;
                            spinner.setVisibility(View.VISIBLE);
                            Outlet_adapter spinnerAdapter = new Outlet_adapter(getActivity(), spinnerData);
                            spinner.setAdapter(spinnerAdapter);
                            progressDialog.dismiss();
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
