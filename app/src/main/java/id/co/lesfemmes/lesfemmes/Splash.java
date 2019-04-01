package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Splash extends AppCompatActivity {

    private static String urlactive;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    ImageView splashscreen;
    TextView versionName_tv,consignment_tv;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                Log.e("Tag","Err " + e);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", " -> "  +e);
        }



        versionName_tv = findViewById(R.id.appVersion);
        versionName_tv.setText("version : " + BuildConfig.VERSION_NAME);
        splashscreen = findViewById(R.id.splashscreen);
        consignment_tv = findViewById(R.id.consigmentText);

        versionName_tv.setVisibility(View.INVISIBLE);
        consignment_tv.setVisibility(View.INVISIBLE);

        Animation expandIn = AnimationUtils.loadAnimation(this, R.anim.expand_in);
        splashscreen.startAnimation(expandIn);


        expandIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                versionName_tv.setVisibility(View.VISIBLE);
                consignment_tv.setVisibility(View.VISIBLE);
                if (isServerReachable(Splash.this)) {
                    new Handler().postDelayed(new Runnable(){
                        @Override
                        public void run() {
                            Intent mainIntent = new Intent(Splash.this,Login_activity.class);
                            Splash.this.startActivity(mainIntent);
                            Splash.this.finish();
                        }
                    }, SPLASH_DISPLAY_LENGTH);
                }else{
                    new LovelyStandardDialog(Splash.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTitle("Tidak terhubung dengan server")
                    .setMessage("Mohon pastikan anda sudah mengaktifkan internet. \n ")
                    .setCancelable(false)
                    .setIcon(R.drawable.frown)
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                    }).show();
                }
            }
        });
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();
    }
    static public boolean isServerReachable(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            URL urlServer = null;
            try {
                urlServer = new URL(urlactive);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(10000);
                try {
                    urlConn.connect();
                    if (urlConn.getResponseCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public boolean isInternetAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {

            Log.e("isInternetAvailable:",e.toString());
            return false;
        }
    }


}
