package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import org.controlsfx.control.GridView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // Hiển thị món ăn lên giao diện
            itemList.setItems(FXCollections.observableArrayList(allMenuItems));
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
        HBox.setHgrow(orderItemsVBox, Priority.ALWAYS);
        for (Map.Entry<RMenuItem, OrderDetail> entry : currentOrder.entrySet()) {
            RMenuItem menuItem = entry.getKey();
            OrderDetail orderDetail = entry.getValue();

            if (orderDetail.getSoLuong() <= 0) continue;

            handleGenerateNewItemDetail(orderDetail, menuItem);
        }
    }

    private void handleGenerateNewItemDetail(OrderDetail orderDetail, RMenuItem item){
        GridPane itemGrid = new GridPane();
        itemGrid.setHgap(10);
        itemGrid.setPadding(new Insets(5));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(75);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        itemGrid.getColumnConstraints().addAll(col1, col2);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        itemGrid.getRowConstraints().add(rowConstraints);

        Label quantityLabel = new Label(String.valueOf(orderDetail.getSoLuong()));
        quantityLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 20px;");

        Label nameLabel = new Label(item.getItemName());
        nameLabel.setWrapText(true);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        HBox infoBox = new HBox(10, quantityLabel, nameLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        itemGrid.add(infoBox, 0, 0);

        Button btnDecrease = new Button("-");
        btnDecrease.setOnAction(e -> {
            orderDetail.setSoLuong(orderDetail.getSoLuong() - 1);
            if (orderDetail.getSoLuong() <= 0) {
                currentOrder.remove(item);
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
            currentOrder.remove(item);
            renderOrderItems();
            updateCartSummary();
        });

        HBox buttonBox = new HBox(5, btnDecrease, btnIncrease, btnDelete);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        itemGrid.add(buttonBox, 1, 0);
        orderItemsVBox.getChildren().add(itemGrid);
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
        lblCartTotal.setText(String.valueOf(totalPrice));
    }

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
        //TODO
        System.out.println("Thêm chiết khấu");
    }

    @FXML
    private void addServiceFee() {
        //TODO
        System.out.println("Thêm phí dịch vụ");
    }

    @FXML
    private void handleThanhToan() {
        if (currentOrder.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Giỏ hàng trống!");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReceiptPreviewView.fxml"));
            Parent invoiceRoot = loader.load();

            ReceiptPreviewController invoiceController = loader.getController();
            invoiceController.initializeData(orderViewRoot, currentOrder);

            Scene newScene = new Scene(invoiceRoot, 600, 800);
            Stage modalStage = new Stage();
            modalStage.setTitle("Xem trước hóa đơn");
            modalStage.setScene(newScene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(btnThanhToan.getScene().getWindow());
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể tải hóa đơn. Lỗi: " + e.getMessage());
            alert.showAndWait();
        }
    }
}