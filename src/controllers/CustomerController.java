package controllers;

import db.DbConnection;
import model.Customer;
import model.Orders;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerController {
    public Customer searchCustomer(String customerID) throws SQLException, ClassNotFoundException {
        PreparedStatement statement = DbConnection.getInstance().getConnection()
                .prepareStatement("SELECT*FROM Customer WHERE custID=?");
        statement.setObject(1,customerID);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()){

            return new Customer(
                    resultSet.getString("custID"),
                    resultSet.getString("custName"),
                    resultSet.getString("custTitle"),
                    resultSet.getString("custAddress"),
                    resultSet.getString("city"),
                    resultSet.getString("province"),
                    resultSet.getString("postalCode")
            );
        }else {
            return null;
        }

    }
    public int lastIDint;
    public String getLastCustomerID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DbConnection.getInstance().getConnection()
                .prepareStatement("SELECT*FROM Customer ORDER BY CustID DESC LIMIT 1").executeQuery();

        if (resultSet.next()){
            lastIDint = (Integer.parseInt(resultSet.getString(1).substring(2)));
            return String.format("C-%03d",++lastIDint);
        }else{
            return "C-001";
        }
    }

    public List<String> getCustomerID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT*FROM Customer").executeQuery();

        List<String> custIDs = new ArrayList<>();
        while(resultSet.next()){
            custIDs.add(resultSet.getString("custID"));
        }
        return custIDs;
    }


    public boolean saveCustomer(Customer customer) throws SQLException, ClassNotFoundException {

        Connection con = DbConnection.getInstance().getConnection();
        String query = "INSERT INTO Customer VALUES(?,?,?,?,?,?,?)";
        PreparedStatement stm = con.prepareStatement(query);
        stm.setObject(1,customer.getCustomerID());
        stm.setObject(2,customer.getCustomerTitle());
        stm.setObject(3,customer.getCustomerName());
        stm.setObject(4,customer.getCustomerAddress());
        stm.setObject(5,customer.getCustomerCity());
        stm.setObject(6,customer.getCustomerProvince());
        stm.setObject(7,customer.getCustomerPostalCode());
        return stm.executeUpdate()>0;
    }


}
