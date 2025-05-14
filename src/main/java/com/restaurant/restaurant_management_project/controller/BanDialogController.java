package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.Ban;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BanDialogController {
    @FXML private TextField txtMaBan;
    @FXML private TextField txtTenBan;
    @FXML private ComboBox<String> cbViTri;
    @FXML private Spinner<Integer> spSoCho;
    @FXML private ComboBox<String> cbTrangThai;

    private boolean isEditMode = false;
    private Ban ban;

    @FXML
    public void initialize() {
        // Khởi tạo ComboBox vị trí
        ObservableList<String> viTriList = FXCollections.observableArrayList(
                "Tầng 1", "Tầng 2", "Tầng 3"
        );
        cbViTri.setItems(viTriList);

        // Khởi tạo Spinner số chỗ
        spSoCho.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 4));

        // Khởi tạo ComboBox trạng thái
        ObservableList<String> trangThaiList = FXCollections.observableArrayList(
                "Trống", "Đang dùng", "Đã đặt"
        );
        cbTrangThai.setItems(trangThaiList);
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        txtMaBan.setDisable(isEditMode); // Không cho sửa mã bàn trong chế độ sửa
    }

    public void setBan(Ban ban) {
        this.ban = ban;
        if (ban != null) {
            txtMaBan.setText(String.valueOf(ban.getMaBan()));
            txtTenBan.setText(ban.getTenBan());
            cbViTri.setValue(ban.getViTri());
            spSoCho.getValueFactory().setValue(ban.getSoChoNgoi());
            cbTrangThai.setValue(ban.getTrangThai());
        }
    }

    public Ban getBan() {
        if (isEditMode && ban != null) {
            // Chế độ sửa: cập nhật thông tin bàn hiện có
            ban.setTenBan(txtTenBan.getText());
            ban.setViTri(cbViTri.getValue());
            ban.setSoChoNgoi(spSoCho.getValue());
            ban.setTrangThai(cbTrangThai.getValue());
            return ban;
        } else {
            // Chế độ thêm: tạo bàn mới
            Ban newBan = new Ban();
            try {
                newBan.setMaBan(Integer.parseInt(txtMaBan.getText()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Mã bàn phải là số nguyên");
            }
            newBan.setTenBan(txtTenBan.getText());
            newBan.setViTri(cbViTri.getValue());
            newBan.setSoChoNgoi(spSoCho.getValue());
            newBan.setTrangThai(cbTrangThai.getValue());
            return newBan;
        }
    }
}