package com.restaurant.restaurant_management_project.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class UInhanvienController {

    @FXML
    private TableView<?> addEmployee_tableView;

    @FXML
    private TableColumn<?, ?> col_Chucvu;

    @FXML
    private TableColumn<?, ?> col_Email;

    @FXML
    private TableColumn<?, ?> col_Hovaten;

    @FXML
    private TableColumn<?, ?> col_Luong;

    @FXML
    private TableColumn<?, ?> col_Manhanvien;

    @FXML
    private TableColumn<?, ?> col_Ngaysinh;

    @FXML
    private TableColumn<?, ?> col_sdt;

    @FXML
    private Button suaBtn;

    @FXML
    private Button themBtn;

    @FXML
    private Button xoaBtn;

    @FXML
    private void initialize() {
        // Xử lý sự kiện khi nhấn nút Thêm
        themBtn.setOnAction(event -> {
            openForm("/fxml/FormThemNhanvien.fxml", "Thêm Nhân Viên");
        });

        // Xử lý sự kiện khi nhấn nút Sửa
        suaBtn.setOnAction(event -> {
            openForm("/fxml/FormSuaNhanvien.fxml", "Sửa Nhân Viên");
        });

        // Xử lý sự kiện khi nhấn nút Xóa
        xoaBtn.setOnAction(event -> {
            openForm("/fxml/FormXoaNhanvien.fxml", "Xác Nhận Xóa");
        });

    }
    // Phương thức chung để mở form
    private void openForm(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // chặn cửa sổ chính khi form đang mở
            stage.showAndWait(); //

        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

        @FXML
    void addEmployeeSelect(MouseEvent event) {
        // Xử lý khi chọn nhân viên trong bảng
        // Có thể enable/disable nút Sửa và Xóa tùy theo selection
        if (addEmployee_tableView.getSelectionModel().getSelectedItem() != null) {
            suaBtn.setDisable(false);
            xoaBtn.setDisable(false);
        } else {
            suaBtn.setDisable(true);
            xoaBtn.setDisable(true);
        }
    }
} 