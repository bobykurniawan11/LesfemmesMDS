package id.co.lesfemmes.lesfemmes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Dialog_editpassword extends DialogFragment {
EditText passwordET,passwordET2;
String username,passwordlama,passwordbaru;
    ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
Button tombolGanti;
    String urlactive;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_editpassword, container, false);
        username          = getArguments().getString("Username");
        passwordET        = v.findViewById(R.id.passwordET);
        passwordET2       = v.findViewById(R.id.password2ET);
        tombolGanti       = v.findViewById(R.id.tombolGanti);
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



        tombolGanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordlama = passwordET.getText().toString();
                passwordbaru = passwordET2.getText().toString();
                if(passwordbaru.equals("")){
                    Toast.makeText(getActivity(), "Harap masukan password baru anda", Toast.LENGTH_SHORT).show();
                    passwordET2.requestFocus();
                    return;
                }
                loginRequest();
            }
        });
        return v;
    }

    private void loginRequest(){
        RequestQueue queue = Volley.newRequestQueue(getDialog().getContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"welcome/login",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObj      = new JSONObject(response);
                            Integer status          = jsonObj.getInt("status");
                            if(status == 1)
                            {
                                progressDialog = new ProgressDialog(getDialog().getContext());
                                progressDialog.setMessage("Harap tunggu");
                                progressDialog.show();
                                updatePassword();
                            }else{
                                Toast.makeText(getActivity(), "Harap cek kembali, password lama anda", Toast.LENGTH_SHORT).show();
                                passwordET.requestFocus();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorResponse", error.toString());
                        progressDialog.dismiss();
                        Toast.makeText(getDialog().getContext(), "Harap coba kembali", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password",passwordlama);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void updatePassword(){
        RequestQueue queue = Volley.newRequestQueue(getDialog().getContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"welcome/gantiPassword",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getDialog().getContext(), "Pasword sudah di ubah", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        sharedPreferences.edit().clear().apply();
                        Intent intent = new Intent(getDialog().getContext(), Login_activity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorResponse", error.toString());
                        Toast.makeText(getDialog().getContext(), "Harap coba kembali", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", passwordbaru);
                params.put("passwordlama", passwordlama);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
}