package id.co.lesfemmes.lesfemmes.absen;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import id.co.lesfemmes.lesfemmes.MainActivity;
import id.co.lesfemmes.lesfemmes.R;

public class Dialog_absen_detail extends DialogFragment {
    String NotificationTitle, NotificationMessage, urlactive;
    ImageView deleteIcon;
    Integer NotificationId;
    String Nama, Nip, Tanggal, JamMasuk, JamKeluar, Alamat, Long, Lat;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.absen_detail_dialog, container, false);

        MainActivity ma = new MainActivity();
        urlactive = ma.geturlactive();

        Nama = getArguments().getString("Nama");
        Nip = getArguments().getString("Nip");
        Tanggal = getArguments().getString("Tanggal");
        Long = getArguments().getString("Long");
        Lat = getArguments().getString("Lat");
        Alamat = getArguments().getString("Alamat");
        JamMasuk = getArguments().getString("JamMasuk");
        JamKeluar = getArguments().getString("JamKeluar");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd-mm-yyyy");
        String inputDateStr = Tanggal;
        Date date = null;
        try {
            date = inputFormat.parse(inputDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Tanggal = outputFormat.format(date);

        TextView nik = v.findViewById(R.id.nik);
        TextView nama = v.findViewById(R.id.nama);
        TextView alamat = v.findViewById(R.id.alamat);
        TextView jamMask = v.findViewById(R.id.jamMask);
        TextView jamKlr = v.findViewById(R.id.jamKlr);
        TextView tanggal = v.findViewById(R.id.tanggal);

        nik.setText(Nip);
        nama.setText(Nama);
        alamat.setText(Alamat);
        jamMask.setText("Jam Masuk : " + JamMasuk);
        jamKlr.setText("Jam Masuk : " + JamKeluar);
        tanggal.setText("Tanggal : " + Tanggal);

        return v;
    }

}