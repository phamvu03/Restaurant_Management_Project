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
import javafx.scene.layout.*;
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
import javafx.scene.Parent;

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
    private Parent orderViewRoot;

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
        itemList.setCellFactory(param -> new ItemGridCell(this::onItemClicked));

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

            GridPane itemGrid = new GridPane();
            itemGrid.setHgap(10);
            itemGrid.setPadding(new Insets(5));

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(75);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(25);
            itemGrid.getColumnConstraints().addAll(col1, col2);

            //add RowContraints
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setVgrow(Priority.ALWAYS);
            itemGrid.getRowConstraints().add(rowConstraints);

            Label quantityLabel = new Label(String.valueOf(orderDetail.getSoLuong()));
            quantityLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 20px;");

            Label nameLabel = new Label(menuItem.getItemName());
            nameLabel.setWrapText(true);
            HBox.setHgrow(nameLabel, Priority.ALWAYS);

            // Label priceLabel = new Label(currencyFormat.format(menuItem.getItemPrice().doubleValue() * orderDetail.getSoLuong()));
            // priceLabel.setAlignment(Pos.CENTER_RIGHT);

//            HBox infoBox = new HBox(10, quantityLabel, nameLabel, priceLabel);
            HBox infoBox = new HBox(10, quantityLabel, nameLabel);
            infoBox.setAlignment(Pos.CENTER_LEFT);
            itemGrid.add(infoBox, 0, 0);


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
                currentOrder.remove(menuItem);
                renderOrderItems();
                updateCartSummary();
            });

            HBox buttonBox = new HBox(5, btnDecrease, btnIncrease, btnDelete);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            itemGrid.add(buttonBox, 1, 0);
            orderItemsVBox.getChildren().add(itemGrid);
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
        lblCartTotal.setText("");
        btnThanhToan.setText("Thanh toán");
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
        if (currentOrder.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Giỏ hàng trống!");
            alert.showAndWait();
            return;
        }

        orderViewRoot = btnThanhToan.getScene().getRoot();
        Parent invoiceRoot = createInvoiceView();
        btnThanhToan.getScene().setRoot(invoiceRoot);
    }

    private Parent createInvoiceView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(20));

        // Title
        Label title = new Label("Hóa đơn thanh toán");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        layout.setTop(title);

        // Bill details
        VBox invoiceContent = new VBox(10);
        invoiceContent.setPadding(new Insets(20, 0, 20, 0));

        // Display ordered items
        VBox billItemsVBox = new VBox(5);
        billItemsVBox.setStyle("-fx-border-color: lightgrey; -fx-padding: 10;");
        double totalPrice = 0;
        for (Map.Entry<RMenuItem, OrderDetail> entry : currentOrder.entrySet()) {
            RMenuItem menuItem = entry.getKey();
            OrderDetail orderDetail = entry.getValue();
            double itemTotal = menuItem.getItemPrice().doubleValue() * orderDetail.getSoLuong();
            totalPrice += itemTotal;
            Label itemLabel = new Label(String.format("%d x %s", orderDetail.getSoLuong(), menuItem.getItemName()));
            Label priceLabel = new Label(currencyFormat.format(itemTotal));
            HBox itemRow = new HBox(itemLabel, priceLabel);
            HBox.setHgrow(itemLabel, Priority.ALWAYS);
            billItemsVBox.getChildren().add(itemRow);
        }

        // Summary (Subtotal, Total)
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(10);
        summaryGrid.setVgap(5);
        summaryGrid.add(new Label("Tạm tính:"), 0, 0);
        Label subtotalLabel = new Label(currencyFormat.format(totalPrice));
        subtotalLabel.setStyle("-fx-font-weight: bold;");
        summaryGrid.add(subtotalLabel, 1, 0);

        summaryGrid.add(new Label("Tổng cộng:"), 0, 1);
        Label totalLabel = new Label(currencyFormat.format(totalPrice));
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        summaryGrid.add(totalLabel, 1, 1);

        // Align summary to the right
        ColumnConstraints col1Constraint = new ColumnConstraints();
        col1Constraint.setHgrow(Priority.ALWAYS);
        ColumnConstraints col2Constraint = new ColumnConstraints();
        summaryGrid.getColumnConstraints().addAll(col1Constraint, col2Constraint);

        invoiceContent.getChildren().addAll(new Label("Chi tiết:"), billItemsVBox, new Separator(), summaryGrid);
        layout.setCenter(invoiceContent);

        // Bottom buttons
        Button btnBack = new Button("Quay lại");
        btnBack.setOnAction(e -> btnBack.getScene().setRoot(orderViewRoot));

        Button btnConfirm = new Button("Xác nhận & In hóa đơn");
        btnConfirm.setDefaultButton(true);
        btnConfirm.setOnAction(e -> {
            handleDoneButton();
        });

        HBox buttonBar = new HBox(20, btnBack, btnConfirm);
        buttonBar.setAlignment(Pos.CENTER);
        layout.setBottom(buttonBar);
        BorderPane.setMargin(buttonBar, new Insets(20, 0, 0, 0));

        return layout;
    }
}
