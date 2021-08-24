package model;

public class OrderItemDetails {
    private String itemCode;
    private String desc;
    private int orderQty;
    private double unitPrice;
    private double discount;

    public OrderItemDetails() {
    }

    @Override
    public String toString() {
        return "OrderItemDetails{" +
                "itemCode='" + itemCode + '\'' +
                ", desc='" + desc + '\'' +
                ", orderQty=" + orderQty +
                ", unitPrice=" + unitPrice +
                ", total=" + discount +
                '}';
    }

    public OrderItemDetails(String itemCode, String desc, int orderQty,
                            double unitPrice, double discount) {
        this.itemCode = itemCode;
        this.desc = desc;
        this.orderQty = orderQty;
        this.unitPrice = unitPrice;
        this.discount = discount;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
