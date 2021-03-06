package id.co.lesfemmes.lesfemmes.absen;

import android.Manifest;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;
import id.co.lesfemmes.lesfemmes.Session_model;

public class Absen_fragment extends Fragment {

    View v;
    Button goAbsen, openDate;
    LocationManager locManager;
    Location location;
    double latitude, longitude;
    Session_model sess;
    String urlactive;
    ProgressDialog progressDialog;
    TextView jamMsktxt, jamKlrtxt, currentDate;
    SimpleDateFormat formatter;
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Absen");
        v = inflater.inflate(R.layout.fragment_absen, container, false);
        goAbsen = v.findViewById(R.id.goAbsen);
        sess = new Session_model(getActivity());
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Mempersiapkan Halaman");
        progressDialog.setCancelable(false);
        jamMsktxt = v.findViewById(R.id.jamMsktxt);
        jamKlrtxt = v.findViewById(R.id.jamKlrtxt);
        currentDate = v.findViewById(R.id.currentDate);
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentDate.setText(date);
        populateTime();
        goAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        showSettingsDialog();
                                        return;
                                    }
                                    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, locationListener);
                                    location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    getAddress(latitude, longitude);
                } else {
                    showSettingsDialog();
                    Toast.makeText(getActivity(), "Gagal, Pastikan GPS anda aktif", Toast.LENGTH_SHORT).show();
                }
            }
        });
        openDate = v.findViewById(R.id.openDate);
        openDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List_absen fragobj = new List_absen();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.addToBackStack(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_frame).toString());
                ft.replace(R.id.content_frame, fragobj);
                ft.commit();
            }
        });

        return v;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void updateWithNewLocation(Location location) {
        String latLongString = "";
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "Lat:" + lat + "\nLong:" + lng;
        } else {
            latLongString = "No location found";
        }
    }

    private Boolean getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            String country = addresses.get(0).getAddressLine(2);
            Bundle bundle = new Bundle();

            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getActivity());
            Intent intent = new Intent("NotifOpen");
            broadcaster.sendBroadcast(intent);
            bundle.putString("Alamat", address);
            bundle.putString("Nik", sess.getUsername());
            bundle.putString("Nama", sess.getFullname());
            bundle.putDouble("Latitude", latitude);
            bundle.putDouble("Longitude", longitude);

            FragmentManager ft = getActivity().getFragmentManager();
            Absen_confirmation dialogFragment = new Absen_confirmation();

            dialogFragment.setArguments(bundle);
            dialogFragment.setCancelable(true);
            dialogFragment.show(ft, "dialog");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void populateTime() {
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive + "Welcomebaru/get_absenhariini",
                response -> {
                    Log.d("Data => ", response);
                    progressDialog.dismiss();
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response);
                        Integer Status = jsonObj.getInt("Status");

                        if (Status == 0) {

                        } else {
                            String db_jam_masuk = jsonObj.getJSONObject("Data").getString("jam_masuk");
                            String db_jam_keluar = jsonObj.getJSONObject("Data").getString("jam_keluar");
                            jamMsktxt.setText("Jam Masuk Hari Ini : " + db_jam_masuk);
                            jamKlrtxt.setText("Jam Pulang Hari Ini : " + db_jam_keluar);
                        }
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
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}
