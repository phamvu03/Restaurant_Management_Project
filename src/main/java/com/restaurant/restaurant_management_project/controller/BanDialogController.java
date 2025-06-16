package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.Ban;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class BanDialogController {
    @FXML private TextField txtMaBan; // Chỉ hiển thị, không cho nhập
    @FXML private ComboBox<String> cbViTri;
    @FXML private Spinner<Integer> spSoGhe; // Sửa từ spSoCho thành spSoGhe
    @FXML private TextArea taGhiChu; // Thêm TextArea cho ghi chú

    private boolean isEditMode = false;
    private Ban ban;

    @FXML
    public void initialize() {
        // Khởi tạo ComboBox vị trí
        ObservableList<String> viTriList = FXCollections.observableArrayList(
                "Tầng 1", "Tầng 2", "Tầng 3", "Sân thượng", "Khu VIP"
        );
        cbViTri.setItems(viTriList);

        // Khởi tạo Spinner số ghế
        spSoGhe.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 4));
        spSoGhe.setEditable(true);

        // Mã bàn sẽ được tự động sinh bởi database
        txtMaBan.setDisable(true);
        txtMaBan.setPromptText("Sẽ được tự động sinh");
    }

    public void setEditMode(boolean isEditMode) {
        this.isEditMode = isEditMode;
        if (isEditMode) {
            txtMaBan.setText(""); // Xóa prompt text trong chế độ sửa
        }
    }

    public void setBan(Ban ban) {
        this.ban = ban;
        if (ban != null) {
            txtMaBan.setText(ban.getMaBan());
            cbViTri.setValue(ban.getViTri());
            spSoGhe.getValueFactory().setValue(ban.getSoGhe());
            taGhiChu.setText(ban.getGhiChu());
        }
    }

    public Ban getBan() {
        if (isEditMode && ban != null) {
            // Chế độ sửa: cập nhật thông tin bàn hiện có
            ban.setViTri(cbViTri.getValue());
            ban.setSoGhe(spSoGhe.getValue());
            ban.setGhiChu(taGhiChu.getText());
            return ban;
        } else {
            // Chế độ thêm: tạo bàn mới (maBan sẽ được database tự sinh)
            Ban newBan = new Ban();
            newBan.setViTri(cbViTri.getValue());
            newBan.setSoGhe(spSoGhe.getValue());
            newBan.setGhiChu(taGhiChu.getText());
            return newBan;
        }
    }

    public boolean isValidInput() {
        String errorMessage = "";

        if (cbViTri.getValue() == null || cbViTri.getValue().trim().isEmpty()) {
            errorMessage += "Vui lòng chọn vị trí bàn!\n";
        }

        if (spSoGhe.getValue() == null || spSoGhe.getValue() <= 0) {
            errorMessage += "Số ghế phải lớn hơn 0!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Thông tin không hợp lệ");
            alert.setHeaderText("Vui lòng kiểm tra lại thông tin");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
}