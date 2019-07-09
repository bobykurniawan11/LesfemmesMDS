package id.co.lesfemmes.lesfemmes;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import id.co.lesfemmes.lesfemmes.absen.Absen_fragment;
import id.co.lesfemmes.lesfemmes.custom.Utils;
import id.co.lesfemmes.lesfemmes.dashboard.Dashboard_fragment;
import id.co.lesfemmes.lesfemmes.notification.Notification_fragment;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {
    int mNotificationsCount;
    Fragment fragment = null;
    Session_model sess;
    String sess_username, sess_fullname, sess_outlet, sess_image;
    FloatingActionButton fab;
    String urlactive;
    CircularImageView imageView;
    Integer sess_isAllowed;
    private SharedPreferences sharedPreferences;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
            mNotificationsCount = mNotificationsCount + 1;
            updateNotificationsBadge(mNotificationsCount);
        }
    };
    private BroadcastReceiver openNotificationEvent = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
            mNotificationsCount = mNotificationsCount - 1;
            updateNotificationsBadge(mNotificationsCount);
        }
    };

    public String geturlactive() {
        return "http://192.168.3.223:84/lesfemmesapi/";
    }

    public void setCustomActionbar(String myCustomTitle) {
        MainActivity.this.getSupportActionBar().setTitle(myCustomTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        setCustomActionbar("Dashboard");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_notif);
        LayerDrawable icon = (LayerDrawable) item.getIcon();
        Utils.setBadgeCount(this, icon, mNotificationsCount);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_notif) {
            fragment = new Notification_fragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("Dashboard_fragment{25ccb1e9 #0 id=0x7f080043}");
            ft.replace(R.id.content_frame, fragment);
            setCustomActionbar("Notifikasi");
            ft.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        sess = new Session_model(this);

        urlactive = geturlactive();
        sess_username = sess.getUsername();
        sess_fullname = sess.getFullname();
        sess_outlet = sess.getOutlet();
        sess_image = sess.getImagepath();
        sess_isAllowed = sess.getAllow_editprice();
        setSupportActionBar(toolbar);

        mNotificationsCount = 0;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Intent intent = getIntent();
        fab = findViewById(R.id.fab);
        if (intent.hasExtra("notifFragment")) {
            fragment = new Notification_fragment();
            fab.hide();
        } else {
            fragment = new Dashboard_fragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        setCustomActionbar("Dashboard");
        ft.commit();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.name);
        TextView navOutlet = headerView.findViewById(R.id.outlet);
        imageView = headerView.findViewById(R.id.imageView);

        getImage();
        navUsername.setText(sess_username + " - " + sess_fullname);
        navOutlet.setText(sess_outlet);
        handleSSLHandshake();
        getCountNotification();
        Menu nav_Menu = navigationView.getMenu();
        if (sess_isAllowed == 1) {
            nav_Menu.findItem(R.id.nav_allowEditSett).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_allowEditSett).setVisible(false);
        }
    }

    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }

    public void getImage() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive + "Welcomebaru/getImage/",
                response -> {
                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        String userimage = jsonObj.getString("userimage");
                        if (!userimage.equals("")) {
                            GlideApp.with(MainActivity.this).load(urlactive + "assets/userprofil/" + userimage)
                                    .override(300, 300)
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imageView);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.d("ErrorResponse", error.toString())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", sess_username);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(openNotificationEvent);
    }

    @Override
    public void onBackStackChanged() {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("Dashboard_fragment{25ccb1e9 #0 id=0x7f080043}");
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (id == R.id.main) {
            showFloatingActionButton();
            setCustomActionbar("Dashboard");
            fragment = new Dashboard_fragment();
            getSupportFragmentManager().popBackStack();
        } else if (id == R.id.nav_manage) {
            hideFloatingActionButton();
            setCustomActionbar("Setting");
            fragment = new Setting_fragment();
        } else if (id == R.id.nav_help) {
            Intent intentMain = new Intent(MainActivity.this, HelpActivity.class);
            MainActivity.this.startActivity(intentMain);
        } else if (id == R.id.nav_omset) {
            hideFloatingActionButton();
            setCustomActionbar("Omset");
            fragment = new Omset_fragment();
        } else if (id == R.id.logout) {
            sharedPreferences = getSharedPreferences(Login_activity.MyPREFERENCES, Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
            Intent intent = new Intent(MainActivity.this, Login_activity.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.nav_allowEditSett) {
            hideFloatingActionButton();
            setCustomActionbar("Pengaturan Ubah Harga");
            fragment = new EditPrice();
        } else if (id == R.id.absen) {
            hideFloatingActionButton();
            setCustomActionbar("Absensi");
            fragment = new Absen_fragment();
        }
        if (fragment != null) {
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("MyData"));
        LocalBroadcastManager.getInstance(this).registerReceiver((openNotificationEvent), new IntentFilter("NotifOpen"));
    }

    public void updateNotificationsBadge(int count) {
        mNotificationsCount = count;
        this.invalidateOptionsMenu();
    }

    private void getCountNotification() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlactive + "Welcomebaru/getCountNotif/" + sess_outlet,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            updateNotificationsBadge(data.getInt("totaldata"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, " Failed to fetch Notification ", Toast.LENGTH_SHORT).show();
            }
        }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }
}