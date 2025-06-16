package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.BanDAO;
import com.restaurant.restaurant_management_project.dao.DatBanDAO;
import com.restaurant.restaurant_management_project.model.Ban;
import com.restaurant.restaurant_management_project.model.DatBan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DatBanController {
    @FXML private TabPane tabPane;
    private BanDAO banDAO = new BanDAO();
    private DatBanDAO datBanDAO = new DatBanDAO();

    private static final String TABLE_IMAGE_PATH = "/image/ban.png";

    @FXML
    public void initialize() {
        loadDSBanTheoTang();
    }

    private void loadDSBanTheoTang() {
        // Xóa các tab cũ trước khi tải lại
        tabPane.getTabs().clear();
        
        List<Ban> dsBan = banDAO.getDSBan();

        // Nhóm bàn theo tầng
        List<String> dsTang = dsBan.stream()
                .map(Ban::getViTri)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (String tang : dsTang) {
            Tab tab = new Tab(tang);
            GridPane grid = createBanGridForTang(dsBan, tang);
            tab.setContent(grid);
            tabPane.getTabs().add(tab);
        }
    }

    private GridPane createBanGridForTang(List<Ban> dsBan, String tang) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getStyleClass().add("ban-grid");

        List<Ban> banTrongTang = dsBan.stream()
                .filter(ban -> ban.getViTri().equals(tang))
                .collect(Collectors.toList());

        int col = 0;
        int row = 0;
        final int MAX_COLS = 5;

        for (Ban ban : banTrongTang) {
            VBox banBox = createBanBox(ban);
            grid.add(banBox, col, row);

            col++;
            if (col >= MAX_COLS) {
                col = 0;
                row++;
            }
        }

        return grid;
    }

    private VBox createBanBox(Ban ban) {
        VBox box = new VBox(5);
        box.getStyleClass().add("ban-box");
        
        // Lấy trạng thái bàn từ database
        String trangThai = banDAO.getTrangThaiBan(ban.getMaBan());
        
        // Cập nhật style class dựa trên trạng thái
        String trangThaiClass = getTrangThaiStyleClass(trangThai);
        box.getStyleClass().add(trangThaiClass);

        // Tạo ImageView với ảnh bàn
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(TABLE_IMAGE_PATH));
            imageView.setImage(image);
        } catch (Exception e) {
            // Nếu không tải được ảnh, tạo placeholder
            imageView = new ImageView();
        }
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        Label lbTen = new Label(ban.getMaBan());
        lbTen.getStyleClass().add("ban-ten");

        Label lbTrangThai = new Label(trangThai);
        lbTrangThai.getStyleClass().add("ban-trang-thai");

        Label lbSoGhe = new Label("Số ghế: " + ban.getSoGhe());
        lbSoGhe.getStyleClass().add("ban-so-ghe");

        Button btnDatBan = new Button("ĐẶT BÀN");
        btnDatBan.getStyleClass().add("btn-dat-ban");
        btnDatBan.setDisable(!trangThai.equalsIgnoreCase("Trống"));

        btnDatBan.setOnAction(e -> showDatBanDialog(ban));

        box.getChildren().addAll(imageView, lbTen, lbTrangThai, lbSoGhe, btnDatBan);
        return box;
    }

    private String getTrangThaiStyleClass(String trangThai) {
        switch (trangThai.toLowerCase()) {
            case "trống":
                return "ban-trong";
            case "đang dùng":
                return "ban-dang-dung";
            case "đã đặt":
                return "ban-da-dat";
            default:
                return "ban-trong";
        }
    }

    private void showDatBanDialog(Ban ban) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/DatBanDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Đặt bàn " + ban.getMaBan());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tabPane.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            DatBanDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setBan(ban); // Truyền thông tin bàn

            dialogStage.showAndWait();

            if (controller.isDatBanClicked()) {
                xuLyDatBan(ban, controller);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Không thể tải form đặt bàn", e.getMessage());
        }
    }

    private void xuLyDatBan(Ban ban, DatBanDialogController controller) {
        try {
            String tenKhachHang = controller.getTenKhachHang();
            String soDienThoai = controller.getSoDienThoai();
            String email = controller.getEmail();
            int soKhach = controller.getSoKhach();
            String ghiChu = controller.getGhiChu();
            LocalDateTime thoiGianDat = controller.getThoiGianDat(); // Sử dụng thời gian từ dialog

            // Kiểm tra số khách không vượt quá số ghế
            if (soKhach > ban.getSoGhe()) {
                showWarningAlert("Cảnh báo", 
                    "Số khách (" + soKhach + ") vượt quá số ghế của bàn (" + ban.getSoGhe() + ")");
                return;
            }

            DatBan datBan = new DatBan(
                ban.getMaBan(), 
                tenKhachHang, 
                soDienThoai,
                thoiGianDat, // Thời gian từ dialog thay vì LocalDateTime.now()
                soKhach,
                ghiChu
            );
            datBan.setEmail(email);

            if (datBanDAO.themDatBan(datBan)) {
                // Cập nhật trạng thái bàn
                if (banDAO.capNhatTrangThai(ban.getMaBan(), "Đã đặt")) {
                    showSuccessAlert("Thành công", 
                        "Đặt bàn " + ban.getMaBan() + " thành công!\n" +
                        "Mã đặt bàn: " + datBan.getMaDatBan() + "\n" +
                        "Thời gian: " + thoiGianDat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    
                    // Tải lại danh sách bàn
                    loadDSBanTheoTang();
                } else {
                    showErrorAlert("Lỗi", "Không thể cập nhật trạng thái bàn", "");
                }
            } else {
                showErrorAlert("Lỗi", "Không thể đặt bàn", "Vui lòng thử lại");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Lỗi", "Đã xảy ra lỗi khi đặt bàn", e.getMessage());
        }
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void refreshBanList() {
        loadDSBanTheoTang();
    }
}