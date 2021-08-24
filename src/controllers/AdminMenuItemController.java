package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Item;
import tm.ItemTM;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminMenuItemController {

    public TableView<ItemTM> tblItemDetails;
    public TextField txtItemCode;
    public TextField txtQtyOnHand;
    public TextField txtPackSize;
    public TextField txtUnitPrice;
    public JFXButton btnAddItem;
    public JFXButton btnClear;
    public TextField txtDesc;
    public TableColumn colCode;
    public TableColumn colDesc;
    public TableColumn colQtyOnHand;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public AnchorPane addItemContext;
    public JFXButton btnEdit;
    public JFXButton btnDelete;
    public int selectedRow = -1;
    public static String selectedItemID;

    public void tableRowSelectedOrNot(MouseEvent mouseEvent){
        if (selectedRow == -1){
            btnDelete.setDisable(true);
            btnEdit.setDisable(true);
        }else{
            btnEdit.setDisable(false);
            btnDelete.setDisable(false);
        }
    }

    public void addItemToDB(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Item item = new Item(
                txtItemCode.getText(),
                txtDesc.getText(),
                txtPackSize.getText(),
                Double.parseDouble(txtUnitPrice.getText()),
                Integer.parseInt(txtQtyOnHand.getText())
        );
        if (new ItemController().saveItem(item)){
            initialize();
            new Alert(Alert.AlertType.CONFIRMATION,"Done!").show();
            loadItemDetails();
            clearAddItemForm(actionEvent);
            btnAddItem.setDisable(true);
        }else{
            System.out.println("FAILED.");
        }
    }

    ObservableList<ItemTM> itemTMS = FXCollections.observableArrayList();

    public void loadItemDetails() throws SQLException, ClassNotFoundException {

        tblItemDetails.getItems().clear();
        ArrayList<Item> itemList = new ItemController().getAllItems();
        itemTMS.clear();
        for (Item item : itemList
             ) {
            itemTMS.add(new ItemTM(
                    item.getItemCode(),
                    item.getDesc(),
                    item.getQtyOnHand(),
                    item.getPackSize(),
                    item.getUnitPrice()
                    ));

        }

        tblItemDetails.setItems(itemTMS);

    }

    public void clearAddItemForm(ActionEvent actionEvent) {
        txtDesc.setText(null);
        txtPackSize.setText(null);
        txtUnitPrice.setText(null);
        txtQtyOnHand.setText(null);
    }

    public void BackToLoginWindo(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/loginForm.fxml");
        Parent load = null;
        if (resource != null) {
            load = FXMLLoader.load(resource);
        }
        Stage window = (Stage) addItemContext.getScene().getWindow();
        window.setScene(new Scene(Objects.requireNonNull(load)));
        window.show();
        window.centerOnScreen();
    }

    public void deleteItem(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        if (selectedRow == -1){
            new Alert(Alert.AlertType.WARNING,"ERROR --> selectedRow(-1)").show();
        }else{
            if (new ItemController().deleteItem(itemTMS.get(selectedRow).getItemCode())){
                itemTMS.remove(selectedRow);
                tblItemDetails.refresh();
            }
        }
    }

    public void enableAddItem(KeyEvent keyEvent) {
        if((!txtDesc.getText().isEmpty())&&(!txtPackSize.getText().isEmpty())
            &&(!txtQtyOnHand.getText().isEmpty()) && (!txtUnitPrice.getText().isEmpty())){
                btnAddItem.setDisable(false);
            }else{
                btnAddItem.setDisable(true);
            }
        }

    public void editItemDetails(MouseEvent mouseEvent) throws IOException {
        System.out.println(selectedRow);
        EventHandler<MouseEvent> handler = MouseEvent::consume;
        selectedItemID = itemTMS.get(selectedRow).getItemCode();
        Stage stage;
        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/EditItemDetailsForm.fxml")));
        Scene scene = new Scene(load);
        stage = new Stage();
        stage.setScene(scene);
        btnEdit.setDisable(true);
        btnDelete.addEventFilter(MouseEvent.ANY,handler);
        btnEdit.addEventFilter(MouseEvent.ANY,handler);
        btnDelete.setDisable(true);
        addItemContext.setDisable(true);

        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                btnEdit.removeEventFilter(MouseEvent.ANY,handler);
                btnDelete.removeEventFilter(MouseEvent.ANY,handler);
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
                addItemContext.setDisable(false);
                initialize();
            }
        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                btnEdit.removeEventFilter(MouseEvent.ANY,handler);
                btnDelete.removeEventFilter(MouseEvent.ANY,handler);
                btnDelete.setDisable(false);
                btnEdit.setDisable(false);
                addItemContext.setDisable(false);
                initialize();

            }
        });
        stage.show();

    }
    public void initialize() {
        tblItemDetails.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    selectedRow = newValue.intValue();
                });

        colCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        try {
            txtItemCode.setText(new ItemController().getLastItemID());
            loadItemDetails();
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}



