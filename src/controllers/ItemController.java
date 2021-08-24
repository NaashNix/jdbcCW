package controllers;

import db.DbConnection;
import model.Item;
import model.ItemDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemController{
    public boolean deleteItem(String itemID) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DbConnection.getInstance().getConnection()
                .prepareStatement("DELETE FROM Item WHERE itemID=?");
        statement.setObject(1,itemID);
        return statement.executeUpdate()>0;
    }
    public Item searchItem(String itemID) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DbConnection.getInstance().getConnection()
                .prepareStatement("SELECT*FROM Item WHERE itemID=?");
        statement.setObject(1,itemID);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()){

            return new Item(
                    resultSet.getString("itemID"),
                    resultSet.getString("description"),
                    resultSet.getString("packSize"),
                    resultSet.getDouble("unitPrice"),
                    resultSet.getInt("qtyOnHand")
            );
        }else {
            return null;
        }
    }


    public List<String> getItemIDs() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT*FROM Item").executeQuery();

        List<String> itemIDs = new ArrayList<>();
        while(resultSet.next()){
            itemIDs.add(resultSet.getString("itemID"));
        }
        return itemIDs;
    }

    public ArrayList<Item> getAllItems() throws SQLException, ClassNotFoundException {
        ArrayList<Item> itemArrayList = new ArrayList<>();
        ResultSet resultSet = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT*FROM Item").executeQuery();

        while (resultSet.next()){
            itemArrayList.add(new Item(
                    resultSet.getString("itemID"),
                    resultSet.getString("description"),
                    resultSet.getString("packSize"),
                    resultSet.getDouble("unitPrice"),
                    resultSet.getInt("qtyOnHand")
            ));
        }
        return itemArrayList;
    }
    public boolean updateItem(Item item) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance().getConnection().
                prepareStatement("UPDATE Item SET description=?,packSize=?,unitPrice=?,qtyOnHand=? WHERE itemID=?");
        stm.setObject(1,item.getDesc());
        stm.setObject(2,item.getPackSize());
        stm.setObject(3,item.getUnitPrice());
        stm.setObject(4,item.getQtyOnHand());
        stm.setObject(5,item.getItemCode());

        return stm.executeUpdate()>0;

    }

    public boolean saveItem(Item item) throws SQLException, ClassNotFoundException {
        Connection con = DbConnection.getInstance().getConnection();
        String query = "INSERT INTO Item VALUES(?,?,?,?,?)";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setObject(1,item.getItemCode());
        statement.setObject(2,item.getDesc());
        statement.setObject(3,item.getPackSize());
        statement.setObject(4,item.getUnitPrice());
        statement.setObject(5,item.getQtyOnHand());
        return statement.executeUpdate()>0;
    }

    public String getLastItemID() throws SQLException, ClassNotFoundException {
        int lastIDint;
        ResultSet resultSet = DbConnection.getInstance().getConnection()
                .prepareStatement("SELECT*FROM Item ORDER BY itemID DESC LIMIT 1")
                .executeQuery();
        if(resultSet.next()){
            lastIDint = (Integer.parseInt(resultSet.getString(1).substring(2)));
            return String.format("I-%03d",++lastIDint);
        }else{
            return "I-001";
        }
    }


}
