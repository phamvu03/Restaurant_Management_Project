/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.GridView;
import org.controlsfx.control.PopOver; // Ví dụ sử dụng PopOver từ ControlsFX

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.geometry.Insets;
import com.restaurant.restaurant_management_project.model.OrderDetail;
import com.restaurant.restaurant_management_project.model.Order;
import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.OrderDetailDAO;
import javafx.stage.Stage;

public class OrderViewController {

    @FXML
    private GridView<RMenuItem> itemList;

    @FXML
    private VBox orderItemsVBox;

    @FXML
    private Label lblCartItemCount;

    @FXML
    private Label lblCartTotal;

    @FXML
    private Button btnThanhToan;

    @FXML
    private TextField txtGhiChu;

    @FXML
    private Label lblItemName;

    private final DecimalFormat currencyFormat = new DecimalFormat("#,### đ");

    private List<RMenuItem> allMenuItems;

    private Map<RMenuItem, OrderDetail> currentOrder = new HashMap<>();

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;

    @FXML
    public void initialize() {
        // Tải danh sách món ăn
        loadMenuItems();
        // Khởi tạo lưới
        initializeGrid();
        if(allMenuItems !=null) {
            itemList.setItems(FXCollections.observableArrayList(allMenuItems)); // Hiển thị món ăn lên giao diện
        }
        updateCartSummary(); // Cập nhật thông tin giỏ hàng ban đầu

        orderDAO = new OrderDAO();
        orderDetailDAO = new OrderDetailDAO();
    }

    MenuItemDaoImpl dao = new MenuItemDaoImpl();
    
    private void loadMenuItems() {
        allMenuItems = dao.getAll();
    }

    private void initializeGrid() {
        itemList.setCellFactory(param -> new ItemView(this::onItemClicked));

        final int SPACING = 15;
        itemList.setCellHeight(300);
        itemList.setHorizontalCellSpacing(SPACING);
        itemList.setVerticalCellSpacing(SPACING);

        // Logic to have up to 5 columns
        itemList.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                int columns = (int) (newVal.doubleValue() / (200 + SPACING));
                if (columns > 5) columns = 5;
                if (columns < 1) columns = 1;
                // Adjust cell width based on number of columns
                double cellWidth = (newVal.doubleValue() - (columns - 1) * SPACING) / columns;
                itemList.setCellWidth(cellWidth);
            }
        });
    }

    private void onItemClicked(RMenuItem item) {
        if (lblItemName != null) {
            lblItemName.setText(item.getItemName());
        }
        System.out.println("Clicked on: " + item.getItemName());
        addItemToOrder(item, 1);
    }
    
    private void addItemToOrder(RMenuItem item, int quantityToAdd) {
        if (currentOrder.containsKey(item)) {
            OrderDetail orderDetail = currentOrder.get(item);
            orderDetail.setSoLuong(orderDetail.getSoLuong() + quantityToAdd);
        } else {
            OrderDetail newOrderDetail = new OrderDetail(null, item.getItemId(), quantityToAdd);
            currentOrder.put(item, newOrderDetail);
        }
        renderOrderItems();
        updateCartSummary();
    }

    private void renderOrderItems() {
        orderItemsVBox.getChildren().clear();
        for (Map.Entry<RMenuItem, OrderDetail> entry : currentOrder.entrySet()) {
            RMenuItem menuItem = entry.getKey();
            OrderDetail orderDetail = entry.getValue();

            if (orderDetail.getSoLuong() <= 0) continue; 

            HBox itemBox = new HBox(10);
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.setPadding(new Insets(5));

            Label quantityLabel = new Label(String.valueOf(orderDetail.getSoLuong()));
            quantityLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 20px;");

            Label nameLabel = new Label(menuItem.getItemName());
            HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS); 

            Label priceLabel = new Label(currencyFormat.format(menuItem.getItemPrice().doubleValue() * orderDetail.getSoLuong()));
            priceLabel.setAlignment(Pos.CENTER_RIGHT);

            Button btnDecrease = new Button("-");
            btnDecrease.setOnAction(e -> {
                orderDetail.setSoLuong(orderDetail.getSoLuong() - 1);
                if (orderDetail.getSoLuong() <= 0) {
                    currentOrder.remove(menuItem);
                }
                renderOrderItems();
                updateCartSummary();
            });

            Button btnIncrease = new Button("+");
            btnIncrease.setOnAction(e -> {
                orderDetail.setSoLuong(orderDetail.getSoLuong() + 1);
                renderOrderItems();
                updateCartSummary();
            });

            Button btnDelete = new Button("X");
            btnDelete.setOnAction(e -> {
                orderDetail.setSoLuong(0);
                renderOrderItems();
                updateCartSummary();
            });
            
            HBox buttonBox = new HBox(5, btnDecrease, btnIncrease, btnDelete);
            buttonBox.setAlignment(Pos.CENTER_LEFT);

            itemBox.getChildren().addAll(quantityLabel, nameLabel, priceLabel, buttonBox);
            orderItemsVBox.getChildren().add(itemBox);
        }
    }

    private void updateCartSummary() {
        int totalItems = 0;
        double totalPrice = 0;
        for (OrderDetail item : currentOrder.values()) {
            totalItems += item.getSoLuong();
            // To calculate price, we need to get the RMenuItem associated with this OrderDetail
            RMenuItem menuItem = getMenuItemFromOrderDetail(item);
            if(menuItem != null){
                totalPrice += menuItem.getItemPrice().doubleValue() * item.getSoLuong();
            }
        }
        lblCartItemCount.setText("(" + totalItems + ")");
        lblCartTotal.setText(currencyFormat.format(totalPrice));
        btnThanhToan.setText("Thanh toán " + currencyFormat.format(totalPrice));
    }


    //TODO: double check this method
    private RMenuItem getMenuItemFromOrderDetail(OrderDetail orderDetail) {
        for (Map.Entry<RMenuItem, OrderDetail> entry : currentOrder.entrySet()) {
            if (entry.getValue().equals(orderDetail)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @FXML
    private void handleDoneButton() {
        if (currentOrder.isEmpty()) {
            // Optionally, show an alert that the order is empty
            closeWindow();
            return;
        }

        Order newOrder = new Order();
        newOrder.setThoiGianTao(new java.sql.Date(new Date().getTime()));
        newOrder.setMaNV(null); 
        newOrder.setMaDatBan(null);

        boolean orderSaved = orderDAO.addOrder(newOrder);

        if (orderSaved) {
            for (OrderDetail detail : currentOrder.values()) {
                orderDetailDAO.addOrderDetail(detail);
            }

            currentOrder.clear();
            renderOrderItems();
            updateCartSummary();
        } else {
            // Handle error: show an alert to the user
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not save the order.");
            alert.showAndWait();
        }
        closeWindow();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) itemList.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clearCart() {
        currentOrder.clear();
        renderOrderItems();
        updateCartSummary();
    }

    @FXML
    private void addDiscount() {
        System.out.println("Thêm chiết khấu");
    }

    @FXML
    private void addServiceFee() {
        System.out.println("Thêm phí dịch vụ");
    }

    @FXML
    private void handleThanhToan() {
        System.out.println("Tiến hành thanh toán với tổng tiền: " + lblCartTotal.getText());
        System.out.println("Ghi chú: " + txtGhiChu.getText());
    }
}
