package controllers;

import com.jfoenix.controls.JFXButton;
import com.sun.org.apache.xpath.internal.operations.Or;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Item;
import model.ItemDetails;

import java.sql.SQLException;
import java.util.Optional;

public class EditOrderDetailsController {

    public TextField txtReqAmount;
    public TextField txtDiscountAmount;
    public JFXButton CustomerEditDone;
    public JFXButton btnClear;
    public Label orderIDLabel;
    public Label itemCodeLabel;
    public Label lblEditedItemTotal;
    public Label txtQtyOnHand;
    public ItemDetails itemDetails;
    String orderIDGot;
    public void initialize() throws SQLException, ClassNotFoundException {
        setDataToFields();
    }

    private void setDataToFields() throws SQLException, ClassNotFoundException {
        itemDetails = ManageOrdersFormController.itemDetailsMNG;
        orderIDGot = ManageOrdersFormController.orderID;
        itemCodeLabel.setText(itemDetails.getItemID());
        orderIDLabel.setText(orderIDGot);
        txtQtyOnHand.setText(
                String.valueOf(itemDetails.getOrderQty()+(new ItemController().searchItem(itemDetails.getItemID()).getQtyOnHand())));
        if (itemDetails != null){
            txtReqAmount.setText(String.valueOf(itemDetails.getOrderQty()));
            txtDiscountAmount.setText(String.valueOf(itemDetails.getDiscount()));
        }

    }

    public void cancel(ActionEvent actionEvent) {

    }

    public void CustomerEditDone(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        boolean b = itemDetails.getOrderQty() < Integer.parseInt(txtReqAmount.getText());
        int newQty;
        if (b){
            newQty = Integer.parseInt(txtReqAmount.getText()) - itemDetails.getOrderQty();
        }else{
            newQty = itemDetails.getOrderQty()-Integer.parseInt(txtReqAmount.getText());
        }
        if (new OrderController().updateOrderDetailItem(new ItemDetails(
                itemDetails.getItemID(),
                Integer.parseInt(txtReqAmount.getText()),
                (txtDiscountAmount.getText().isEmpty()? 0.00 : Double.parseDouble(txtDiscountAmount.getText()) )
        ),
                orderIDLabel.getText(),
                itemDetails.getOrderQty() < Integer.parseInt(txtReqAmount.getText()),
                newQty
        )){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Done!");
            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK) {
                Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                stage.hide();
            }
        }else {
            new Alert(Alert.AlertType.WARNING,"Failed!").show();
        }
    }


    public void enableDoneButton(KeyEvent keyEvent) throws SQLException, ClassNotFoundException {
            double discountPresentage = 0;
            double total = 0;
        if (txtReqAmount.getText().isEmpty()){
            lblEditedItemTotal.setText(null);
            CustomerEditDone.setDisable(true);
        }else{
            discountPresentage = txtDiscountAmount.getText().isEmpty()? 0.00 : Double.parseDouble(txtDiscountAmount.getText());
            total = new ItemController().searchItem(itemDetails.getItemID()).getUnitPrice() * Integer.parseInt(txtReqAmount.getText());
            lblEditedItemTotal.setText(String.valueOf(total - total/100*discountPresentage));
            CustomerEditDone.setDisable(false);
        }


    }
}
