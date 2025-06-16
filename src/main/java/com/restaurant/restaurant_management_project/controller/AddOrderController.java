package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.DatBanDAO;
import com.restaurant.restaurant_management_project.dao.OrderDAO;
import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.model.DatBan;
import com.restaurant.restaurant_management_project.model.Order;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // Set thời gian tạo mặc định với định dạng
        thoiGianTao = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        thoiGianTaoLabel.setText(thoiGianTao.format(formatter));

        // Load danh sách bàn có sẵn
        loadAvailableTables();
    }

    private void loadAvailableTables() {
        try {
            LocalDateTime currentTime = LocalDateTime.now();
            List<DatBan> availableTables = datBanDAO.getDSDatBan().stream()
                    .filter(table -> {
                        try {
                            boolean validStatus = "ban-trong".equals(table.getTrangThai()) || table.getTrangThai() == null;
                            
                            // Kiểm tra thời gian đặt bàn
                            boolean validTime = true;
                            if (table.getThoiGianDat() != null) {
                                // Chuyển đổi từ java.sql.Date sang LocalDateTime
                                LocalDateTime reservationTime = table.getThoiGianDat()
                                    .toLocalDate()
                                    .atStartOfDay();
                                validTime = reservationTime.isBefore(currentTime);
                            }
                            
                            return validStatus && validTime;
                        } catch (Exception e) {
                            System.err.println("Lỗi khi xử lý bàn " + table.getMaBan() + ": " + e.getMessage());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

            banComboBox.getItems().clear();
            for (DatBan table : availableTables) {
                if (table.getMaBan() != null) {
                    banComboBox.getItems().add(table.getMaBan());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải danh sách bàn: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách bàn: " + e.getMessage());
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
            String maNV = EmployeeDAO.getAllEmployee().stream()
                    .filter(emp -> "Admin".equals(emp.getTenNV()))
                    .findFirst()
                    .map(emp -> emp.getMaNV())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên Admin"));

            // Tạo đơn hàng mới
            Order donHang = new Order();
            DatBan datBan = new DatBan();
            donHang.setMaNV(maNV);
            donHang.setThoiGianTao(Date.valueOf(thoiGianTao.toLocalDate()));
            donHang.setMaDatBan(banComboBox.getValue());
            datBan.setTenKhachHang(tenKhachField.getText().isEmpty() ? null : tenKhachField.getText());
            datBan.setSoKhach(soKhach);
            datBan.setSoDienThoai(sdt);

            // Lưu đơn hàng
            boolean orderSuccess = OrderDAO.addOrder(donHang);
            boolean reservSuccess = datBanDAO.themDatBan(datBan);

            if (orderSuccess && reservSuccess) {
                // Cập nhật trạng thái bàn
                String datBanId = datBanDAO.getDSDatBan().stream()
                        .filter(table -> banComboBox.getValue().equals(table.getMaBan()))
                        .findFirst()
                        .map(DatBan::getMaDatBan)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bàn"));
                datBanDAO.capNhatTrangThai(datBanId, "dang-su-dung");

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