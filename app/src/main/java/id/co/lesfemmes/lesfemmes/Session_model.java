package id.co.lesfemmes.lesfemmes;

import android.content.Context;
import android.content.SharedPreferences;

public class Session_model {
    String username;
    String fullname;
    String outlet;
    String kodeposisi;
    String namaposisi;

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    String imagepath;
    private SharedPreferences sharedPreferences;

    public Session_model(Context ctx){
        SharedPreferences loginPreferences = ctx.getSharedPreferences(Login_activity.MyPREFERENCES, Context.MODE_PRIVATE);
        username    = loginPreferences.getString("username_session","");
        fullname    = loginPreferences.getString("fullname_session","");
        outlet      = loginPreferences.getString("outlet_session","");
        namaposisi  = loginPreferences.getString("namaposisi_session","");
        namaposisi  = loginPreferences.getString("namaposisi_session","");
        imagepath   = loginPreferences.getString("image_session","");
    }
    public String getProfilImage() {
        return imagepath;
    }

    public String getUsername() {
        return username;
    }
    public String getFullname() {
        return fullname;
    }
    public String getOutlet() {
        return outlet;
    }
    public String getKodeposisi() {
        return kodeposisi;
    }
    public String getNamaposisi() {
        return namaposisi;
    }


}
