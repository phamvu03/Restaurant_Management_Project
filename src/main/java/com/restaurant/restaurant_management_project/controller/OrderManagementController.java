package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.OrderDetailDAO;
import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.model.Order;
import com.restaurant.restaurant_management_project.model.OrderDetail;
import com.restaurant.restaurant_management_project.model.OrderView;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import com.restaurant.restaurant_management_project.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

public class OrderManagementController implements Initializable {
    @FXML
    private DatePicker datePickerFilter;
    @FXML
    private TableView<OrderView> ordersTableView;
    @FXML
    private TableColumn<OrderView, String> maDonHangColumn;
    @FXML
    private TableColumn<OrderView, String> maDatBanColumn;
    @FXML
    private TableColumn<OrderView, String> maNVColumn;
    @FXML
    private TableColumn<OrderView, Date> thoiGianTaoColumn;
    @FXML
    private TableColumn<OrderView, Date> thoiGianThanhToanColumn;
    @FXML
    private TableColumn<OrderView, BigDecimal> doanhThuColumn;
    @FXML
    private Button btnXoaDonHang;
    @FXML
    private Button viewDetailsButton;

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private MenuItemDaoImpl menuItemDAO;
    private EmployeeDAO employeeDAO;

    private ObservableList<OrderView> orderViewList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.orderDAO = new OrderDAO();
        this.orderDetailDAO = new OrderDetailDAO();
        this.menuItemDAO = new MenuItemDaoImpl();
        this.employeeDAO = new EmployeeDAO();

        setupTableColumns();
        loadOrderData();
        
