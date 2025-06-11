package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.BanDAO;
import com.restaurant.restaurant_management_project.dao.DatBanDAO;
import com.restaurant.restaurant_management_project.model.DatBan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class DSDatBanController {

    @FXML private TableView<DatBan> datBanTable;
    @FXML private TableColumn<DatBan, Integer> colMaDatBan;
    @FXML private TableColumn<DatBan, Integer> colMaBan;
    @FXML private TableColumn<DatBan, String> colTenKhachHang;
    @FXML private TableColumn<DatBan, String> colSoDienThoai;
    @FXML private TableColumn<DatBan, java.time.LocalDateTime> colThoiGianDat;
    @FXML private TableColumn<DatBan, Integer> colSoLuongNguoi;
    @FXML private TableColumn<DatBan, String> colGhiChu;
    @FXML private Button btnHuyDatBan;

    private DatBanDAO datBanDAO = new DatBanDAO();
    private BanDAO banDAO = new BanDAO();
    private ObservableList<DatBan> datBanList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadDSDatBan();
        btnHuyDatBan.setDisable(true); // Vô hiệu hóa nút ban đầu
        datBanTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> btnHuyDatBan.setDisable(newSelection == null)
        );
    }

    private void setupTable() {
        colMaDatBan.setCellValueFactory(new PropertyValueFactory<>("maDatBan"));
        colMaBan.setCellValueFactory(new PropertyValueFactory<>("maBan"));
        colTenKhachHang.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        colSoDienThoai.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colThoiGianDat.setCellValueFactory(new PropertyValueFactory<>("thoiGianDat"));
        colSoLuongNguoi.setCellValueFactory(new PropertyValueFactory<>("soLuongNguoi"));
        colGhiChu.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));

        datBanTable.setItems(datBanList);
    }

    private void loadDSDatBan() {
        datBanList.setAll(datBanDAO.getDSDatBan());
    }

    @FXML
    private void huyDatBan() {
        DatBan selected = datBanTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn một đặt bàn để hủy!");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận hủy đặt bàn");
        confirmDialog.setHeaderText("Bạn có chắc chắn muốn hủy đặt bàn này?");
        confirmDialog.setContentText("Đặt bàn của " + selected.getTenKhachHang() + " sẽ bị hủy.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (datBanDAO.huyDatBan(selected.getMaDatBan())) {
                // Cập nhật trạng thái bàn thành "Trống"
                banDAO.capNhatTrangThai(selected.getMaBan(), "Trống");
                loadDSDatBan(); // Làm mới danh sách
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Hủy đặt bàn thành công!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể hủy đặt bàn!");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}