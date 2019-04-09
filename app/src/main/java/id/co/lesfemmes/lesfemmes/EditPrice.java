package id.co.lesfemmes.lesfemmes;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class EditPrice extends Fragment {

    Spinner spinner;
    protected List<Outlet_model> spinnerData;
    ProgressDialog progressDialog;
    String userLogin;

    String urlactive;
    Session_model sess_model;
    DatePickerDialog datePickerDialog;
    Button addBtn;
    private int mYear, mMonth, mDay;
    String theDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_price, container, false);
        spinner = v.findViewById(R.id.outlet);
        progressDialog = new ProgressDialog(getActivity());
        sess_model = new Session_model(getContext());
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        userLogin = sess_model.getUsername();
        requestOutlet();

        addBtn = v.findViewById(R.id.AddBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
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
                                theDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                Toast.makeText(getActivity(), "  " + theDate, Toast.LENGTH_SHORT).show();
                                // TODO Buat database, api & adapter...


                            }

                        }, mYear, mMonth, mDay);

                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {

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
