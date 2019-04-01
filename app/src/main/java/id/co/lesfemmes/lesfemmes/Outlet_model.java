package id.co.lesfemmes.lesfemmes;

public class Outlet_model {
    String OutletCode;

    public Outlet_model(String OutletCode){
        this.OutletCode = OutletCode;
    }

    public String getOutletCode() {
        return OutletCode;
    }

    public void setOutletCode(String outletCode) {
        OutletCode = outletCode;
    }

    @Override
    public String toString() {
        return OutletCode;
    }
}
