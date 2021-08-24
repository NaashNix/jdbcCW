package tm;

public class ItemCartTM {
    private String itemCode;
    private String itemDesc;
    private double unitPrice;
    private int reqAmount;
    private double total;
    private double discount;

    public ItemCartTM() {
    }

    public ItemCartTM(String itemCode, String itemDesc,
                      double unitPrice, int reqAmount, double total
                        ,double discount
                      ) {
        this.itemCode = itemCode;
        this.itemDesc = itemDesc;
        this.unitPrice = unitPrice;
        this.reqAmount = reqAmount;
        this.total = total;
        this.setDiscount(discount);
    }

    @Override
    public String toString() {
        return "ItemCartTM{" +
                "itemCode='" + itemCode + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", unitPrice=" + unitPrice +
                ", reqAmount=" + reqAmount +
                ", total=" + total +
                ", discount=" + getDiscount() +
                '}';
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getReqAmount() {
        return reqAmount;
    }

    public void setReqAmount(int reqAmount) {
        this.reqAmount = reqAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
