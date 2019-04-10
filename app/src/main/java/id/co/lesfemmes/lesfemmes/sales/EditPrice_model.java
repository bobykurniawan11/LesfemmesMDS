package id.co.lesfemmes.lesfemmes.sales;

public class EditPrice_model {

    String CustomerCode, TransactionDate;

    public EditPrice_model(String CustomerCode, String TransactionDate) {
        this.CustomerCode = CustomerCode;
        this.TransactionDate = TransactionDate;
    }

    public String getCustomerCode() {
        return CustomerCode;
    }

    public void setCustomerCode(String customerCode) {
        CustomerCode = customerCode;
    }

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }
}
