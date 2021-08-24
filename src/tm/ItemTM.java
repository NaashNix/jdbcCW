package tm;

import javafx.scene.control.Button;

import javax.swing.*;

public class ItemTM {
    private String itemCode;
    private String desc;
    private int qtyOnHand;
    private String packSize;
    private Double unitPrice;

    public ItemTM(String itemCode, String desc, int qtyOnHand, String packSize,
                  Double unitPrice) {
        this.itemCode = itemCode;
        this.desc = desc;
        this.qtyOnHand = qtyOnHand;
        this.packSize = packSize;
        this.unitPrice = unitPrice;
    }


    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public String getPackSize() {
        return packSize;
    }

    public void setPackSize(String packSize) {
        this.packSize = packSize;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

}
