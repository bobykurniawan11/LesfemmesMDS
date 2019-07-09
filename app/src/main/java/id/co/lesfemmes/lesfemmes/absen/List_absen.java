package id.co.lesfemmes.lesfemmes.absen;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;
import id.co.lesfemmes.lesfemmes.Session_model;


public class List_absen extends Fragment {
    static final String[] Months = new String[]{"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO: Rename and change types of parameters
    private static final String ARG_PARAM2 = "param2";
    Session_model sess;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    Integer urutTahun, xy, ab;
    Button searchBtn;
    private String bulan, urlactive, tahun, tanggal;
    private List<Absen_model> absenList;
    private Absen_adapter mAdapter;

    public List_absen() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("List Absensi");
        View v = inflater.inflate(R.layout.fragment_list_absen, container, false);
        sess = new Session_model(getActivity());
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        xy = 0;
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.get(Calendar.YEAR);

        bulan = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        tahun = String.valueOf(calendar.get(Calendar.YEAR));
        tanggal = String.valueOf(calendar.get(Calendar.DATE));

        absenList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Mempersiapkan Halaman");
        progressDialog.setCancelable(false);

        searchBtn = v.findViewById(R.id.searchBtn);

        ArrayList<String> days = new ArrayList<String>();
        for (int i = 1; i <= 31; i++) {
            days.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapterDays = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, days);

        MaterialSpinner spinDays = v.findViewById(R.id.spinnerDays);
        spinDays.setAdapter(adapterDays);
        spinDays.setSelectedIndex(calendar.get(Calendar.DATE) - 1);
        spinDays.setSelected(true);
        spinDays.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                tanggal = String.valueOf(position + 1);
            }
        });

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2018; i <= thisYear; i++) {
            years.add(Integer.toString(i));
            if (thisYear == i) {
                ab = xy;
            }
            xy++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MaterialSpinner spinYear = v.findViewById(R.id.years);
        spinYear.setAdapter(adapter);
        spinYear.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                tahun = item;
            }
        });

        spinYear.setSelectedIndex(ab);
        spinYear.setSelected(true);
        // Set months
        ArrayAdapter<String> adapterMonths = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Months);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        MaterialSpinner spinMonths = v.findViewById(R.id.months);

        spinMonths.setAdapter(adapterMonths);
        spinMonths.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                bulan = String.valueOf(position + 1);
            }
        });
        spinMonths.setSelectedIndex(calendar.get(Calendar.MONTH));
        spinMonths.setSelected(true);

        mAdapter = new Absen_adapter(absenList, getContext());

        recyclerView = v.findViewById(R.id.rc_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        populateAbsen();
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnItemClickListener(new Absen_adapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle = new Bundle();
                bundle.putString("Nama", absenList.get(position).getNama());
                bundle.putString("Nip", absenList.get(position).getNip());
                bundle.putString("Tanggal", absenList.get(position).getTanggal());
                bundle.putString("Long", absenList.get(position).getLong());
                bundle.putString("Lat", absenList.get(position).getLat());
                bundle.putString("Alamat", absenList.get(position).getAlamat());
                bundle.putString("JamMasuk", absenList.get(position).getJamMasuk());
                bundle.putString("JamKeluar", absenList.get(position).getJamKeluar());

                FragmentManager ft = getActivity().getFragmentManager();
                Dialog_absen_detail dialogFragment = new Dialog_absen_detail();

                dialogFragment.setArguments(bundle);
                dialogFragment.show(ft, "dialog");
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                populateAbsen();
            }
        });

        return v;
    }


    public void populateAbsen() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive + "Welcomebaru/get_absenuser",
                response -> {
                    progressDialog.dismiss();
                    try {
                        absenList.clear();
                        JSONObject jsonObjd = new JSONObject(response);
                        JSONArray arrayDetail = jsonObjd.getJSONArray("data_absen");
                        for (int i = 0; i < arrayDetail.length(); i++) {
                            JSONObject detail = arrayDetail.getJSONObject(i);
                            Absen_model sdn = new Absen_model(
                                    detail.getString("FullName"),
                                    detail.getString("Nip"),
                                    detail.getString("Tanggal"),
                                    detail.getString("jam_masuk"),
                                    detail.getString("jam_keluar"),
                                    detail.getString("AlamatAbsen"),
                                    detail.getString("Longitude"),
                                    detail.getString("Latitude")
                            );
                            absenList.add(sdn);
                        }

                        mAdapter = new Absen_adapter(absenList, getContext());
                        recyclerView.setAdapter(mAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    Log.d("ErrorResponse", error.toString());
                    progressDialog.dismiss();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nik", sess.getUsername());
                params.put("bulan", bulan);
                params.put("tahun", tahun);
                params.put("tanggal", tanggal);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}
