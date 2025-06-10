/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.EquipmentDAO;
import com.restaurant.restaurant_management_project.model.Equipment;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

import com.restaurant.restaurant_management_project.util.enums.EquipmentCategory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class AddEquipmentController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private Button addBtn;

    @FXML
    private Button cancelBtn;

    @FXML
    private DatePicker date;

    @FXML
    private TextField nameTxt;

    @FXML
    private TextField quantityTxt;

    @FXML
    private ComboBox<EquipmentCategory> cbxStatus;

    @FXML
    private TextField typeTxt;
    @FXML
    private Label nameErrorLabel;
    @FXML
    private Label typeErrorLabel;
    @FXML
    private Label quantityErrorLabel;
    @FXML
    private Label statusErrorLabel;
    @FXML
    private Label dateErrorLabel;

    private EquipmentController equipmentController;

    public void setEquipmentController(EquipmentController controller) {
        this.equipmentController = controller;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbxStatus.setItems(FXCollections.observableArrayList(EquipmentCategory.values()));

        cbxStatus.setConverter(new StringConverter<EquipmentCategory>() {
            @Override
            public String toString(EquipmentCategory category) {
                return category != null ? category.getDisplayName() : "";
            }

            @Override
            public EquipmentCategory fromString(String string) {
                return null;
            }
        });

        cbxStatus.getSelectionModel().selectFirst();

        addBtn.setOnAction(e -> handleAddBtnAction());
        cancelBtn.setOnAction(e -> handleCancelBtnAction());

        nameTxt.textProperty().addListener((obs, oldVal, newVal) -> nameErrorLabel.setVisible(false));
        typeTxt.textProperty().addListener((obs, oldVal, newVal) -> typeErrorLabel.setVisible(false));
        quantityTxt.textProperty().addListener((obs, oldVal, newVal) -> quantityErrorLabel.setVisible(false));
        cbxStatus.valueProperty().addListener((obs, oldVal, newVal) -> statusErrorLabel.setVisible(false));
        date.valueProperty().addListener((obs, oldVal, newVal) -> dateErrorLabel.setVisible(false));
    }

    private void handleAddBtnAction() {
        // Reset tất cả các thông báo lỗi trước khi validate lại
        hideAllErrorLabels();

        String name = nameTxt.getText();
        String type = typeTxt.getText();
        String quantityStr = quantityTxt.getText();
        EquipmentCategory selectedCategory = cbxStatus.getValue();
        Date reportDate = date.getValue() != null ? Date.valueOf(date.getValue()) : null;

        boolean isValid = true;

        // Kiểm tra ràng buộc Tên
        if (name.isEmpty()) {
            showErrorLabel(nameErrorLabel, "Vui lòng nhập Tên dụng cụ.");
            isValid = false;
        }

        // Kiểm tra ràng buộc Loại
        if (type.isEmpty()) {
            showErrorLabel(typeErrorLabel, "Vui lòng nhập Loại dụng cụ.");
            isValid = false;
        }

        // Kiểm tra ràng buộc Số lượng
        int quantity = 0;
        if (quantityStr.isEmpty()) {
            showErrorLabel(quantityErrorLabel, "Vui lòng nhập Số lượng.");
            isValid = false;
        } else {
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) { // Thêm kiểm tra số lượng phải lớn hơn 0
                    showErrorLabel(quantityErrorLabel, "Số lượng phải là số nguyên dương.");
                    isValid = false;
                }
            } catch (NumberFormatException ex) {
                showErrorLabel(quantityErrorLabel, "Số lượng phải là một số nguyên.");
                isValid = false;
            }
        }

        // Kiểm tra ràng buộc Trạng thái
        if (selectedCategory == null) {
            showErrorLabel(statusErrorLabel, "Vui lòng chọn Tình trạng.");
            isValid = false;
        }

        // Kiểm tra ràng buộc Ngày thống kê (tùy chọn, nếu bạn muốn ngày không được để trống)
        if (reportDate == null) {
            showErrorLabel(dateErrorLabel, "Vui lòng chọn Ngày thống kê.");
            isValid = false;
        }

        if (!isValid) {
            // Hiển thị Alert tổng quát nếu có quá nhiều lỗi hoặc muốn thông báo chung
            // showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ và chính xác các thông tin.");
            return; // Dừng lại nếu có lỗi
        }

        String status = selectedCategory.getDisplayName(); // Lấy displayName sau khi đã validate

        Equipment equip = new Equipment();
        equip.setTenDungCu(name);
        equip.setLoai(type);
        equip.setSoLuong(quantity);
        equip.setTinhTrang(status);
        equip.setNgayThongKe(reportDate);

        EquipmentDAO dao = new EquipmentDAO();
        boolean success = dao.addEquipment(equip);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm dụng cụ thành công!");
            if (equipmentController != null) {
                equipmentController.loadDataFromDatabase();
            }
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể thêm dụng cụ.");
        }
    }

    private void handleCancelBtnAction() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    private void showErrorLabel(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void hideErrorLabel(Label errorLabel) {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
    private void hideAllErrorLabels() {
        hideErrorLabel(nameErrorLabel);
        hideErrorLabel(typeErrorLabel);
        hideErrorLabel(quantityErrorLabel);
        hideErrorLabel(statusErrorLabel);
        hideErrorLabel(dateErrorLabel);
    }
}