package com.restaurant.restaurant_management_project.model;

import javafx.scene.image.Image;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class RMenuItem {

    private Integer id;
    private String itemId;
    private String itemName;
    private boolean itemStatus;
    private BigDecimal itemPrice;
    private String itemCategory;
    private byte[] itemImage;
    private String itemUnit;
    private Integer sideItem;


    public RMenuItem() {
    }

    public RMenuItem(Integer id, String itemId, String itemName, boolean itemStatus, BigDecimal itemPrice, String itemCategory, byte[] itemImage, String itemUnit, Integer sideItem, Integer soldQuantity) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemStatus = itemStatus;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.itemImage = itemImage;
        this.itemUnit = itemUnit;
        this.sideItem = sideItem;
    }

    public RMenuItem(String itemName, boolean itemStatus, BigDecimal itemPrice,
                     String itemCategory, byte[] itemImage, String itemUnit, Integer sideItem) {
        this.id = null;
        this.itemId = null;
        this.itemName = itemName;
        this.itemStatus = itemStatus;
        this.itemPrice = itemPrice;
        this.itemCategory = itemCategory;
        this.itemImage = itemImage;
        this.itemUnit = itemUnit;
        this.sideItem = sideItem;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(boolean itemStatus) {
        this.itemStatus = itemStatus;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public byte[] getItemImage() {
        return itemImage;
    }

    public void setItemImage(byte[] itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public Integer getSideItem() {
        return sideItem;
    }

    public void setSideItem(Integer sideItem) {
        this.sideItem = sideItem;
    }

}
