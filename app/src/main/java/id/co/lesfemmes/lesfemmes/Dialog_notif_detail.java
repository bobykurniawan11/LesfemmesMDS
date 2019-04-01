package id.co.lesfemmes.lesfemmes;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Dialog_notif_detail extends DialogFragment {
    String NotificationTitle,NotificationMessage,urlactive;
    ImageView deleteIcon;
    Integer NotificationId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_notification, container, false);
        TextView NotificationTitle_tv = v.findViewById(R.id.NotificationTitle);
        TextView NotificationMessage_tv = v.findViewById(R.id.NotificationMessage);
        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();

        NotificationTitle           = getArguments().getString("NotificationTitle");
        NotificationMessage         = getArguments().getString("NotificationMessage");
        NotificationId              = getArguments().getInt("NotificationId");

        NotificationTitle_tv.setText(NotificationTitle);
        NotificationMessage_tv.setText(NotificationMessage);
        updateStatus();
        return v;
    }
    private void updateStatus(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, urlactive+"/welcome/updateNotificationStatus/",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

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
                params.put("NotificationId", String.valueOf(NotificationId));
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}