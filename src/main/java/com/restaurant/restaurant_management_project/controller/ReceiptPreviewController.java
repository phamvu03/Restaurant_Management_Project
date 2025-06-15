package com.restaurant.restaurant_management_project.controller;

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
        // Đây là nơi bạn sẽ thêm logic xử lý khi nhấn "Xác nhận & In hóa đơn"
        // Ví dụ: Lưu hóa đơn vào DB, gọi hàm in, reset giỏ hàng, vv.
        System.out.println("Xác nhận và in hóa đơn...");

        // Gọi hàm handleDoneButton() của bạn ở đây
        // Lưu ý: handleDoneButton() có thể cần được truyền vào hoặc truy cập thông qua một callback/service
        // Hiện tại, tôi sẽ mô phỏng nó.
        handleDoneButton();

        // Sau khi hoàn tất, bạn có thể quay lại màn hình chính hoặc màn hình đặt hàng trống
        // Hoặc hiển thị một thông báo thành công và sau đó chuyển màn hình
    }

    private void handleDoneButton() {
//         Logic của bạn từ hàm handleDoneButton() gốc
//         currentOrder.clear(); // Xóa giỏ hàng
//         renderOrderItems(); // Cập nhật UI giỏ hàng
//         updateCartSummary(); // Cập nhật tổng tiền

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Hóa đơn đã được xác nhận và sẵn sàng để in!");
        alert.showAndWait();

        // Sau khi xử lý, có thể quay lại trang đặt hàng hoặc trang chủ
        if (orderViewRoot != null) {
            invoiceLayout.getScene().setRoot(orderViewRoot);
            // Có thể cần truyền thông tin về việc giỏ hàng đã được xóa về Controller cũ để nó cập nhật UI
            // Ví dụ: EventBus hoặc callback
        }
    }
}