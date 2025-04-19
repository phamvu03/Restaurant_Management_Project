package com.restaurant.restaurant_management_project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class FormThemNhanvienController {

    @FXML
    private Button lamMoiBtn;

    @FXML
    private ChoiceBox<?> nhapChucvu;

    @FXML
    private TextField nhapEmail;

    @FXML
    private TextField nhapHovaten;

    @FXML
    private TextField nhapLuong;

    @FXML
    private TextField nhapManhanvien;

    @FXML
    private DatePicker nhapNgaysinh;

    @FXML
    private TextField nhapSdt;

    @FXML
    private Button themBtn;

    @FXML
    private void initialize() {
        // Xử lý sự kiện nút Thêm
        themBtn.setOnAction(event -> {

        });


        // Xử lý sự kiện nút Làm mới
        lamMoiBtn.setOnAction(event -> {
            clearFields();
        });
    }

    private void clearFields() {
        nhapManhanvien.clear();
        nhapHovaten.clear();
        nhapNgaysinh.setValue(null);
        nhapLuong.clear();
        nhapSdt.clear();
        nhapChucvu.getSelectionModel().clearSelection();
        nhapEmail.clear();
    }

    private void closeWindow() {
        Stage stage = (Stage) themBtn.getScene().getWindow();
        stage.close();
    }
}
