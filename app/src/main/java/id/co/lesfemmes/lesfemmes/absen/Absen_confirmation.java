package id.co.lesfemmes.lesfemmes.absen;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;

public class Absen_confirmation extends DialogFragment {
    Button okButton;

    String nik_val, alamat_val, urlactive;
    String tipe;
    String longitude, latitude;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        View v = inflater.inflate(R.layout.absen_notification, container, false);
        TextView nik = v.findViewById(R.id.nik);
        TextView nama = v.findViewById(R.id.nama);
        TextView alamat = v.findViewById(R.id.alamat);

        nik.setText(getArguments().getString("Nik"));
        nama.setText(getArguments().getString("Nama"));
        alamat.setText(getArguments().getString("Alamat"));

        nik_val = getArguments().getString("Nik");
        alamat_val = getArguments().getString("Alamat");
        tipe = "0";
        longitude = String.valueOf(getArguments().getDouble("Longitude"));
        latitude = String.valueOf(getArguments().getDouble("Latitude"));

        okButton = v.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAbsensi();

            }
        });
        RadioGroup rb = v.findViewById(R.id.your_Radio_group_id);

        rb.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_masuk:
                        tipe = "0";
                        break;
                    case R.id.radio_keluar:
                        tipe = "1";
                        break;
                }
            }

        });


        return v;
    }

    private void saveAbsensi() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive + "Welcomebaru/absensi",
                response -> {
                    Log.d("Data => ", response);
                    dismiss();
                },
                error -> Log.d("ErrorResponse", error.toString())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nik", nik_val);
                params.put("alamat", alamat_val);
                params.put("tipe", tipe);
                params.put("longitude", longitude);
                params.put("latitude", latitude);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}
