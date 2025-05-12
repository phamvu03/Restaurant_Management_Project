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
 *
 * @author admin
 */
public class UpdateEquipmentController implements Initializable {
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

    @FXML
    private Button updateBtn;
    
    private Equipment currentEquipment;
    private EquipmentController equipmentController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        updateBtn.setOnAction(e -> handleUpdate());
        cancelBtn.setOnAction(e -> closeWindow());
    }
    
    public void setEquipment(Equipment equip) {
        this.currentEquipment = equip;

        // Gán dữ liệu vào các trường
        idTxt.setText(equip.getMaDungCu());
        idTxt.setDisable(true); // không cho sửa ID

        nameTxt.setText(equip.getTenDungCu());
        typeTxt.setText(equip.getLoai());
        quantityTxt.setText(String.valueOf(equip.getSoLuong()));
        statusTxt.setText(equip.getTinhTrang());
        if (equip.getNgayThongKe() != null) {
            date.setValue(equip.getNgayThongKe().toLocalDate());
        }
    }
    public void setEquipmentController(EquipmentController controller) {
        this.equipmentController = controller;
    }
    
    private void handleUpdate() {
        String name = nameTxt.getText();
        String type = typeTxt.getText();
        String quantityStr = quantityTxt.getText();
        String status = statusTxt.getText();
        Date reportDate = (date.getValue() != null) ? Date.valueOf(date.getValue()) : null;

        if (name.isEmpty() || type.isEmpty() || quantityStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Tên, Loại và Số lượng.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Sai định dạng", "Số lượng phải là số nguyên.");
            return;
        }

        // Cập nhật dữ liệu
        Equipment updated = new Equipment();
        updated.setMaDungCu(currentEquipment.getMaDungCu());
        updated.setTenDungCu(name);
        updated.setLoai(type);
        updated.setSoLuong(quantity);
        updated.setTinhTrang(status);
        updated.setNgayThongKe(reportDate);

        EquipmentDAO dao = new EquipmentDAO();
        boolean success = dao.updateEquipment(updated);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thành công!");
            if (equipmentController != null) {
                equipmentController.loadDataFromDatabase();
            }
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật dụng cụ.");
        }
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
