package model;

public class ItemDetails {
    private String itemID;
    private int orderQty;
    private double discount;

    public ItemDetails(String itemID, int orderQty, double discount) {
        this.itemID = itemID;
        this.orderQty = orderQty;
        this.discount = discount;
    }

    public ItemDetails() {
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    @Override
    public String toString() {
        return "ItemDetails{" +
                "itemID='" + itemID + '\'' +
                ", orderQty=" + orderQty +
                ", discount=" + discount +
                '}';
    }
}
