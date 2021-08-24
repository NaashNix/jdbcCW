package model;

import java.util.ArrayList;

public class Orders {
    private String orderID;
    private String orderDate;
    private String custID;
    private ArrayList<ItemDetails> items;

    public Orders(String orderID, String orderDate, String custID, ArrayList<ItemDetails> items) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.custID = custID;
        this.items = items;
    }

    public Orders() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public ArrayList<ItemDetails> getItems() {
        return items;
    }

    public void setItems(ArrayList<ItemDetails> items) {
        this.items = items;
    }
}
