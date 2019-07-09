package id.co.lesfemmes.lesfemmes.absen;

public class Absen_model {
    String Nama, Nip, Tanggal, JamMasuk, JamKeluar, Alamat, Long, Lat;

    public Absen_model(String nama, String nip, String tanggal, String jamMasuk, String jamKeluar, String alamat, String aLong, String lat) {
        this.Nama = nama;
        this.Nip = nip;
        this.Tanggal = tanggal;
        this.JamMasuk = jamMasuk;
        this.JamKeluar = jamKeluar;
        this.Alamat = alamat;
        this.Long = aLong;
        this.Lat = lat;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        Nama = nama;
    }


    public String getNip() {
        return Nip;
    }

    public void setNip(String nip) {
        Nip = nip;
    }

    public String getTanggal() {
        return Tanggal;
    }

    public void setTanggal(String tanggal) {
        Tanggal = tanggal;
    }

    public String getJamMasuk() {
        return JamMasuk;
    }

    public void setJamMasuk(String jamMasuk) {
        JamMasuk = jamMasuk;
    }

    public String getJamKeluar() {
        return JamKeluar;
    }

    public void setJamKeluar(String jamKeluar) {
        JamKeluar = jamKeluar;
    }

    public String getAlamat() {
        return Alamat;
    }

    public void setAlamat(String alamat) {
        Alamat = alamat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }
}
