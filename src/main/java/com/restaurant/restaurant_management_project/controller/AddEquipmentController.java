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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private TextField idTxt;
    
    @FXML
    private TextField nameTxt;

    @FXML
    private TextField quantityTxt;

    @FXML
    private TextField statusTxt;

    @FXML
    private TextField typeTxt;
    
    private EquipmentController equipmentController;

    public void setEquipmentController(EquipmentController controller) {
        this.equipmentController = controller;
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addBtn.setOnAction(e -> handleAddBtnAction());
        cancelBtn.setOnAction(e -> handleCancelBtnAction());
    }   
    
    private void handleAddBtnAction() {
        String id = idTxt.getText();
        String name = nameTxt.getText();
        String type = typeTxt.getText();
        String quantityStr = quantityTxt.getText();
        String status = statusTxt.getText();
        Date reportDate = date.getValue() != null ? Date.valueOf(date.getValue()) : null;

        // Kiểm tra ràng buộc
        if (name.isEmpty() || type.isEmpty() || quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Tên, Loại và Số lượng.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Số lượng không hợp lệ", "Số lượng phải là một số nguyên.");
            return;
        }

        Equipment equip = new Equipment();
        equip.setMaDungCu(id);
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }    
}
