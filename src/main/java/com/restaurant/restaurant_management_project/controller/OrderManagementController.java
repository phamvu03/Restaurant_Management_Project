package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.OrderDetailDAO;
import com.restaurant.restaurant_management_project.model.Order;
import com.restaurant.restaurant_management_project.model.OrderDetail;
import com.restaurant.restaurant_management_project.model.OrderView;
import com.restaurant.restaurant_management_project.model.RMenuItem;
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

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private MenuItemDaoImpl menuItemDAO;

    private ObservableList<OrderView> orderViewList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.orderDAO = new OrderDAO();
        this.orderDetailDAO = new OrderDetailDAO();
        this.menuItemDAO = new MenuItemDaoImpl();

        setupTableColumns();
        loadOrderData();
    }

    /**
     * Liên kết các cột trong TableView với các thuộc tính trong lớp OrderView.
     */
    private void setupTableColumns() {
        maDonHangColumn.setCellValueFactory(new PropertyValueFactory<>("maDonHang"));
        maDatBanColumn.setCellValueFactory(new PropertyValueFactory<>("maDatBan"));
        maNVColumn.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        thoiGianTaoColumn.setCellValueFactory(new PropertyValueFactory<>("thoiGianTao"));
        thoiGianThanhToanColumn.setCellValueFactory(new PropertyValueFactory<>("thoiGianThanhToan"));
        doanhThuColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
    }

    /**
     * Tải danh sách đơn hàng từ DB, tính toán doanh thu cho mỗi đơn và hiển thị lên TableView.
     */
    private void loadOrderData() {
        orderViewList.clear(); // Xóa dữ liệu cũ
        List<Order> ordersFromDb = orderDAO.getAllOrders();

        for (Order order : ordersFromDb) {
            BigDecimal revenue = calculateRevenueForOrder(order.getMaDonHang());
            orderViewList.add(new OrderView(order, revenue));
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
            showAlert(Alert.AlertType.WARNING, "Chưa chọn đơn hàng", "Vui lòng chọn một đơn hàng để xóa.");
            return;
        }

        // Tạo hộp thoại xác nhận trước khi xóa
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Xác nhận xóa");
        confirmationAlert.setHeaderText("Bạn có chắc chắn muốn xóa đơn hàng: " + selectedOrder.getMaDonHang() + "?");
        confirmationAlert.setContentText("Hành động này không thể hoàn tác. Tất cả chi tiết liên quan đến đơn hàng cũng sẽ bị xóa.");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String orderId = selectedOrder.getMaDonHang();

            // Xóa tất cả chi tiết đơn hàng trước (quan trọng để không vi phạm ràng buộc khóa ngoại)
            // LƯU Ý: Bạn cần thêm phương thức deleteAllDetailsForOrder vào OrderDetailDAO
            // boolean detailsDeleted = orderDetailDAO.deleteAllDetailsForOrder(orderId);

            // Sau đó, xóa đơn hàng chính
            boolean orderDeleted = orderDAO.deleteOrder(orderId);

            if (orderDeleted) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa đơn hàng thành công.");
                // Tải lại dữ liệu để cập nhật bảng
                loadOrderData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa đơn hàng. Vui lòng thử lại.");
            }
        }
    }

    @FXML
    void handleAddNewOrder(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddOrder.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm Đơn Hàng Mới");
            Scene newScene = new Scene( root, 800, 800);
            stage.setScene(newScene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadOrderData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi Giao Diện", "Không thể mở cửa sổ thêm đơn hàng.");
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