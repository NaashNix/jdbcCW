package controllers;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Customer;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddCustomerFormController implements Initializable {

    public TextField txtCustomerName;
    public TextField txtCustomerAddress;
    public TextField txtCustomerCity;
    public Label lblCustomerID;
    public TextField txtPostalCode;
    public ComboBox cmbCrustTitle;
    public ComboBox cmbCustProvince;
    public JFXButton btnAddCustomer;
    public JFXButton btnClear;
    public AnchorPane AddCustomerContext;

    @FXML
    public void addCustomer(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {

        Customer customer = new Customer(
                lblCustomerID.getText(),
                txtCustomerName.getText(),
                cmbCrustTitle.getValue().toString(),
                txtCustomerAddress.getText(),
                txtCustomerCity.getText(),
                cmbCustProvince.getValue().toString(),
                txtPostalCode.getText()
        );
        if (new CustomerController().saveCustomer(customer)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Done!");
            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK) {
                    Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                    stage.hide();
            }else{
                clearForm(actionEvent);
            }
        }else{
            new Alert(Alert.AlertType.ERROR,"Failed!").show();
        }
        lblCustomerID.setText(new CustomerController().getLastCustomerID());

    }

    @FXML
    public void clearForm(ActionEvent actionEvent) {
        cmbCustProvince.getSelectionModel().clearSelection();
        cmbCrustTitle.getSelectionModel().clearSelection();
        txtCustomerCity.clear();
        txtCustomerName.clear();
        txtCustomerAddress.clear();
        txtPostalCode.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            lblCustomerID.setText(new CustomerController().getLastCustomerID());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        cmbCrustTitle.getItems().addAll("Top","Mid","New");
        cmbCustProvince.getItems().addAll(
                "Western Province",
                "Eastern Province",
                "Nothern Province",
                "South Province",
                "Uva Province",
                "Central Province",
                "Sabaragamu Province",
                "North Central Province",
                "North Westarn Province"
        );
    }
}
