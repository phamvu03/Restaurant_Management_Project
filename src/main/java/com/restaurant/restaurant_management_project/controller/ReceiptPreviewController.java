package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.OrderDetailDAO;
import com.restaurant.restaurant_management_project.model.Order;
import com.restaurant.restaurant_management_project.model.OrderDetail;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.sql.Date;

public class ReceiptPreviewController {

    @FXML
    private GridPane billItems;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Parent invoiceLayout;
    @FXML
    private Label orderId;
    @FXML
    private Label taxLabel;
    @FXML
    private Label discounLabel;

    private Parent orderViewRoot;
    private Map<RMenuItem, OrderDetail> currentOrder;
    private DecimalFormat currencyFormat;
    private int currentRow = 0;

    public void initializeData(Parent orderViewRoot, Map<RMenuItem, OrderDetail> currentOrder) {
        this.orderViewRoot = orderViewRoot;
        this.currentOrder = currentOrder;
        this.currencyFormat = new DecimalFormat("#,### đ");
        invoiceDetails();
    }

    private void invoiceDetails() {
        billItems.getChildren().clear();
        double totalPrice = 0;

        if (currentOrder != null && !currentOrder.isEmpty()) {
            for (Map.Entry<RMenuItem, OrderDetail> entry : currentOrder.entrySet()) {
                RMenuItem menuItem = entry.getKey();
                OrderDetail orderDetail = entry.getValue();
                double itemTotal = menuItem.getItemPrice().doubleValue() * orderDetail.getSoLuong();
                totalPrice += itemTotal;

                Label itemLabel = new Label(String.valueOf(menuItem.getItemName()));
                itemLabel.setWrapText(true);
                Label quantitiyLabel = new Label(String.valueOf(orderDetail.getSoLuong()));
                Label priceLabel = new Label(currencyFormat.format(itemTotal));

                currentRow++;
                billItems.addRow(currentRow, itemLabel, quantitiyLabel, new Label(), priceLabel);
            }
        } else {
            billItems.getChildren().add(new Label("Giỏ hàng trống."));
        }
        subtotalLabel.setText(currencyFormat.format(totalPrice));
        double tax = totalPrice * 10 / 100;
        totalPrice += tax;
        taxLabel.setText(currencyFormat.format(tax));
        totalLabel.setText(currencyFormat.format(totalPrice));
    }

    @FXML
    private void handleBack() {
        Stage currentStage = (Stage) invoiceLayout.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void handleConfirmAndPrint() {
        System.out.println("Xác nhận và in hóa đơn...");
        handleDoneButton();

        // Sau khi hoàn tất, bạn có thể quay lại màn hình chính hoặc màn hình đặt hàng trống
        // Hoặc hiển thị một thông báo thành công và sau đó chuyển màn hình
    }

    private void handleDoneButton() {
        try {
            // Create new order
            Order newOrder = new Order();
            newOrder.setThoiGianTao(new java.sql.Date(new java.util.Date().getTime()));
            newOrder.setThoiGianThanhToan(new java.sql.Date(new java.util.Date().getTime()));
            newOrder.setMaNV(null);
            newOrder.setMaDatBan(null);

            // Save order to database
            OrderDAO orderDAO = new OrderDAO();
            boolean orderSaved = orderDAO.addOrder(newOrder);

            if (orderSaved) {
                // Save order details
                OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
                for (OrderDetail detail : currentOrder.values()) {
                    detail.setMaDonHang(newOrder.getMaDonHang());
                    orderDetailDAO.addOrderDetail(detail);
                }

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Thanh toán thành công!");
                alert.showAndWait();

                // Clear current order and return to container screen
                currentOrder.clear();
                if (orderViewRoot != null) {
                    Stage currentStage = (Stage) invoiceLayout.getScene().getWindow();
                    currentStage.close();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Không thể lưu thông tin đơn hàng!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Có lỗi xảy ra: " + e.getMessage());
            alert.showAndWait();
        }
    }
}