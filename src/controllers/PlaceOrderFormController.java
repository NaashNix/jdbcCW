package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Customer;
import model.Item;
import model.ItemDetails;
import model.Orders;
import tm.ItemCartTM;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PlaceOrderFormController{
    public Rectangle addCustomerSQ;
    public ImageView imgAddCustomer;
    public static Stage stage;
    public EventHandler<MouseEvent> handler = MouseEvent::consume;
    public Label txtCustomerName;
    public Label txtCustomerTitle;
    public Label txtCustomerAddress;
    public Label txtCustomerPostalCode;
    public JFXComboBox<String> cmbItemSelector;
    public JFXComboBox<String> cmbCustomerIDs;
    public Label txtQtyOnHand;
    public Label txtUnitPrice;
    public JFXButton btnClearForm;
    public JFXButton btnAddToCart;
    public TextField txtReqAmount;
    public TextField txtDiscountAmount;
    public TextField txtItemDesc;
    public Label txtCaptionQtyOnHand;
    public TableView<ItemCartTM> tblCart;
    public TableColumn colItemCode;
    public TableColumn colQty;
    public TableColumn colPrice;
    public Label txtGrandTotal;
    public DecimalFormat df=new DecimalFormat("#.##");
    public JFXButton btnPay;
    public Label txtItemTotal;
    public Label lblDiscountedAmount;
    public Label lblSubTotal;
    public Label lblOrderID;
    public int tableSelectedRow = -1;
    public Label txtCustomerNameLabel;
    public Label txtOrderIDLabel;
    public JFXButton btnRemoveRow;
    public JFXButton btnModifyOrder;
    public AnchorPane placeOrderContext;
    public Rectangle SQBackToLogin;
    public ImageView imgBackToLogin;
    public Rectangle SQManageOrders;


    public void openAddCustomerForm(MouseEvent mouseEvent) throws IOException {
        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/AddCustomerForm.fxml")));
        Scene scene = new Scene(load);
        stage = new Stage();
        stage.setScene(scene);


        addCustomerSQ.addEventFilter(MouseEvent.ANY,handler);
        imgAddCustomer.addEventFilter(MouseEvent.ANY,handler);

        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                addCustomerSQ.removeEventFilter(MouseEvent.ANY,handler);
                imgAddCustomer.removeEventFilter(MouseEvent.ANY,handler);
                addCustomerMouseEX(mouseEvent);
                try {
                    initialize();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                addCustomerSQ.removeEventFilter(MouseEvent.ANY,handler);
                imgAddCustomer.removeEventFilter(MouseEvent.ANY,handler);
                addCustomerMouseEX(mouseEvent);
                try {
                    initialize();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
        stage.show();

    }

    public void addCustomerMouseEN(MouseEvent mouseEvent) {
        addCustomerSQ.setFill(Color.rgb(99, 110, 114,1));
    }

    public void addCustomerMouseEX(MouseEvent mouseEvent) {
        addCustomerSQ.setFill(Color.rgb(127, 140, 141,1));
    }


    public void setLblOrderID() throws SQLException, ClassNotFoundException {
        lblOrderID.setText(new OrderController().getOrderID());
    }

    public void initialize() throws SQLException, ClassNotFoundException {

        setLblOrderID();
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("reqAmount"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblCart.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            tableSelectedRow = newValue.intValue();
        });

        if (cmbItemSelector.getSelectionModel().isEmpty()){
            txtReqAmount.setEditable(false);
        }

        loadCustomerIDs();
        loadItemIDs();
        cmbItemSelector.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    try {
                        setItemData((String) newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
        cmbCustomerIDs.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    try {
                        setCustomerData((String) newValue);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });

    }

    private void setItemData(String itemID) throws SQLException, ClassNotFoundException {
        Item item = new ItemController().searchItem(itemID);
        if (item == null){
            System.out.println("NULL --> Failed!");
            
        }else{
            txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
            txtItemDesc.setText(item.getDesc());
            txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        }
    }

    private void loadItemIDs() throws SQLException, ClassNotFoundException {
        List<String> itemIDs = new ItemController().getItemIDs();
        cmbItemSelector.getItems().addAll(itemIDs);
    }

    private void setCustomerData(String customerID) throws SQLException, ClassNotFoundException {
        Customer customer = new CustomerController().searchCustomer(customerID);
        if (customer == null){
            System.out.println("CustomerDatasetNULL");
        }else{
            txtCustomerNameLabel.setText(customer.getCustomerName());
            txtOrderIDLabel.setText(lblOrderID.getText());
            txtCustomerName.setText(customer.getCustomerName());
            txtCustomerTitle.setText(customer.getCustomerTitle());
            txtCustomerAddress.setText(customer.getCustomerAddress());
            txtCustomerPostalCode.setText(customer.getCustomerPostalCode());
        }
    }
    public void calGrandTotal(){
        double total = 0.00;
        double totalDiscountedAmount = 0.0;
        for (ItemCartTM tempItemCartTM : itemCartTMS){
            total+=tempItemCartTM.getTotal();
            totalDiscountedAmount = totalDiscountedAmount+
                    (tempItemCartTM.getUnitPrice()*tempItemCartTM.getReqAmount())
                            /100*tempItemCartTM.getDiscount();

        }
        lblDiscountedAmount.setText("- "+df.format(totalDiscountedAmount));
        txtGrandTotal.setText(df.format(total));
        lblSubTotal.setText(df.format(total+totalDiscountedAmount));
    }
    List<String> customerIDs;
    public void loadCustomerIDs() throws SQLException, ClassNotFoundException {
        cmbCustomerIDs.getItems().clear();
        if (customerIDs == null){

        }else {
            customerIDs.clear();
        }
        customerIDs = new CustomerController().getCustomerID();
        cmbCustomerIDs.getItems().addAll(customerIDs);
    }

    public void clearItemFormOnAction(ActionEvent actionEvent) {
        txtReqAmount.clear();
        txtUnitPrice.setText(null);
        txtQtyOnHand.setText(null);
        txtDiscountAmount.clear();
        cmbItemSelector.getSelectionModel().clearSelection();
        txtItemDesc.setText(null);
        txtItemTotal.setText("0.00");
        btnAddToCart.setDisable(true);
    }


    public void enableAddToCart(KeyEvent inputMethodEvent) throws InvocationTargetException , NumberFormatException{
        if (cmbItemSelector.getSelectionModel().isEmpty()){
            new Alert(Alert.AlertType.WARNING,"Plese Select Item.").show();
            txtItemTotal.setText("0.00");
        }else {
            txtReqAmount.setDisable(false);
            if (txtReqAmount.getText().isEmpty()) {
                btnAddToCart.setDisable(true);
                txtItemTotal.setText("0.00");
            } else {
                if (Integer.parseInt(txtReqAmount.getText()) > Integer.parseInt(txtQtyOnHand.getText())) {
                    txtCaptionQtyOnHand.setTextFill(Color.rgb(194, 54, 22));
                    txtQtyOnHand.setTextFill(Color.rgb(194, 54, 22));
                    btnAddToCart.setDisable(true);
                } else {
                    double tot = Double.parseDouble(txtUnitPrice.getText())
                            * Integer.parseInt(txtReqAmount.getText());
                    if (txtDiscountAmount.getText().isEmpty()){
                        txtItemTotal.setText(df.format(tot));
                    }else{
                          txtItemTotal.setText(df.format(tot - (tot/100 *
                          Double.parseDouble(txtDiscountAmount.getText()))));
                    }
                    btnAddToCart.setDisable(false);
                    txtCaptionQtyOnHand.setTextFill(Color.rgb(97, 96, 96));
                    txtQtyOnHand.setTextFill(Color.rgb(0, 0, 0));
                }
            }
        }
    }

    ObservableList<ItemCartTM> itemCartTMS = FXCollections.observableArrayList();
    public void addToCartOnAction(ActionEvent actionEvent) {
        String itemCode = cmbItemSelector.getValue();
        String itemDesc = txtItemDesc.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int reqAmount = Integer.parseInt(txtReqAmount.getText());
        System.out.println("Discount"+txtDiscountAmount.getText());
        double discount = txtDiscountAmount.getText().isEmpty()? 0.00 :
                 Double.parseDouble(df.format(Double.parseDouble(txtDiscountAmount.getText())));
        double total = calTota((unitPrice*reqAmount),discount);

        ItemCartTM itemCartTM = new ItemCartTM(
                itemCode,
                itemDesc,
                unitPrice,
                reqAmount,
                total,
                discount
        );

        int rowNumber = isExist(itemCartTM);

        if (rowNumber == -1){
            itemCartTMS.add(itemCartTM);
        }else{

            ItemCartTM sameItemCartTM = itemCartTMS.get(rowNumber);
            ItemCartTM newItemCartTM = new ItemCartTM(
                    sameItemCartTM.getItemCode(),
                    sameItemCartTM.getItemDesc(),
                    sameItemCartTM.getUnitPrice(),
                    sameItemCartTM.getReqAmount()+reqAmount,
                    sameItemCartTM.getTotal()+total,
                    sameItemCartTM.getDiscount()
            );
            itemCartTMS.remove(rowNumber);
            itemCartTMS.add(rowNumber,newItemCartTM);
        }

        clearItemFormOnAction(actionEvent);
        tblCart.setItems(itemCartTMS);
        calGrandTotal();
        if (tblCart.getItems().isEmpty()){
            btnPay.setDisable(true);
        }else{
            btnPay.setDisable(false);
        }

    }

    private double calTota(double subTotal, double discount) {
        return  Double.parseDouble(df.format(subTotal-(subTotal/100)*discount));
    }

    private int isExist(ItemCartTM itemCartTM){
        for (int i= 0; i < itemCartTMS.size(); i++){
            if (itemCartTM.getItemCode().equals(itemCartTMS.get(i).getItemCode())){
                return i;
            }
        }
        return -1;
    }

    public void cmbItemSelectorOnAction(ActionEvent actionEvent) {
        if (!cmbItemSelector.getSelectionModel().isEmpty()){
            txtReqAmount.setEditable(true);
        }
    }

    public void calItemTotalDiscountButton(KeyEvent keyEvent) throws InvocationTargetException {
            enableAddToCart(keyEvent);
    }

    public void placeOrderOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String today = (f.format(date));

        ArrayList<ItemDetails> itemDetails = new ArrayList<>();

        for (ItemCartTM tmpItmCartTM : itemCartTMS
             ) {

            itemDetails.add(new ItemDetails(
                    tmpItmCartTM.getItemCode(),
                    tmpItmCartTM.getReqAmount(),
                    tmpItmCartTM.getDiscount()
            ));
        }

        Orders order = new Orders(
                lblOrderID.getText(),
                today,
                cmbCustomerIDs.getValue(),
                itemDetails
        );

        if (new OrderController().placeOrder(order)){
            new Alert(Alert.AlertType.CONFIRMATION,"Done").show();
            setLblOrderID();
            clearCart(actionEvent);
            btnPay.setDisable(true);
        }else{
            new Alert(Alert.AlertType.WARNING,"Failed!").show();
            System.out.println("Failed --> "+lblOrderID.getText());
        }

    }

    public void clearCart(ActionEvent actionEvent){
        clearItemFormOnAction(actionEvent);
        MouseEvent mouseEvent = null;
        tableRowSelectedOrNot(mouseEvent);
        clearCustomer();
        txtOrderIDLabel.setText("Select Customer");
        itemCartTMS.clear();
        tblCart.refresh();
    }

    public void clearCustomer(){
        cmbCustomerIDs.getSelectionModel().clearSelection();
        txtCustomerPostalCode.setText(null);
        txtCustomerAddress.setText(null);
        txtCustomerTitle.setText(null);
        txtCustomerName.setText(null);
        txtCustomerNameLabel.setText("Hello Wrold!");
    }

    public void tableRowSelectedOrNot(MouseEvent mouseEvent) {
        if (tableSelectedRow == -1){
            btnRemoveRow.setDisable(true);
        }else{
            btnRemoveRow.setDisable(false);
            btnModifyOrder.setDisable(false);
        }
    }

    public void removeSelectedOnAction(ActionEvent actionEvent) {
        itemCartTMS.remove(tableSelectedRow);
        calGrandTotal();
        tblCart.refresh();
        MouseEvent mouseEvent = null;
        tableRowSelectedOrNot(mouseEvent);
    }

    public void modifyOrderOnAction(ActionEvent actionEvent) throws IOException {
        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/ModifyOrderedItem.fxml")));
        Scene scene = new Scene(load);
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void openManageOrders(MouseEvent mouseEvent) throws IOException {
        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("../view/ManageOrdersForm.fxml")));
        placeOrderContext.getChildren().clear();
        placeOrderContext.getStylesheets().clear();
        placeOrderContext.getChildren().add(load);

    }

    public void logoutMouseClicked(MouseEvent mouseEvent) throws IOException {
        URL resource = getClass().getResource("../view/loginForm.fxml");
        Parent load = FXMLLoader.load(resource);
        Stage window = (Stage) placeOrderContext.getScene().getWindow();
        window.setScene(new Scene(load));
        window.show();
        window.centerOnScreen();
    }

    public void BackButtonMouseEN(MouseEvent mouseEvent) {
        SQBackToLogin.setFill(Color.rgb(99, 110, 114,1));
    }

    public void BackButtonMouseEX(MouseEvent mouseEvent) {
        SQBackToLogin.setFill(Color.rgb(127, 140, 141,1));
    }

    public void manageOrdersMouseEN(MouseEvent mouseEvent) {
        SQManageOrders.setFill(Color.rgb(99, 110, 114,1));
    }

    public void manageOrdersMouseEX(MouseEvent mouseEvent) {
        SQManageOrders.setFill(Color.rgb(127, 140, 141,1));
    }


}
