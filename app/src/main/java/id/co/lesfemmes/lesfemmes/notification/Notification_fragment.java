package id.co.lesfemmes.lesfemmes.notification;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.lesfemmes.lesfemmes.Dialog_notif_detail;
import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;
import id.co.lesfemmes.lesfemmes.Session_model;

public class Notification_fragment extends Fragment {
    private List<Notification_model> notificationList;
    private Notification_adapter mAdapter;
    RecyclerView recyclerView;
    Session_model sess_model;
    MainActivity ma;
    String urlactive;
    SearchView searchView;
    RadioGroup isApproved;
    RadioButton choosed;
    String selectedStatusNotif;

    View v;
    @Override
    public void onResume() {
        super.onResume();
        MainActivity mYourActiviy = (MainActivity) getActivity();
        mYourActiviy.setCustomActionbar("Notification");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_with_search, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Kode Outlet");
        search(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ma = new MainActivity();
        urlactive = ma.geturlactive();
        v= inflater.inflate(R.layout.fragment_notification, container, false);
        selectedStatusNotif = "";

        notificationList = new ArrayList<>();
        mAdapter = new Notification_adapter(notificationList,getContext());
        recyclerView = v.findViewById(R.id.rc_list);
        sess_model = new Session_model(getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(v.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        getNotification();
        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new Notification_adapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Bundle bundle=new Bundle();

                LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getActivity());
                Intent intent = new Intent("NotifOpen");
                broadcaster.sendBroadcast(intent);

                bundle.putString("NotificationTitle", notificationList.get(position).getNotificationTitle());
                bundle.putString("NotificationMessage", notificationList.get(position).getNotificationMessage());
                bundle.putInt("NotificationId", notificationList.get(position).getNotificationId());

                FragmentManager ft = getActivity().getFragmentManager();
                Dialog_notif_detail dialogFragment = new Dialog_notif_detail();

                dialogFragment.setArguments(bundle);
                dialogFragment.show(ft, "dialog");
            }
            @Override
            public void onItemLongClick(int position, View v) {

            }
        });

        isApproved = v.findViewById(R.id.isApproved);
        isApproved.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                choosed = v.findViewById(checkedId);
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.clearAnimation();
                switch(checkedId){
                    case R.id.all:
                        selectedStatusNotif = "";
                        getNotification();
                        break;
                    case R.id.approved:
                        selectedStatusNotif = "2";
                        getNotification();
                        break;
                    case R.id.notapproved:
                        selectedStatusNotif = "1";
                        getNotification();
                    break;
                }
            }
        });


        return v;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void getNotification(){
        notificationList.clear();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"/Welcomebaru/getNotificationNew/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObjd         = new JSONObject(response);
                            JSONArray arrayDetail       = jsonObjd.getJSONArray("Notification");
                            for(int i = 0; i < arrayDetail.length();i++){
                                JSONObject detail = arrayDetail.getJSONObject(i);
                                Notification_model sdn = new Notification_model(
                                        detail.getString("CustomerCode"),
                                        detail.getString("NotificationDate"),
                                        detail.getString("NotificationTitle"),
                                        detail.getString("NotificationMessage"),
                                        detail.getInt("NotificationStatus"),
                                        detail.getInt("NotificationId"),
                                        detail.getInt("Type")
                                );
                                notificationList.add(sdn);
                            }
                            mAdapter = new Notification_adapter(notificationList,getContext());
                            recyclerView.setAdapter(mAdapter);
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("OutletCode", sess_model.getOutlet());
                params.put("UserLogin", sess_model.getUsername());
                params.put("selectedStatusNotif",selectedStatusNotif);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}
