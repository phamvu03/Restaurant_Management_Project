package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.DatBanDAO;
import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.model.DatBan;
import com.restaurant.restaurant_management_project.model.DonHang;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AddOrderController {
    @FXML
    private TextField tenKhachField;
    @FXML
    private TextField soKhachField;
    @FXML
    private TextField sdtField;
    @FXML
    private Label thoiGianTaoLabel;
    @FXML
    private ComboBox<String> banComboBox;

    private DatBanDAO datBanDAO;
    private OrderDAO OrderDAO;
    private EmployeeDAO EmployeeDAO;
    private LocalDateTime thoiGianTao;

    @FXML
    public void initialize() {
        datBanDAO = new DatBanDAO();
        OrderDAO = new OrderDAO();
        EmployeeDAO = new EmployeeDAO();

        // Set thời gian tạo mặc định
        thoiGianTao = LocalDateTime.now();
        thoiGianTaoLabel.setText(thoiGianTao.toString());

        // Load danh sách bàn có sẵn
        loadAvailableTables();
    }

    private void loadAvailableTables() {
        List<DatBan> availableTables = datBanDAO.getDSDatBan().stream()
                .filter(table -> "Available".equals(table.getTrangThai()))
                .collect(Collectors.toList());

        banComboBox.getItems().clear();
        for (DatBan table : availableTables) {
            banComboBox.getItems().add(table.getMaBan());
        }
    }

    @FXML
    private void handleSave() {
        // Validate required fields
        if (soKhachField.getText().isEmpty() || sdtField.getText().isEmpty() || banComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Vui lòng điền đầy đủ thông tin bắt buộc!");
            return;
        }

        try {
            // Validate số khách
            int soKhach = Integer.parseInt(soKhachField.getText());
            if (soKhach <= 0) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số khách phải lớn hơn 0!");
                return;
            }

            // Validate số điện thoại
            String sdt = sdtField.getText();
            if (!sdt.matches("\\d{10,11}")) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại không hợp lệ!");
                return;
            }

            // Lấy mã nhân viên Admin
            String maNV = EmployeeDAO.getNhanVienByTen("Admin").getMaNV();

            // Tạo đơn hàng mới
            DonHang donHang = new DonHang();
            donHang.setMaNV(maNV);
            donHang.setThoiGianTao(Date.valueOf(thoiGianTao.toLocalDate()));
            donHang.setMaDatBan(banComboBox.getValue());
            donHang.setTenKhach(tenKhachField.getText().isEmpty() ? null : tenKhachField.getText());
            donHang.setSoKhach(soKhach);
            donHang.setSdtKhach(sdt);

            // Lưu đơn hàng
            boolean success = OrderDAO.addDonHang(donHang);

            if (success) {
                // Cập nhật trạng thái bàn
                DatBan datBan = datBanDAO.getDatBanByMaBan(banComboBox.getValue());
                datBan.setTrangThai("Occupied");
                datBanDAO.updateDatBan(datBan);

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm đơn hàng thành công!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm đơn hàng!");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Số khách phải là số!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Có lỗi xảy ra: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) tenKhachField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 