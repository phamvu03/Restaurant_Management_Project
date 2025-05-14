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
import java.util.List;
import java.util.stream.Collectors;

public class DatBanController {
    @FXML private TabPane tabPane;
    private BanDAO banDAO = new BanDAO();

    // Đường dẫn ảnh chung cho tất cả các bàn
    private static final String TABLE_IMAGE_PATH = "/image/ban.png";

    @FXML
    public void initialize() {
        loadDSBanTheoTang();

    }

    private void loadDSBanTheoTang() {
        List<Ban> dsBan = banDAO.getDSBan();

        // Nhóm bàn theo tầng
        List<String> dsTang = dsBan.stream()
                .map(Ban::getViTri)
                .distinct()
                .collect(Collectors.toList());

        for (String tang : dsTang) {
            Tab tab = new Tab(" " + tang);
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
        box.getStyleClass().add(ban.getTrangThai().equalsIgnoreCase("Trống") ? "ban-trong" :
                ban.getTrangThai().equalsIgnoreCase("Đang dùng") ? "ban-dang-dung" : "ban-da-dat");

        // Tạo ImageView với ảnh bàn chung
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(TABLE_IMAGE_PATH)));
        imageView.setFitWidth(80);  // Điều chỉnh kích thước ảnh
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        Label lbTen = new Label(ban.getTenBan());
        lbTen.getStyleClass().add("ban-ten");

        Label lbTrangThai = new Label(ban.getTrangThai());
        lbTrangThai.getStyleClass().add("ban-trang-thai");

        Button btnDatBan = new Button("ĐẶT BÀN");
        btnDatBan.getStyleClass().add("btn-dat-ban");
        btnDatBan.setDisable(!ban.getTrangThai().equalsIgnoreCase("Trống"));

        btnDatBan.setOnAction(e -> showDatBanDialog(ban));

        box.getChildren().addAll(imageView, lbTen, lbTrangThai, btnDatBan);
        return box;
    }

    private void showDatBanDialog(Ban ban) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/DatBanDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Đặt bàn " + ban.getTenBan());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tabPane.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            DatBanDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isDatBanClicked()) {
                String tenKhachHang = controller.getTenKhachHang();
                String soDienThoai = controller.getSoDienThoai();
                int soLuongNguoi = controller.getSoLuongNguoi();
                String ghiChu = controller.getGhiChu();

                DatBan datBan = new DatBan(ban.getMaBan(), tenKhachHang, soDienThoai,
                        LocalDateTime.now(),
                        controller.getThoiGianDen(),
                        soLuongNguoi,
                        ghiChu);

                DatBanDAO datBanDAO = new DatBanDAO();
                if (datBanDAO.themDatBan(datBan)) {
                    BanDAO banDAO = new BanDAO();
                    banDAO.capNhatTrangThai(ban.getMaBan(), "Đã đặt");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText("Đặt bàn thành công");
                    alert.setContentText("Bàn " + ban.getTenBan() + " đã được đặt thành công");
                    alert.showAndWait();

                    // Cập nhật lại giao diện sau khi đặt bàn thành công
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tải form đặt bàn");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}