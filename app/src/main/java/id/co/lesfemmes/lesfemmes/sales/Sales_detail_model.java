package id.co.lesfemmes.lesfemmes.sales;

public class Sales_detail_model {
    String ItemCode,Harga,Disc,Total,Quantity,HargaafterDisc,TotalHarga,NoNota,IdHeader,Note,NoSKU;
    Integer Status;

    public Sales_detail_model(String IdHeader, String itemCode, String harga, String disc, String total,String Quantity,String HargaafterDisc,String TotalHarga,String NoNota,Integer Status,String Note,String NoSKU) {
        this.ItemCode = itemCode;
        this.Harga = harga;
        this.Disc = disc;
        this.Total = total;
        this.Quantity = Quantity;
        this.HargaafterDisc = HargaafterDisc;
        this.TotalHarga = TotalHarga;
        this.NoNota = NoNota;
        this.IdHeader = IdHeader;
        this.Status = Status;
        this.Note = Note;
        this.NoSKU = NoSKU;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getIdHeader() {
        return IdHeader;
    }

    public void setIdHeader(String idHeader) {
        IdHeader = idHeader;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getHarga() {
        return Harga;
    }

    public void setHarga(String harga) {
        Harga = harga;
    }

    public String getDisc() {
        return Disc;
    }

    public void setDisc(String disc) {
        Disc = disc;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getHargaafterDisc() {
        return HargaafterDisc;
    }

    public void setHargaafterDisc(String hargaafterDisc) {
        HargaafterDisc = hargaafterDisc;
    }

    public String getTotalHarga() {
        return TotalHarga;
    }

    public void setTotalHarga(String totalHarga) {
        TotalHarga = totalHarga;
    }

    public String getNoNota() {
        return NoNota;
    }

    public void setNoNota(String noNota) {
        NoNota = noNota;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getNoSKU() {
        return NoSKU;
    }

    public void setNoSKU(String noSKU) {
        NoSKU = noSKU;
    }
}
