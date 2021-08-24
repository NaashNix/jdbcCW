package controllers;

import db.DbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginFormController {
    public TextField txtLoginUserName;
    public PasswordField txtLoginPassword;
    public AnchorPane loginWindowContext;
    public static String setUserName;


    public void openRelatedDashboard(ActionEvent actionEvent) throws SQLException, ClassNotFoundException, IOException {

        String id = txtLoginUserName.getText();
        setUserName = txtLoginUserName.getText();

        PreparedStatement preparedStatement = DbConnection.getInstance().getConnection()
                .prepareStatement("SELECT * FROM Users WHERE id=?");
        preparedStatement.setObject(1,id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){

            switch (resultSet.getString("roll")) {
                case "admin": {
                    adminLoginChecker(resultSet);
                    break;
                }

                case "cashi": {
                    cashierLoginChecker(resultSet);
                    break;
                }

                case "mngr": {
                    managerLoginChecker();
                    break;
                }

                default: {
                    new Alert(Alert.AlertType.ERROR, "Invalid User roll");
                }
            }
            System.out.println("HEllo");
        }else{
            new Alert(Alert.AlertType.ERROR,"Invalid ID").show();
        }
    }

    private void managerLoginChecker() {
        System.out.println("manager");
    }

    private void cashierLoginChecker(ResultSet resultSet) throws IOException, SQLException {

        if (txtLoginPassword.getText().equals(resultSet.getString("password"))){
            URL resource = getClass().getResource("../view/CashierDashboardForm.fxml");
            Parent load = FXMLLoader.load(resource);
            //String css = this.getClass().getResource("../css/PlaceOrderStyle.css").toExternalForm();
            Scene scene = new Scene(load);
            //scene.getStylesheets().add(css);
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/CashierDashboardForm.fxml"));
            Stage window = (Stage)loginWindowContext.getScene().getWindow();
            window.setScene(scene);
            window.show();
            window.centerOnScreen();
        }else{
            new Alert(Alert.AlertType.ERROR,"Invalid Password").show();

        }
    }

    private void adminLoginChecker(ResultSet resultSet) throws SQLException, IOException {
        if(txtLoginPassword.getText().equals(resultSet.getString("password"))){
            URL resource = getClass().getResource("../view/SystemAdminDashboardFrom.fxml");
            Parent load = FXMLLoader.load(resource);
            String css = this.getClass().getResource("../css/AdminDashboardStyle.css").toExternalForm();
            Scene scene = new Scene(load);
            scene.getStylesheets().add(css);
            Stage stage = (Stage)loginWindowContext.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        }else{
            new Alert(Alert.AlertType.ERROR,"Invalid Password").show();
        }
    }
}
