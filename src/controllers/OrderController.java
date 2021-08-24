package controllers;

import db.DbConnection;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class OrderController {

    public int lastIDint;

    public String getOrderID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT*FROM Orders ORDER BY orderID DESC LIMIT 1")
                .executeQuery();

        if (resultSet.next()){
            lastIDint = (Integer.parseInt(resultSet.getString(1).substring(2)));
            return String.format("O-%03d",++lastIDint);

        }else{
            return "O-001";
        }

    }

    public boolean placeOrder(Orders orders) throws SQLException {
        Connection con = null;
        try {
            con = DbConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            PreparedStatement stm = con.prepareStatement("INSERT INTO Orders VALUES(?,?,?)");
            stm.setObject(1,orders.getOrderID());
            stm.setObject(2,orders.getOrderDate());
            stm.setObject(3,orders.getCustID());

            if (stm.executeUpdate()>0){
                if (saveOrderDetails(orders.getOrderID(), orders.getItems())){
                    con.commit();
                    return true;
                }else{
                    con.rollback();
                    return false;
                }
            }else{
                con.rollback();
                return false;
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            con.setAutoCommit(true);
        }
        return false;
    }

    public boolean saveOrderDetails(String orderID, ArrayList<ItemDetails> items){
        for (ItemDetails itemDetails : items){
            try {
                Connection con = DbConnection.getInstance().getConnection();
                PreparedStatement stm =
                        con.prepareStatement("INSERT INTO orderDetail VALUES(?,?,?,?)");
                stm.setObject(1,orderID);
                stm.setObject(2,itemDetails.getItemID());
                stm.setObject(3,itemDetails.getOrderQty());
                stm.setObject(4,itemDetails.getDiscount());

                if (stm.executeUpdate() > 0){
                    if (updateQty(itemDetails.getItemID(),itemDetails.getOrderQty())){

                    }else{
                        return false;
                    }
                }else{
                    return false;
                }


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return true;
    }

    private boolean updateQty(String itemCode, int qty) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance().getConnection()
                .prepareStatement
                        ("UPDATE Item SET qtyOnHand=(qtyOnHand-" + qty
                                + ") WHERE itemID='" + itemCode + "'");
        return stm.executeUpdate()>0;
    }

    public ArrayList<OrderItemDetails> getItemsForManage(String orderID) throws SQLException, ClassNotFoundException {
        System.out.println(orderID);
        ArrayList<OrderItemDetails> orderItemDetails = new ArrayList<>();
        PreparedStatement statement = DbConnection.getInstance()
                .getConnection().prepareStatement("SELECT*FROM orderDetail WHERE orderID=?");
        statement.setObject(1,orderID);
        ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Item item = new ItemController().searchItem(resultSet.getString("itemID"));
                orderItemDetails.add(new OrderItemDetails(
                        item.getItemCode(),
                        item.getDesc(),
                        resultSet.getInt("orderQty"),
                        item.getUnitPrice(),
                        resultSet.getDouble("discount")
                ));
            }
        return orderItemDetails;
    }



    public ArrayList<Orders> searchOrders(String custID) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(
                "SELECT * FROM Orders WHERE CustID=?"
        );
        stm.setObject(1,custID);
        ResultSet resultSet = stm.executeQuery();

        ArrayList<Orders> ordersArrayList = new ArrayList<>();
        while (resultSet.next()){
            ordersArrayList.add(new Orders(
                    resultSet.getString("orderID"),
                    resultSet.getString("orderDate"),
                    resultSet.getString("CustID"),
                    getOrderDetails(resultSet.getString("orderID"))
            ));
        }

        return ordersArrayList;
    }

    public boolean deleteOrder(String orderID) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DbConnection.getInstance().getConnection()
                .prepareStatement("DELETE FROM Orders WHERE orderID=?");
        statement.setObject(1,orderID);

        return statement.executeUpdate()>0;


        /*if (statement.executeUpdate()>0){
            PreparedStatement stm = DbConnection.getInstance().getConnection()
                    .prepareStatement("DELETE FROM orderDetail WHERE orderID=?");
            stm.setObject(1,orderID);
            return statement.executeUpdate()>0;
        }else{
            return false;
        }
*/    }


    public ArrayList<ItemDetails> getOrderDetails(String orderID) throws SQLException, ClassNotFoundException {

        ArrayList<ItemDetails> itemDetails = new ArrayList<>();

        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement(
                "SELECT * FROM orderDetail WHERE orderID=?"
        );
        stm.setObject(1,orderID);
        ResultSet resultSet = stm.executeQuery();

        while (resultSet.next()){
            itemDetails.add(new ItemDetails(
                    resultSet.getString("itemID"),
                    resultSet.getInt("orderQty"),
                    resultSet.getDouble("discount")
            ));
        }
        return itemDetails;
    }

    public boolean deleteOrderedItem(String orderID,String itemID) throws SQLException, ClassNotFoundException {
        PreparedStatement statement =DbConnection.getInstance().getConnection()
                .prepareStatement("DELETE FROM orderDetail WHERE orderID=? AND itemID=?");
        statement.setObject(1,orderID);
        statement.setObject(2,itemID);

        if (statement.executeUpdate()>0){
            return true;
        }else{
            return false;
        }
    }

    public Orders searchOrder(String orderID) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DbConnection.getInstance().getConnection()
                .prepareStatement("SELECT*FROM Orders WHERE orderID=?");
        statement.setObject(1,orderID);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()){
            return new Orders(
                    resultSet.getString("orderID"),
                    resultSet.getString("orderDate"),
                    resultSet.getString("CustID"),
                    getOrderDetails(resultSet.getString("orderID"))
            );
        }else {
            return null;
        }
    }



    public boolean updateOrderDetailItem(ItemDetails itemDetails, String orderID, boolean addOrMinus,int changedQty) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DbConnection.getInstance().getConnection()
                .prepareStatement("UPDATE orderDetail SET orderQty=?,discount=? WHERE itemID=? AND orderID=?");
        statement.setObject(1,itemDetails.getOrderQty());
        statement.setObject(2,itemDetails.getDiscount());
        statement.setObject(3,itemDetails.getItemID());
        statement.setObject(4,orderID);

        if (statement.executeUpdate()>0){
            System.out.println("DONE! @updateOrderDetail");
            return updateItemtable(itemDetails.getItemID(),changedQty,addOrMinus);
        }else{
            System.out.println("FAIL! @updateOrderDetail");
            return false;
        }
    }

    private boolean updateItemtable(String itemID, int orderQty, boolean addOrMinus) throws SQLException, ClassNotFoundException {
        String query = null;
        if (addOrMinus){
             query = ("UPDATE Item SET qtyOnHand=(qtyOnHand-"+orderQty+") WHERE itemID=?");
        }else{
            query =  ("UPDATE Item SET qtyOnHand=(qtyOnHand+"+orderQty+") WHERE itemID=?");
        }

        Connection con = DbConnection.getInstance().getConnection();
        PreparedStatement statement = con.prepareStatement(query);
        statement.setObject(1,itemID);

        return statement.executeUpdate()>0;

    }

}
