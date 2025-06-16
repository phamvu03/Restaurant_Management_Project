package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.Ban;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DatBanDialogController {
    @FXML private Label lbBanInfo;
    @FXML private TextField tfTenKhachHang;
    @FXML private TextField tfSoDienThoai;
    @FXML private TextField tfEmail;
    @FXML private Spinner<Integer> spSoKhach;
    @FXML private DatePicker dpNgayDat; // Thêm DatePicker cho ngày đặt
    @FXML private Spinner<Integer> spGio; // Thêm Spinner cho giờ
    @FXML private Spinner<Integer> spPhut; // Thêm Spinner cho phút
    @FXML private TextArea taGhiChu;
    @FXML private Button btnDatBan;
    @FXML private Button btnHuy;

    private Stage dialogStage;
    private boolean datBanClicked = false;
    private Ban ban;

    @FXML
    private void initialize() {
        // Thiết lập Spinner cho số khách
        spSoKhach.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        spSoKhach.setEditable(true);

        // Thiết lập DatePicker cho ngày đặt (mặc định là hôm nay)
        dpNgayDat.setValue(LocalDate.now());
        
        // Thiết lập Spinner cho giờ (6:00 - 23:00)
        spGio.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 23, LocalTime.now().getHour()));
        spGio.setEditable(true);
        
        // Thiết lập Spinner cho phút (0, 15, 30, 45)
        SpinnerValueFactory<Integer> phutFactory = new SpinnerValueFactory<Integer>() {
            {
                setValue(roundToNearestQuarter(LocalTime.now().getMinute()));
            }
            
            @Override
            public void decrement(int steps) {
                int currentValue = getValue();
                int newValue = currentValue - 15;
                if (newValue < 0) {
                    newValue = 45;
                }
                setValue(newValue);
            }
            
            @Override
            public void increment(int steps) {
                int currentValue = getValue();
                int newValue = currentValue + 15;
                if (newValue > 45) {
                    newValue = 0;
                }
                setValue(newValue);
            }
        };
        spPhut.setValueFactory(phutFactory);
        spPhut.setEditable(false);

        // Validation cho số điện thoại
        tfSoDienThoai.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfSoDienThoai.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Validation cho email
        tfEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });

        tfTenKhachHang.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });

        tfSoDienThoai.textProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });

        // Validation cho ngày giờ
        dpNgayDat.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });

        spGio.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateForm();
        });
    }
    
    // Làm tròn phút về 15 phút gần nhất
    private int roundToNearestQuarter(int minute) {
        if (minute < 8) return 0;
        else if (minute < 23) return 15;
        else if (minute < 38) return 30;
        else if (minute < 53) return 45;
        else return 0;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
        if (ban != null) {
            lbBanInfo.setText("Bàn: " + ban.getMaBan() + " - Số ghế: " + ban.getSoGhe());
            spSoKhach.getValueFactory().setValue(Math.min(ban.getSoGhe(), 1));
            ((SpinnerValueFactory.IntegerSpinnerValueFactory) spSoKhach.getValueFactory())
                .setMax(ban.getSoGhe());
        }
    }

    @FXML
    private void handleDatBan() {
        if (isInputValid()) {
            datBanClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleHuy() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (tfTenKhachHang.getText() == null || tfTenKhachHang.getText().trim().isEmpty()) {
            errorMessage += "Tên khách hàng không được để trống!\n";
        }

        if (tfSoDienThoai.getText() == null || tfSoDienThoai.getText().trim().isEmpty()) {
            errorMessage += "Số điện thoại không được để trống!\n";
        } else if (tfSoDienThoai.getText().length() < 10) {
            errorMessage += "Số điện thoại phải có ít nhất 10 chữ số!\n";
        }

        if (tfEmail.getText() != null && !tfEmail.getText().trim().isEmpty()) {
            if (!isValidEmail(tfEmail.getText())) {
                errorMessage += "Email không hợp lệ!\n";
            }
        }

        if (spSoKhach.getValue() <= 0) {
            errorMessage += "Số khách phải lớn hơn 0!\n";
        }

        if (dpNgayDat.getValue() == null) {
            errorMessage += "Vui lòng chọn ngày đặt!\n";
        } else if (dpNgayDat.getValue().isBefore(LocalDate.now())) {
            errorMessage += "Ngày đặt không thể là ngày trong quá khứ!\n";
        }

        if (spGio.getValue() == null) {
            errorMessage += "Vui lòng chọn giờ đặt!\n";
        }

        // Kiểm tra thời gian đặt không được trong quá khứ
        if (dpNgayDat.getValue() != null && spGio.getValue() != null && spPhut.getValue() != null) {
            LocalDateTime thoiGianDat = LocalDateTime.of(
                dpNgayDat.getValue(),
                LocalTime.of(spGio.getValue(), spPhut.getValue())
            );
            
            if (thoiGianDat.isBefore(LocalDateTime.now())) {
                errorMessage += "Thời gian đặt không thể là thời điểm trong quá khứ!\n";
            }
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

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void validateForm() {
        boolean isValid = tfTenKhachHang.getText() != null && !tfTenKhachHang.getText().trim().isEmpty() &&
                         tfSoDienThoai.getText() != null && tfSoDienThoai.getText().length() >= 10 &&
                         dpNgayDat.getValue() != null && spGio.getValue() != null;
        btnDatBan.setDisable(!isValid);
    }

    // Getters
    public boolean isDatBanClicked() {
        return datBanClicked;
    }

    public String getTenKhachHang() {
        return tfTenKhachHang.getText();
    }

    public String getSoDienThoai() {
        return tfSoDienThoai.getText();
    }

    public String getEmail() {
        return tfEmail.getText();
    }

    public int getSoKhach() {
        return spSoKhach.getValue();
    }

    public String getGhiChu() {
        return taGhiChu.getText();
    }

    // Thêm getter cho thời gian đặt
    public LocalDateTime getThoiGianDat() {
        if (dpNgayDat.getValue() != null && spGio.getValue() != null && spPhut.getValue() != null) {
            return LocalDateTime.of(
                dpNgayDat.getValue(),
                LocalTime.of(spGio.getValue(), spPhut.getValue())
            );
        }
        return LocalDateTime.now(); // Fallback
    }
}