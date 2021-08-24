package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.ItemDetails;
import model.OrderItemDetails;
import model.Orders;
import tm.ItemManageTM;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ManageOrdersFormController {

    public VBox vBoxOrderButtons;
    public JFXComboBox<String> cmbCustomerID;
    public Label txtCustomerName;
    public Label txtOrderID;
    public JFXButton previousButton = null;
    public AnchorPane manageOrdersContext;
    public TableView<ItemManageTM> tblItemDetails;
    public TableColumn colItemID;
    public TableColumn colDesc;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colTotal;
    public TableColumn colDiscount;
    public static int selectedRow = -1;
    public Label txtGrandTotal;
    public Label txtDiscountedAmount;
    public DecimalFormat df=new DecimalFormat("#.##");
    public JFXButton btnRemove;
    public JFXButton btnEdit;
    public Rectangle SQBackToLogin;
    public ImageView imgBackToLogin;
    public JFXButton btnDeleteOrder;
    public static String selectedItemID;
    public static String orderID;
    public static ItemDetails itemDetailsMNG;
    public JFXButton btnDone;
    public Orders preiousOrder;

    public void initialize() throws SQLException, ClassNotFoundException {

        colItemID.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        loadCustomerID();
        cmbCustomerID.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        clearBoard();
                        txtCustomerName.setText(new CustomerController().searchCustomer(newValue).getCustomerName());
                        selectedCustomer(newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );

        tblItemDetails.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    selectedRow = newValue.intValue();
                    if (selectedRow != -1){
                        btnEdit.setDisable(false);
                        btnRemove.setDisable(false);
                    }else{
                        btnRemove.setDisable(true);
                        btnEdit.setDisable(true);
                    }
                }
        );
    }

    private void clearBoard() {
        txtOrderID.setText(null);
        txtGrandTotal.setText(null);
        txtOrderID.setText(null);
        txtDiscountedAmount.setText(null);
        itemManageTMS.clear();
        tblItemDetails.refresh();

    }


    private JFXButton createOrderNumberButon(Orders order){
        JFXButton button = new JFXButton(order.getOrderID());
        button.setStyle("-fx-background-color : #bdc3c7;");
        button.setPrefSize(102.0,24.0);
        button.setOnAction(event -> {
            try {
                if (previousButton == null){
                }else{
                    previousButton.setStyle("-fx-background-color : #bdc3c7;");
                }

                button.setStyle("-fx-background-color : #2980b9;");
                btnDeleteOrder.setDisable(false);
                setDataToBoard(order);
                preiousOrder = new Orders(
                        txtOrderID.getText(),
                        order.getOrderDate(),
                        order.getCustID(),
                        order.getItems()
                );
                previousButton = button;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return button;
    }

    private void setDataToBoard(Orders order) throws SQLException, ClassNotFoundException {

        txtOrderID.setText(order.getOrderID());
        setDatatoTable(order);
        calculateGrandTotal();
    }

    private void calculateGrandTotal() {
        double total = 0.00;
        double discount = 0.00;
        for (ItemManageTM tempItem : itemManageTMS
             ) {
            total+=tempItem.getTotal();
            discount+=tempItem.getTotal()/100*tempItem.getDiscount();
        }

        try {
            txtDiscountedAmount.setText(df.format(discount));
            txtGrandTotal.setText(df.format(total));
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    ObservableList<ItemManageTM> itemManageTMS = FXCollections.observableArrayList();

    private void setDatatoTable(Orders order) throws SQLException, ClassNotFoundException {
        ArrayList<OrderItemDetails> orderItemDetails =
                new OrderController().getItemsForManage(order.getOrderID());

        itemManageTMS.clear();
        for (OrderItemDetails tempOrderItem : orderItemDetails
             ) {
            itemManageTMS.add(new ItemManageTM(
                    tempOrderItem.getItemCode(),
                    tempOrderItem.getDesc(),
                    tempOrderItem.getOrderQty(),
                    tempOrderItem.getUnitPrice(),
                    tempOrderItem.getDiscount(),
                    Double.parseDouble(df.format(
                            (tempOrderItem.getOrderQty()*tempOrderItem.getUnitPrice())-(tempOrderItem.getOrderQty()*
                                    tempOrderItem.getUnitPrice())/100*tempOrderItem.getDiscount()
                    ))

            ));
        }

        tblItemDetails.setItems(itemManageTMS);
    }

    private void selectedCustomer(String newValue) throws SQLException, ClassNotFoundException {
        ArrayList<Orders> orderList = new OrderController().searchOrders(newValue);
        vBoxOrderButtons.getChildren().clear();
        for (Orders order : orderList
             ) {
            vBoxOrderButtons.getChildren().add(createOrderNumberButon(order));
        }
    }

    private void loadCustomerID() throws SQLException, ClassNotFoundException {
        List<String> customerIDs = new CustomerController().getCustomerID();
        cmbCustomerID.getItems().addAll(customerIDs);
    }

    public void backButtonMouseClicked(MouseEvent mouseEvent) throws IOException, SQLException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Order will be restore to previous!");
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.OK);
        if (button == ButtonType.OK) {

            if (previousButton!=null) {
                if (new OrderController().deleteOrder(preiousOrder.getOrderID())) {
                    new OrderController().placeOrder(preiousOrder);
                }
            }
            manageOrdersContext.getChildren().clear();
            manageOrdersContext.getStylesheets().clear();
            Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/PlaceOrderForm.fxml")));
            manageOrdersContext.getChildren().add(load);

        }
    }

    public void orderRemove(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        if (selectedRow == -1){
            new Alert(Alert.AlertType.WARNING,"ERROR --> selectedRow(-1)").show();
        }else{
            if (new OrderController().deleteOrderedItem(
                    txtOrderID.getText(),
                    itemManageTMS.get(selectedRow).getItemCode()
            )){
                itemManageTMS.remove(selectedRow);
                tblItemDetails.refresh();
                if (itemManageTMS.isEmpty()){
                    new Alert(Alert.AlertType.WARNING,"Empty Order!").show();
                }
                calculateGrandTotal();
            }else{
                System.out.println("ERROR-->orderRemove.deleteFailed(false)");
            }
            if (itemManageTMS.isEmpty()){
                btnDone.setDisable(true);
            }else{
                btnDone.setDisable(false);
            }
        }
    }

    public void orderEditConfirm(MouseEvent mouseEvent) throws IOException {
        selectedItemID = itemManageTMS.get(selectedRow).getItemCode();
        orderID = txtOrderID.getText();
        itemDetailsMNG = new ItemDetails(
                itemManageTMS.get(selectedRow).getItemCode(),
                itemManageTMS.get(selectedRow).getOrderQty(),
                itemManageTMS.get(selectedRow).getDiscount()
        );
        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/EditOrderDetails.fxml")));
        Scene scene = new Scene(load);
        Stage stage = new Stage();
        stage.setScene(scene);

        manageOrdersContext.setDisable(true);

        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                manageOrdersContext.setDisable(false);
                itemManageTMS.clear();
                try {
                    setDataToBoard(getLoadedOrder(orderID));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (itemManageTMS.isEmpty()){
                    new Alert(Alert.AlertType.CONFIRMATION,"Order will be deleted!").show();
                    try {
                        deleteEntireOrder(mouseEvent);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }

        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                manageOrdersContext.setDisable(false);
                tblItemDetails.getItems().clear();
                try {
                    setDataToBoard(getLoadedOrder(orderID));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (itemManageTMS.isEmpty()){
                    new Alert(Alert.AlertType.CONFIRMATION,"Order will be deleted!").show();
                    try {
                        deleteEntireOrder(mouseEvent);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        stage.show();


    }

    public Orders getLoadedOrder(String orderIDAfterEdited) throws SQLException, ClassNotFoundException {
        return new OrderController().searchOrder(orderIDAfterEdited);
    }

    public void BackButtonMouseEN(MouseEvent mouseEvent) {
        SQBackToLogin.setFill(Color.rgb(99, 110, 114,1));
    }

    public void BackButtonMouseEX(MouseEvent mouseEvent) {
        SQBackToLogin.setFill(Color.rgb(127, 140, 141,1));
    }

    public void deleteEntireOrder(MouseEvent mouseEvent) throws SQLException, ClassNotFoundException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to delete the order?");
        Optional<ButtonType> result = alert.showAndWait();
        ButtonType button = result.orElse(ButtonType.OK);
        if (button == ButtonType.OK) {
            if (new OrderController().deleteOrder(txtOrderID.getText())) {
                Alert alt = new Alert(Alert.AlertType.INFORMATION, "Deleted!");
                Optional<ButtonType> rst = alt.showAndWait();
                ButtonType btn = rst.orElse(ButtonType.OK);
                if (btn == ButtonType.OK) {
                    clearBoard();
                } else {
                    clearBoard();
                }
            } else {
                System.out.println("Failed!");
            }
        }
    }

    public void exit(MouseEvent mouseEvent) throws IOException, SQLException, ClassNotFoundException {
        clearBoard();
        initialize();
    }
}
