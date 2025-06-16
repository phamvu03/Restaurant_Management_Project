package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.*;
import com.restaurant.restaurant_management_project.dao.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class OrderDetailsController {

    @FXML
    private Label lblOrderId;
    @FXML
    private Label lblEntryTime;
    @FXML
    private Label lblExitTime;
    @FXML
    private Label lblTableId;
    @FXML
    private Label lblCustomerName;
    @FXML
    private Label lblPhoneNumber;
    @FXML
    private Label lblEmployeeId;
    @FXML
    private Label lblEmployeeName;
    @FXML
    private TableView<OrderDetail> orderItemsTable;
    @FXML
    private TableColumn<OrderDetail, String> itemNameColumn;
    @FXML
    private TableColumn<OrderDetail, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderDetail, Double> priceColumn;
    @FXML
    private TableColumn<OrderDetail, Double> totalColumn;
    @FXML
    private Label lblTotalAmount;

    private final DecimalFormat currencyFormat = new DecimalFormat("#,### đ");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private DatBanDAO datBanDAO;
    private EmployeeDAO employeeDAO;
    private OrderDAO orderDAO;
    private Order currentOrder;

    public OrderDetailsController() {
        this.datBanDAO = new DatBanDAO();
        this.employeeDAO = new EmployeeDAO();
        this.orderDAO = new OrderDAO();
    }

    public void initializeData(Order order, Map<RMenuItem, OrderDetail> orderDetails) {
        this.currentOrder = order;
        // Set order information
        lblOrderId.setText(String.valueOf(order.getMaDonHang()));
        lblEntryTime.setText(dateFormat.format(order.getThoiGianTao()));
        lblExitTime.setText(order.getThoiGianThanhToan() != null ?
            dateFormat.format(order.getThoiGianThanhToan()) : "Chưa thanh toán");

        // Set table booking information
        if (order.getMaDatBan() != null) {
            DatBan datBan = datBanDAO.getDatBanById(order.getMaDatBan());
            if (datBan != null) {
                lblTableId.setText(datBan.getMaDatBan());
                lblCustomerName.setText(datBan.getTenKhachHang());
                lblPhoneNumber.setText(datBan.getSoDienThoai());
            } else {
                lblTableId.setText("Không có thông tin");
                lblCustomerName.setText("Không có thông tin");
                lblPhoneNumber.setText("Không có thông tin");
            }
        } else {
            lblTableId.setText("Không có thông tin");
            lblCustomerName.setText("Không có thông tin");
            lblPhoneNumber.setText("Không có thông tin");
        }

        // Set employee information
        if (order.getMaNV() != null) {
            List<Employee> employees = employeeDAO.getAllEmployee();
            Employee employee = employees.stream()
                .filter(emp -> emp.getMaNV().equals(order.getMaNV()))
                .findFirst()
                .orElse(null);

            if (employee != null) {
                lblEmployeeId.setText(employee.getMaNV());
                lblEmployeeName.setText(employee.getTenNV());
            } else {
                lblEmployeeId.setText("Không có thông tin");
                lblEmployeeName.setText("Không có thông tin");
            }
        } else {
            lblEmployeeId.setText("Không có thông tin");
            lblEmployeeName.setText("Không có thông tin");
        }

        // Configure table columns
        itemNameColumn.setCellValueFactory(cellData -> {
            RMenuItem menuItem = orderDetails.keySet().stream()
                .filter(item -> item.getItemId().equals(cellData.getValue().getMaMon()))
                .findFirst()
                .orElse(null);
            return new SimpleStringProperty(menuItem != null ? menuItem.getItemName() : "");
        });

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        priceColumn.setCellValueFactory(cellData -> {
            String maMon = cellData.getValue().getMaMon();
            RMenuItem menuItem = orderDetails.keySet().stream()
                    .filter(item -> item.getItemId().equals(maMon))
                    .findFirst()
                    .orElse(null);
            double price = (menuItem != null) ? menuItem.getItemPrice().doubleValue() : 0.0;
            return new SimpleDoubleProperty(price).asObject();
        });

        totalColumn.setCellValueFactory(cellData -> {
            String maMon = cellData.getValue().getMaMon();
            int soLuong = cellData.getValue().getSoLuong();
            RMenuItem menuItem = orderDetails.keySet().stream()
                    .filter(item -> item.getItemId().equals(maMon))
                    .findFirst()
                    .orElse(null);
            double total = (menuItem != null) ? menuItem.getItemPrice().doubleValue() * soLuong : 0.0;
            return new SimpleDoubleProperty(total).asObject();
        });

        // Format currency columns
        priceColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });

        totalColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(total));
                }
            }
        });

        // Add items to table
        orderItemsTable.getItems().addAll(orderDetails.values());

        // Calculate and display total amount
        double totalAmount = orderDetails.entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getItemPrice().doubleValue() * entry.getValue().getSoLuong())
            .sum();
        lblTotalAmount.setText(currencyFormat.format(totalAmount));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblOrderId.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleDone() {
        if (currentOrder == null) {
            showAlert("Lỗi", "Không tìm thấy thông tin đơn hàng", Alert.AlertType.ERROR);
            return;
        }

        if (currentOrder.getThoiGianThanhToan() != null) {
            showAlert("Thông báo", "Đơn hàng này đã được thanh toán", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            // Update payment time
            currentOrder.setThoiGianThanhToan(new java.sql.Date(new Date().getTime()));
            
            // Update order in database using addOrder
            boolean success = orderDAO.addOrder(currentOrder);
            
            if (success) {
                showAlert("Thành công", "Thanh toán đơn hàng thành công", Alert.AlertType.INFORMATION);
                // Update UI
                lblExitTime.setText(dateFormat.format(currentOrder.getThoiGianThanhToan()));
                // Close the window
                handleClose();
            } else {
                showAlert("Lỗi", "Không thể cập nhật thông tin thanh toán", Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            showAlert("Lỗi", "Có lỗi xảy ra khi xử lý thanh toán: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 