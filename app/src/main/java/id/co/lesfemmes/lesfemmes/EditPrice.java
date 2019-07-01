package id.co.lesfemmes.lesfemmes;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import id.co.lesfemmes.lesfemmes.sales.EditPrice_model;


public class EditPrice extends Fragment {

    Spinner spinner;
    protected List<Outlet_model> spinnerData;
    ProgressDialog progressDialog;
    String userLogin;
    String urlll;

    String urlactive, CustomerCode;
    Session_model sess_model;
    DatePickerDialog datePickerDialog;
    Button addBtn, searchButton;
    private int mYear, mMonth, mDay;
    String theDate, theMonth, theYear;
    RecyclerView recyclerView;

    EditText bulanET, tahunET;
    String tanggalAwalVal, tanggalAkhirVal;

    private List<EditPrice_model> editList;
    private EditPrice_adapter mAdapter;
    Outlet_adapter spinnerAdapter;
    int selectedOutletPos;
    String selectedCustomer, selectedTransactionDate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_price, container, false);
        spinner = v.findViewById(R.id.outlet);
        searchButton = v.findViewById(R.id.searchBtn);

        bulanET = v.findViewById(R.id.bulan);
        tahunET = v.findViewById(R.id.tahun);

        progressDialog = new ProgressDialog(getActivity());
        sess_model = new Session_model(getContext());
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        userLogin = sess_model.getUsername();
        urlll = urlactive + "Welcomebaru/getAllowedEditTransaction/" + CustomerCode;
        requestOutlet();
        theMonth = "";
        theYear = "";
        addBtn = v.findViewById(R.id.AddBtn);
        recyclerView = v.findViewById(R.id.rc_list);
        editList = new ArrayList<>();
        mAdapter = new EditPrice_adapter(editList, getContext());
        progressDialog = new ProgressDialog(getActivity());
        spinnerAdapter = new Outlet_adapter(getActivity(), spinnerData);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        TextView tanggal_awal = v.findViewById(R.id.fromDate);
        TextView tanggal_akhir = v.findViewById(R.id.toDate);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                progressDialog.setMessage("...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                CustomerCode = spinnerData.get(arg2).OutletCode;
                urlll = urlactive + "Welcomebaru/getAllowedEditTransaction/" + CustomerCode;
                requestList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerCode = spinner.getSelectedItem().toString();
                theMonth = bulanET.getText().toString();
                theYear = tahunET.getText().toString();
                progressDialog.setMessage("...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                if (theMonth.isEmpty()) {
                    urlll = urlactive + "Welcomebaru/getAllowedEditTransaction/" + CustomerCode;
                } else {
                    urlll = urlactive + "Welcomebaru/getAllowedEditTransaction/" + CustomerCode + "/" + theMonth + "/" + theYear;
                }
                requestList();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tanggalAwalVal = tanggal_awal.getText().toString();
                tanggalAkhirVal = tanggal_akhir.getText().toString();
                theDate = tanggalAwalVal + " - " + tanggalAkhirVal;
                progressDialog.show();
                save();
            }
        });

        mAdapter.setOnItemClickListener(new EditPrice_adapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(getActivity(), "Tekan Lama Untuk Menghapus", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position, View v) {
                selectedCustomer = editList.get(position).getCustomerCode();
                selectedTransactionDate = editList.get(position).getTransactionDate();
                delete();
            }
        });

        tanggal_awal.setOnClickListener(new View.OnClickListener() {
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
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tanggal_awal.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            tanggal_awal.setText("");
                        }
                    }
                });
                datePickerDialog.show();
            }
        });

        tanggal_akhir.setOnClickListener(new View.OnClickListener() {
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
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tanggal_akhir.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            tanggal_akhir.setText("");
                        }
                    }
                });
                datePickerDialog.show();
            }
        });

        return v;
    }
    private void requestOutlet() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive + "Welcomebaru/getOutlet/" + userLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        spinnerData = Arrays.asList(mGson.fromJson(response, Outlet_model[].class));
                        if (null != spinnerData) {
                            assert spinner != null;
                            spinner.setVisibility(View.VISIBLE);
                            spinnerAdapter = new Outlet_adapter(getActivity(), spinnerData);
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

    private void requestList() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlll,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        editList.clear();
                        try {
                            JSONObject jsonObjd = new JSONObject(response);
                            JSONArray arrayDetail = jsonObjd.getJSONArray("Sales");
                            for (int i = 0; i < arrayDetail.length(); i++) {
                                JSONObject detail = arrayDetail.getJSONObject(i);
                                EditPrice_model sdn = new EditPrice_model(
                                        detail.getString("CustomerCode"),
                                        detail.getString("TransactionDate")
                                );
                                editList.add(sdn);
                            }
                            mAdapter = new EditPrice_adapter(editList, getContext());
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
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

    private void save() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive + "Welcomebaru/saveAllowed/" + CustomerCode + "/" + tanggalAwalVal + "/" + tanggalAkhirVal + "/" + userLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length() > 0) {
                            try {
                                JSONObject jsonObj = new JSONObject(response);
                                Integer IsError = jsonObj.getInt("IsError");
                                if (IsError == 1) {
                                    String Message = jsonObj.getString("Message");
                                    Toast.makeText(getActivity(), Message, Toast.LENGTH_LONG).show();
                                } else {
                                    requestList();
                                }
                                progressDialog.hide();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.hide();
                            }
                        } else {
                            progressDialog.hide();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    private void delete() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive + "Welcomebaru/deleteAllowed/" + selectedCustomer + "/" + selectedTransactionDate,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

}