        // Add listener to enable/disable view details button based on selection
        ordersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewDetailsButton.setDisable(newSelection == null);
        });
    }

    /**
     * Liên kết các cột trong TableView với các thuộc tính trong lớp OrderView.
     */
    private void setupTableColumns() {
        maDonHangColumn.setCellValueFactory(new PropertyValueFactory<>("maDonHang"));
        maDatBanColumn.setCellValueFactory(new PropertyValueFactory<>("maDatBan"));
        maNVColumn.setCellValueFactory(new PropertyValueFactory<>("tenNV"));
        
        // Căn giữa cho cột thời gian
        thoiGianTaoColumn.setCellValueFactory(new PropertyValueFactory<>("thoiGianTao"));
        thoiGianTaoColumn.setStyle("-fx-alignment: CENTER;");
        
        thoiGianThanhToanColumn.setCellValueFactory(new PropertyValueFactory<>("thoiGianThanhToan"));
        thoiGianThanhToanColumn.setStyle("-fx-alignment: CENTER;");
        
        // Căn phải cho cột doanh thu
        doanhThuColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        doanhThuColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
    }

    /**
     * Tải danh sách đơn hàng từ DB, tính toán doanh thu cho mỗi đơn và hiển thị lên TableView.
     */
    private void loadOrderData() {
        orderViewList.clear(); // Xóa dữ liệu cũ
        List<Order> ordersFromDb = orderDAO.getAllOrders();
        List<Employee> employees = employeeDAO.getAllEmployee();

        for (Order order : ordersFromDb) {
            BigDecimal revenue = calculateRevenueForOrder(order.getMaDonHang());
            String tenNV = employees.stream()
                    .filter(emp -> emp.getMaNV().equals(order.getMaNV()))
                    .findFirst()
                    .map(Employee::getTenNV)
                    .orElse("Không xác định");
            orderViewList.add(new OrderView(order, revenue, tenNV));
        }

        ordersTableView.setItems(orderViewList);
    }

    /**
     * Tính tổng doanh thu cho một đơn hàng dựa vào mã đơn hàng.
     * @param orderId Mã đơn hàng cần tính doanh thu.
     * @return Tổng doanh thu dạng BigDecimal.
     */
    private BigDecimal calculateRevenueForOrder(String orderId) {
        BigDecimal totalRevenue = BigDecimal.ZERO;
        // getOrderDetailsByOrderId
        List<OrderDetail> details = orderDetailDAO.getAllEquipment();

        List<OrderDetail> filteredDetails = details.stream()
                .filter(d -> d.getMaDonHang().equals(orderId))
                .collect(Collectors.toList());

        for (OrderDetail detail : filteredDetails) {
            // LƯU Ý: Bạn cần tự thêm phương thức findByMaMon vào MenuItemDaoImpl
            RMenuItem menuItem = new RMenuItem();//menuItemDAO.findByMaMon(detail.getMaMon());
            if (menuItem != null) {
                BigDecimal itemPrice = menuItem.getItemPrice() != null ? menuItem.getItemPrice() : BigDecimal.ZERO;
                BigDecimal quantity = new BigDecimal(detail.getSoLuong());
                totalRevenue = totalRevenue.add(itemPrice.multiply(quantity));
            }
        }
        return totalRevenue;
    }

    @FXML
    void handleSearch(ActionEvent event) {
        LocalDate selectedDate = datePickerFilter.getValue();
        if (selectedDate == null) {
            ordersTableView.setItems(orderViewList);
            return;
        }

        List<OrderView> filtered = orderViewList.stream()
                .filter(orderView -> orderView.getThoiGianTao().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());

        ObservableList<OrderView> filteredList = FXCollections.observableList(filtered);

        ordersTableView.setItems(filteredList);
    }

    @FXML
    void handleSortByRevenue(ActionEvent event) {
        // Sắp xếp danh sách đang hiển thị trên bảng theo doanh thu tăng dần
        Comparator<OrderView> comparator = Comparator.comparing(OrderView::getRevenue);
        FXCollections.sort(ordersTableView.getItems(), comparator);
    }

    @FXML
    void handleDeleteOrder(ActionEvent event) {
        OrderView selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn đơn hàng cần xóa!");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Xác nhận xóa đơn hàng");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa đơn hàng " + selectedOrder.getMaDonHang() + "?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Xóa chi tiết đơn hàng trước
                if (orderDetailDAO.deleteOrderDetails(selectedOrder.getMaDonHang())) {
                    // Sau đó xóa đơn hàng
                    if (orderDAO.deleteOrder(selectedOrder.getMaDonHang())) {
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa đơn hàng thành công!");
                        loadOrderData(); // Tải lại dữ liệu
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa đơn hàng!");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa chi tiết đơn hàng!");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra khi xóa đơn hàng: " + e.getMessage());
            }
        }
    }

    @FXML
    void handleAddNewOrder(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddOrderView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm Đơn Hàng Mới");
            Scene newScene = new Scene( root, 600, 600);
            stage.setScene(newScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadOrderData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Giao Diện", "Không thể mở cửa sổ thêm đơn hàng.");
        }
    }

    @FXML
    void handleViewDetails(ActionEvent event) {
        OrderView selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn đơn hàng cần xem chi tiết!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OrderDetailsView.fxml"));
            Parent orderDetailsRoot = loader.load();

            OrderDetailsController orderDetailsController = loader.getController();
            
            // Get the full order details using stream
            Order order = orderDAO.getAllOrders().stream()
                .filter(o -> o.getMaDonHang().equals(selectedOrder.getMaDonHang()))
                .findFirst()
                .orElse(null);

            if (order == null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy thông tin đơn hàng!");
                return;
            }

            // Get order details using stream
            List<OrderDetail> orderDetails = orderDetailDAO.getAllEquipment().stream()
                .filter(detail -> detail.getMaDonHang().equals(selectedOrder.getMaDonHang()))
                .collect(Collectors.toList());

            // Get all menu items
            List<RMenuItem> allMenuItems = menuItemDAO.getAll();

            // Create a map of menu items and order details using stream
            Map<RMenuItem, OrderDetail> orderDetailsMap = orderDetails.stream()
                .collect(Collectors.toMap(
                    detail -> allMenuItems.stream()
                        .filter(item -> item.getItemId().equals(detail.getMaMon()))
                        .findFirst()
                        .orElse(null),
                    detail -> detail,
                    (existing, replacement) -> existing
                ));
            
            orderDetailsController.initializeData(order, orderDetailsMap);

            Scene newScene = new Scene(orderDetailsRoot, 600, 800);
            Stage modalStage = new Stage();
            modalStage.setTitle("Chi tiết đơn hàng - " + selectedOrder.getMaDonHang());
            modalStage.setScene(newScene);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(viewDetailsButton.getScene().getWindow());
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải chi tiết đơn hàng. Lỗi: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}