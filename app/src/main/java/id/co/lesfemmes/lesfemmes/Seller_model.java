package id.co.lesfemmes.lesfemmes;


public class Seller_model {

    String Nip,fullname;
    public Seller_model(String Nip,String fullname){
        this.Nip = Nip;
        this.fullname = fullname;
    }

    public String getNip() {
        return Nip;
    }

    public void setNip(String nip) {
        this.Nip = nip;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @Override
    public String toString() {
        return this.Nip.toString();
    }
}
