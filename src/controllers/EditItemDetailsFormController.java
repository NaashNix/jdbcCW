package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Item;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Optional;

public class EditItemDetailsFormController {

    public Image imageRestore;
    public ImageView restoreImageView;
    public Label lblItemID;
    public TextField txtItemDesc;
    public TextField txtQtyOnHand;
    public TextField txtUnitPrice;
    public JFXButton btnClear;
    public TextField txtPackSize;
    public JFXButton CustomerEditDone;

    public void initialize() throws SQLException, ClassNotFoundException {
            setDataToFields();
    }

    private void setDataToFields() throws SQLException, ClassNotFoundException {
        Item item = new ItemController().searchItem(AdminMenuItemController.selectedItemID);
        lblItemID.setText(item.getItemCode());
        txtItemDesc.setText(item.getDesc());
        txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
        txtPackSize.setText(item.getPackSize());
    }

    public void restoreMouseEN(MouseEvent mouseEvent) throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/assets/undo.png"));
        restoreImageView.setImage(image);

    }

    public void restoreMouseEX(MouseEvent mouseEvent) throws FileNotFoundException {
        Image image = new Image(new FileInputStream("src/assets/undoNormal.png"));
        restoreImageView.setImage(image);
    }

    public void cancel(ActionEvent actionEvent) {
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        stage.hide();
    }

    public void CustomerEditDone(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(new ItemController().updateItem(new Item(
                lblItemID.getText(),
                txtItemDesc.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        ))){
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Done!");
            Optional<ButtonType> result = alert.showAndWait();
            ButtonType button = result.orElse(ButtonType.CANCEL);
            if (button == ButtonType.OK) {
                Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
                stage.hide();
            }
        }

    }

    public void restorePrevious(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        setDataToFields();
    }
}
