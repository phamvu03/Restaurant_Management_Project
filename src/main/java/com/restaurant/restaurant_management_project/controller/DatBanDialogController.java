package com.restaurant.restaurant_management_project.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DatBanDialogController {
    @FXML private TextField txtTenKH;
    @FXML private TextField txtSoDT;
    @FXML private Spinner<Integer> spSoLuong;
    @FXML private ComboBox<Integer> cbGio;
    @FXML private ComboBox<Integer> cbPhut;
    @FXML private TextArea taGhiChu;

    private Stage dialogStage;
    private boolean datBanClicked = false;

    @FXML
    private void initialize() {
        // Thiết lập giá trị mặc định cho Spinner
        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        spSoLuong.setValueFactory(valueFactory);

        // Thiết lập giá trị cho ComboBox giờ và phút
        for (int i = 7; i <= 22; i++) { // Giờ từ 7h sáng đến 10h tối
            cbGio.getItems().add(i);
        }
        cbGio.setValue(12); // Giờ mặc định 12h

        for (int i = 0; i < 60; i += 15) { // Phút cách nhau 15 phút (0, 15, 30, 45)
            cbPhut.getItems().add(i);
        }
        cbPhut.setValue(0);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isDatBanClicked() {
        return datBanClicked;
    }

    @FXML
    private void handleDatBan() {
        if (isInputValid()) {
            datBanClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (txtTenKH.getText() == null || txtTenKH.getText().isEmpty()) {
            errorMessage += "Vui lòng nhập tên khách hàng!\n";
        }

        if (txtSoDT.getText() == null || txtSoDT.getText().isEmpty()) {
            errorMessage += "Vui lòng nhập số điện thoại!\n";
        }

        if (cbGio.getValue() == null || cbPhut.getValue() == null) {
            errorMessage += "Vui lòng chọn giờ đến!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Lỗi nhập liệu");
            alert.setHeaderText("Vui lòng nhập đầy đủ thông tin:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    // Các phương thức getter để lấy dữ liệu từ dialog
    public String getTenKhachHang() {
        return txtTenKH.getText();
    }

    public String getSoDienThoai() {
        return txtSoDT.getText();
    }

    public int getSoLuongNguoi() {
        return spSoLuong.getValue();
    }

    public String getGhiChu() {
        return taGhiChu.getText();
    }
}