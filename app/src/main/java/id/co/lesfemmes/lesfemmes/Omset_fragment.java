package id.co.lesfemmes.lesfemmes;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Omset_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Session_model sess;
    ArrayList<BarDataSet> yAxis;
    ArrayList<BarEntry> yValues;
    ArrayList<String> xAxis1;
    BarEntry values ;
    BarChart chart;
    String urlactive;
    String sess_username, sess_fullname, sess_outlet, sess_image;
    Button finddata;

    protected List<Outlet_model> spinnerData;
    String OutletCode;
    Integer selectedOutletPos;
    Spinner spinner;
    ProgressDialog progressDialog;

    EditText bulanInput,tahunInput;
    String bulanInputVal,tahunInputVal;


    public Omset_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Omset_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Omset_fragment newInstance(String param1, String param2) {
        Omset_fragment fragment = new Omset_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Omset");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_omset, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Menyiapkan Halaman");
        progressDialog.setCancelable(false);
        progressDialog.show();
        spinner = v.findViewById(R.id.outlet);
        chart =  v.findViewById(R.id.chart);
        tahunInput = v.findViewById(R.id.tahun);
        bulanInput = v.findViewById(R.id.bulan);

        bulanInputVal = "0";
        tahunInputVal = "0";

        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        sess = new Session_model(getActivity());
        sess_username = sess.getUsername();
        sess_fullname = sess.getFullname();
        sess_outlet = sess.getOutlet();
        sess_image = sess.getImagepath();
        OutletCode = sess_outlet;
        requestOutlet();
        finddata = v.findViewById(R.id.finddata);
        finddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                bulanInputVal= bulanInput.getText().toString();
                tahunInputVal= tahunInput.getText().toString();

                OutletCode = spinner.getSelectedItem().toString();
                load_data_from_server();
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
        progressDialog.dismiss();
        return v;
    }
    private void requestOutlet(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive+"welcome/getOutlet/" + sess_username,
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
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("###,###,##0");
        return formatter.format(Double.parseDouble(amount));
    }

    public void load_data_from_server() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"welcomebaru/populateChart/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            xAxis1 = new ArrayList<>();
                            yAxis = null;
                            yValues = new ArrayList<>();
                            JSONArray jsonarray = new JSONArray(response);
                            String score,name;
                            for(int i=0; i < jsonarray.length(); i++) {
                                JSONObject jsonobject = jsonarray.getJSONObject(i);
                                 score = jsonobject.getString("Totalharga");
                                 name = jsonobject.getString("TanggalNota");
                                 xAxis1.add(name);
                                 values = new BarEntry(Float.valueOf(score),i);
                                 yValues.add(values);
                            }
                            BarDataSet barDataSet1 = new BarDataSet(yValues, "Omset");
                            barDataSet1.setColor(Color.rgb(0, 82, 19));
                            yAxis = new ArrayList<>();
                            yAxis.add(barDataSet1);
                            String names[]= xAxis1.toArray(new String[xAxis1.size()]);

                            final BarData data = new BarData(names,yAxis);
                            chart.setData(data);
                            chart.animateXY(2000, 2000);
                            chart.setDrawValueAboveBar(true);
                            chart.setMaxVisibleValueCount(30);
                            chart.setPinchZoom(true);
                            chart.setDrawGridBackground(true);
                            chart.invalidate();

                            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
                            {
                                @Override
                                public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                                    String nominal = currencyFormat(String.valueOf(e.getVal()));
                                    Toast.makeText( getActivity() , "Rp." + nominal, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNothingSelected()
                                {

                                }
                            });
                            Legend l = chart.getLegend();
                            l.setFormSize(9f);
                            l.setTextSize(11f);
                            l.setXEntrySpace(4f);

                            progressDialog.dismiss();
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
                params.put("Bulan", bulanInputVal);
                params.put("Tahun", tahunInputVal);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}
