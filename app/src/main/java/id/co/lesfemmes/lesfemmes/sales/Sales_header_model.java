package id.co.lesfemmes.lesfemmes.sales;



public class Sales_header_model {

    String OutletCode, TanggalTransaksi, CreatedBy, Totalharga;
    String CreatedDate;
    Integer Status,StatusPosting;

    public Sales_header_model() {

    }

    public Sales_header_model(String outletCode, String tanggalTransaksi, String CreatedDate, Integer Status, String Totalharga, Integer StatusPosting) {
        this.OutletCode = outletCode;
        this.TanggalTransaksi = tanggalTransaksi;
        this.Status = Status;
        this.CreatedDate = CreatedDate;
        this.Totalharga = Totalharga;
        this.StatusPosting = StatusPosting;
    }

    public Integer getStatusPosting() {
        return StatusPosting;
    }

    public void setStatusPosting(Integer statusPosting) {
        StatusPosting = statusPosting;
    }

    public String getOutletCode() {
        return OutletCode;
    }

    public void setOutletCode(String outletCode) {
        OutletCode = outletCode;
    }

    public String getTanggalTransaksi() {
        return TanggalTransaksi;
    }

    public void setTanggalTransaksi(String tanggalTransaksi) {
        TanggalTransaksi = tanggalTransaksi;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getTotalharga() {
        return Totalharga;
    }

    public void setTotalharga(String totalharga) {
        Totalharga = totalharga;
    }
}
