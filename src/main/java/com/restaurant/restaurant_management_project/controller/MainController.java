package com.restaurant.restaurant_management_project.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentPane;
    @FXML private Button btnDatBan;
    @FXML private Button btnQLBan;
    @FXML private Button btnThongKe;
    @FXML private Button btnDSDatBan;
    @FXML private Button btnDangXuat;

    @FXML
    public void initialize() {
        // Load giao diện "Đặt bàn" mặc định
        loadView("/fxml/DatBanGUI.fxml");
    }

    @FXML
    private void loadDatBanView() {
        loadView("/fxml/DatBanGUI.fxml");
    }

    @FXML
    private void loadQLBanView() {
        loadView("/fxml/QLBanGUI.fxml");
    }

    @FXML
    private void loadThongKeView() {
        loadView("/fxml/ThongKeGUI.fxml");
    }

    @FXML
    private void loadDSDatBanView() {
        loadView("/fxml/DSDatBanGUI.fxml");
    }

    @FXML
    private void handleDangXuat() {
        // Logic cho đăng xuất (có thể đóng ứng dụng hoặc chuyển đến màn hình đăng nhập)

        // Ví dụ: Đóng ứng dụng
        System.exit(0);
    }

    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải giao diện");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}