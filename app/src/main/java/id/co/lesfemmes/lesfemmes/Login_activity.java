package id.co.lesfemmes.lesfemmes;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.andreabaccega.widget.FormEditText;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class Login_activity extends AppCompatActivity {
    String urlactive;
    private SharedPreferences sharedPreferences;
    Button login_btn;
    String username,password,deviceId;
    FormEditText username_tv,password_tv;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            setContentView(R.layout.login_layout);
            loginCheck();
            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            username_tv = findViewById(R.id.username_tv);
            password_tv = findViewById(R.id.password_tv);
            login_btn = findViewById(R.id.login_btn);
            progressDialog = new ProgressDialog(Login_activity.this);
            progressDialog.setMessage("Harap tunggu");
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    username = username_tv.getText().toString();
                    password = password_tv.getText().toString();
                    FormEditText[] allFields	= { username_tv, password_tv };
                    boolean allValid = true;
                    for (FormEditText field: allFields) {
                        allValid = field.testValidity() && allValid;
                    }
                    if (allValid) {
                        loginRequest();
                    }
                }
            });
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        deviceId = token;
                    }
                });
        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    private void loginRequest(){
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(Login_activity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"Welcomebaru/login",
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObj      = new JSONObject(response);
                        Integer status          = jsonObj.getInt("status");
                        String fullname_db      = jsonObj.getJSONObject("datauser").getString("fullname");
                        String username_db      = jsonObj.getJSONObject("datauser").getString("username");
                        String outlete_db       = jsonObj.getJSONObject("datauser").getString("outlet");
                        String userimagee_db    = jsonObj.getJSONObject("datauser").getString("userimage");
                        String posisie_db       = jsonObj.getJSONObject("datauser").getString("posisi");
                        String namaposisie_db   = jsonObj.getJSONObject("datauser").getString("namaposisi");
                        if(status == 1)
                        {
                            username_tv.setText("");
                            password_tv.setText("");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("fullname_session", fullname_db);
                            editor.putString("username_session", username_db);
                            editor.putString("outlet_session", outlete_db);
                            editor.putString("image_session", userimagee_db);
                            editor.putString("posisi_session", posisie_db);
                            editor.putString("namaposisi_session", namaposisie_db);
                            editor.apply();
                            Intent intent = new Intent(Login_activity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            username_tv.setText("");
                            password_tv.setText("");
                            new LovelyStandardDialog(Login_activity.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTitle("Login gagal")
                                    .setMessage("Harap Cek kembali username dan password anda. \n ")
                                    .setCancelable(false)
                                    .setIcon(R.drawable.frown)
                                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) { }
                                    }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        username_tv.setText("");
                        password_tv.setText("");
                        new LovelyStandardDialog(Login_activity.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTitle("Login gagal")
                                .setMessage("Harap coba kembali. \n ")
                                .setCancelable(false)
                                .setIcon(R.drawable.frown)
                                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {}
                                }).show();
                    }
                },
                error -> Log.d("ErrorResponse", error.toString())
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password",password);
                params.put("deviceId",deviceId);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
    public void loginCheck(){
        SharedPreferences loginPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        if (loginPreferences.contains("username_session")) {
            Intent intentMain = new Intent(Login_activity.this, MainActivity.class);
            Login_activity.this.startActivity(intentMain);
            finish();
        }
    }
}