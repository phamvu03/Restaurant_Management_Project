package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.BanDAO;
import com.restaurant.restaurant_management_project.model.Ban;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class QLBanController {
    @FXML private TableView<Ban> banTable;
    @FXML private TableColumn<Ban, Integer> colMaBan;
    @FXML private TableColumn<Ban, String> colTenBan;
    @FXML private TableColumn<Ban, String> colViTri;
    @FXML private TableColumn<Ban, String> colTrangThai;
    @FXML private TableColumn<Ban, Integer> colSoCho;

    private BanDAO banDAO = new BanDAO();
    private ObservableList<Ban> banList = FXCollections.observableArrayList();
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadDSBan();
    }

    private void setupTable() {
        colMaBan.setCellValueFactory(new PropertyValueFactory<>("maBan"));
        colTenBan.setCellValueFactory(new PropertyValueFactory<>("tenBan"));
        colViTri.setCellValueFactory(new PropertyValueFactory<>("viTri"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colSoCho.setCellValueFactory(new PropertyValueFactory<>("soChoNgoi"));

        banTable.setItems(banList);
    }

    private void loadDSBan() {
        banList.setAll(banDAO.getDSBan());
    }

    // Trong QLBanController.java
    @FXML
    private void themBan() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ThemBanDialog.fxml"));
            DialogPane dialogPane = loader.load();

            BanDialogController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Thêm bàn mới");
            dialog.initOwner(primaryStage);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Ban newBan = controller.getBan();
                if (banDAO.themBan(newBan)) {
                    loadDSBan();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công",
                            "Thêm bàn mới thành công! Mã bàn: " + newBan.getMaBan());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể thêm bàn mới");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải form thêm bàn");
        }
    }
    @FXML
    private void suaBan() {
        Ban selected = banTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn bàn cần sửa");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SuaBanDialog.fxml"));
            DialogPane dialogPane = loader.load();

            BanDialogController controller = loader.getController();
            controller.setBan(selected);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Sửa thông tin bàn");
            dialog.initOwner(primaryStage);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Ban updatedBan = controller.getBan();
                if (banDAO.suaBan(updatedBan)) {
                    loadDSBan();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin bàn thành công!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải form sửa bàn");
        }
    }
    @FXML
    private void xoaBan() {
        Ban selected = banTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn bàn cần xóa");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Bạn có chắc chắn muốn xóa bàn này?");
        confirmDialog.setContentText("Bàn " + selected.getTenBan() + " sẽ bị xóa vĩnh viễn.");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (banDAO.xoaBan(selected.getMaBan())) {
                loadDSBan();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa bàn thành công!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa bàn");
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
}