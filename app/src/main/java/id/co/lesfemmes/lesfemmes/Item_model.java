package id.co.lesfemmes.lesfemmes;

public class Item_model  {

    String ItemCode;
    String ItemPrice;
    String ItemDisc;
    String ItemName;


    public String getItemCode() {
        return ItemCode;
    }

    public String getItemPrice() {
        return ItemPrice;
    }

    public String getItemDisc() {
        return ItemDisc;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public void setItemDisc(String itemDisc) {
        ItemDisc = itemDisc;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    @Override
    public String toString() {
        return  ItemCode;
    }
}
