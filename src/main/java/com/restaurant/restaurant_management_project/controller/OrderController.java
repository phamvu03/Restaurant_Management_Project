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
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.geometry.Insets;
import com.restaurant.restaurant_management_project.model.OrderDetail;
import com.restaurant.restaurant_management_project.model.Order;
import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.OrderDetailDAO;
import javafx.stage.Stage;
import javafx.scene.Parent;
import com.restaurant.restaurant_management_project.dao.DatBanDAO;
import com.restaurant.restaurant_management_project.model.DatBan;
import java.time.LocalDateTime;
import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.model.Employee;

public class OrderController {

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

    @FXML
    private Button filterBtn;

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
        
        // Tạo dữ liệu mặc định
        createDefaultEmployee();
        createDefaultDatBan();
        createDefaultOrder();
    }

    MenuItemDaoImpl dao = new MenuItemDaoImpl();
    
    private void loadMenuItems() {
        allMenuItems = dao.getAll();
    }

    private void initializeGrid() {
        itemList.setCellFactory(param -> new ItemGridCell(this::onItemClicked));

        final int SPACING = 15;
        final double SCALE_RATIO = 0.8;
        itemList.setCellHeight(300 * SCALE_RATIO);
        itemList.setHorizontalCellSpacing(SPACING);
        itemList.setVerticalCellSpacing(SPACING);

        // Logic to have up to 6 columns with scale ratio
        itemList.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                int columns = (int) (newVal.doubleValue() / ((200 * SCALE_RATIO) + SPACING));
                if (columns > 6) columns = 6;
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
    private void handleFilter() {
        // Create a dialog for filter options
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Lọc món ăn");
        dialog.setHeaderText("Chọn tiêu chí lọc");

        // Create filter options
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Price range filter
        HBox priceBox = new HBox(10);
        TextField minPrice = new TextField();
        TextField maxPrice = new TextField();
        minPrice.setPromptText("Giá tối thiểu");
        maxPrice.setPromptText("Giá tối đa");
        priceBox.getChildren().addAll(new Label("Khoảng giá:"), minPrice, new Label("-"), maxPrice);

        // Category filter
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.setPromptText("Chọn loại món");
        categoryCombo.getItems().addAll("Tất cả", "Món chính", "Món phụ", "Đồ uống", "Tráng miệng");

        content.getChildren().addAll(priceBox, categoryCombo);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.APPLY) {
                // Apply filters
                List<RMenuItem> filteredItems = allMenuItems.stream()
                    .filter(item -> {
                        boolean priceMatch = true;
                        if (!minPrice.getText().isEmpty()) {
                            priceMatch = priceMatch && item.getItemPrice().doubleValue() >= Double.parseDouble(minPrice.getText());
                        }
                        if (!maxPrice.getText().isEmpty()) {
                            priceMatch = priceMatch && item.getItemPrice().doubleValue() <= Double.parseDouble(maxPrice.getText());
                        }

                        boolean categoryMatch = true;
                        if (categoryCombo.getValue() != null && !categoryCombo.getValue().equals("Tất cả")) {
                            categoryMatch = item.getItemCategory().equals(categoryCombo.getValue());
                        }

                        return priceMatch && categoryMatch;
                    })
                    .collect(Collectors.toList());

                // Update the grid with filtered items
                itemList.setItems(FXCollections.observableArrayList(filteredItems));
            }
        });
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

    private void createDefaultEmployee() {
        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            Employee defaultEmployee = new Employee();
            defaultEmployee.setTenNV("Nhân viên mặc định");
            defaultEmployee.setNgaySinh(LocalDate.now());
            defaultEmployee.setSDT("0123456789");
            defaultEmployee.setEmail("default@restaurant.com");
            defaultEmployee.setChucVu("Nhân viên");
            defaultEmployee.setLuong(new java.math.BigDecimal("5000000"));

            boolean success = employeeDAO.addEmployee(defaultEmployee);
            if (success) {
                System.out.println("Đã tạo nhân viên mặc định thành công");
            } else {
                System.out.println("Không thể tạo nhân viên mặc định");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo nhân viên mặc định: " + e.getMessage());
        }
    }

    private void createDefaultDatBan() {
        try {
            DatBanDAO datBanDAO = new DatBanDAO();
            DatBan defaultDatBan = new DatBan();
            defaultDatBan.setMaBan("BA0001"); // Mã bàn mặc định
            defaultDatBan.setTenKhachHang("Khách lẻ");
            defaultDatBan.setSoKhach(1);
            defaultDatBan.setSoDienThoai("0123456789");
            defaultDatBan.setThoiGianDat(LocalDateTime.now());
            defaultDatBan.setTrangThai("Đã đặt");
            defaultDatBan.setGhiChu("Đặt bàn mặc định cho khách lẻ");
            
            boolean success = datBanDAO.themDatBan(defaultDatBan);
            if (success) {
                System.out.println("Đã tạo đặt bàn mặc định thành công");
            } else {
                System.out.println("Không thể tạo đặt bàn mặc định");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo đặt bàn mặc định: " + e.getMessage());
        }
    }

    private void createDefaultOrder() {
        try {
            // Lấy đặt bàn mặc định mới nhất
            DatBanDAO datBanDAO = new DatBanDAO();
            List<DatBan> dsDatBan = datBanDAO.getDSDatBan();
            if (dsDatBan.isEmpty()) {
                System.out.println("Không tìm thấy đặt bàn mặc định, đang tạo mới...");
                createDefaultDatBan();
                dsDatBan = datBanDAO.getDSDatBan();
                if (dsDatBan.isEmpty()) {
                    System.out.println("Không thể tạo đặt bàn mặc định");
                    return;
                }
            }
            
            // Lấy đặt bàn mới nhất và kiểm tra mã đặt bàn
            DatBan latestDatBan = dsDatBan.get(0);
            String maDatBan = latestDatBan.getMaDatBan();
            if (maDatBan == null || maDatBan.trim().isEmpty()) {
                System.out.println("Mã đặt bàn không hợp lệ");
                return;
            }

            // Lấy nhân viên mặc định
            EmployeeDAO employeeDAO = new EmployeeDAO();
            List<Employee> dsNhanVien = employeeDAO.getAllEmployee();
            if (dsNhanVien.isEmpty()) {
                System.out.println("Không tìm thấy nhân viên mặc định");
                return;
            }
            Employee defaultEmployee = dsNhanVien.get(0);

            // Tạo đơn hàng mặc định
            Order defaultOrder = new Order();
            defaultOrder.setMaDatBan(maDatBan); // Sử dụng mã đặt bàn đã kiểm tra
            defaultOrder.setMaNV(defaultEmployee.getMaNV());
            defaultOrder.setThoiGianTao(new java.sql.Date(new java.util.Date().getTime()));
            defaultOrder.setThoiGianThanhToan(null); // Chưa thanh toán

            boolean success = orderDAO.addOrder(defaultOrder);
            if (success) {
                System.out.println("Đã tạo đơn hàng mặc định thành công");
                
                // Lấy danh sách đơn hàng và tìm đơn hàng mới nhất bằng Stream
                List<Order> orders = orderDAO.getAllOrders();
                String maDonHang = orders.stream()
                    .max((o1, o2) -> o1.getThoiGianTao().compareTo(o2.getThoiGianTao()))
                    .map(Order::getMaDonHang)
                    .orElse(null);

                if (maDonHang != null) {
                    // Tạo chi tiết đơn hàng mặc định
                    if (allMenuItems != null && !allMenuItems.isEmpty()) {
                        RMenuItem defaultMenuItem = allMenuItems.get(0);
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setMaDonHang(maDonHang);
                        orderDetail.setMaMon(defaultMenuItem.getItemId());
                        orderDetail.setSoLuong(1);
                        
                        boolean detailSuccess = orderDetailDAO.addOrderDetail(orderDetail);
                        if (detailSuccess) {
                            System.out.println("Đã tạo chi tiết đơn hàng mặc định thành công");
                        } else {
                            System.out.println("Không thể tạo chi tiết đơn hàng mặc định");
                        }
                    }
                } else {
                    System.out.println("Không thể lấy mã đơn hàng vừa tạo");
                }
            } else {
                System.out.println("Không thể tạo đơn hàng mặc định");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo đơn hàng mặc định: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug
        }
    }
}