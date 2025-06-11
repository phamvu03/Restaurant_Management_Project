package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.EquipmentDAO;
import com.restaurant.restaurant_management_project.model.Equipment;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class EquipmentController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private TableView<Equipment> equipmentTableId;

    @FXML
    private TableColumn<Equipment, String> colCondition;

    @FXML
    private TableColumn<Equipment, String> colId;

    @FXML
    private TableColumn<Equipment, String> colName;

    @FXML
    private TableColumn<Equipment, Integer> colQuantity;

    @FXML
    private TableColumn<Equipment, Date> colReportDate;

    @FXML
    private TableColumn<Equipment, String>  colType;

    @FXML
    private Button addFormBtn;

    @FXML
    private Button updateFormBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button refreshBtn;

    @FXML
    private Button searchBtn;

    @FXML
    private TextField searchTxt;

    private final EquipmentDAO equipDAO = new EquipmentDAO();

    private EquipmentController equipmentController;

    public void setEquipmentController(EquipmentController controller) {
        this.equipmentController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Thêm dòng này để colName tự động fill khoảng trống
        equipmentTableId.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); //

        loadDataFromDatabase();
        addFormBtn.setOnAction(e -> handleAddForm());
        updateFormBtn.setOnAction(e -> handleUpdateForm());
        deleteBtn.setOnAction(e -> handleDelete());
        refreshBtn.setOnAction(e -> {
            loadDataFromDatabase();
        });
        searchBtn.setOnAction(e -> {
            try {
                handleSearch();
            } catch (SQLException ex) {
                Logger.getLogger(EquipmentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void loadDataFromDatabase(){
        List<Equipment> list = equipDAO.getAllEquipment();
        ObservableList<Equipment> observableList  = FXCollections.observableArrayList(list);

        colId.setCellValueFactory(new PropertyValueFactory<>("MaDungCu"));
        colName.setCellValueFactory(new PropertyValueFactory<>("TenDungCu"));
        colType.setCellValueFactory(new PropertyValueFactory<>("Loai"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("SoLuong"));
        colCondition.setCellValueFactory(new PropertyValueFactory<>("TinhTrang"));
        colReportDate.setCellValueFactory(new PropertyValueFactory<>("NgayThongKe"));
        colReportDate.setCellFactory(column -> {
            return new TableCell<>() {
                private final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(sdf.format(item));
                    }
                }
            };
        });


        equipmentTableId.setItems(observableList);
    }
    private void handleAddForm(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddEquipment.fxml"));
            Parent root = loader.load();

            AddEquipmentController controller = loader.getController();
            controller.setEquipmentController(this);

            Stage stage = new Stage();
            stage.setTitle("Thêm Dụng Cụ");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void handleUpdateForm(){
        Equipment selected = equipmentTableId.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn thiết bị", "Vui lòng chọn thiết bị cần cập nhật.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpdateEquipment.fxml"));
            Parent root = loader.load();

            UpdateEquipmentController controller = loader.getController();
            controller.setEquipment(selected);
            controller.setEquipmentController(this);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật Dụng cụ");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void handleDelete(){
        Equipment selected = equipmentTableId.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn thiết bị", "Vui lòng chọn thiết bị cần xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc chắn muốn xóa thiết bị \"" + selected.getTenDungCu() + "\"?");

        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean success = equipDAO.deleteEquipment(selected.getMaDungCu());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa thiết bị.");
                    loadDataFromDatabase();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Xóa thiết bị thất bại.");
                }
            }
        });
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleSearch() throws SQLException{
        String keyword = searchTxt.getText().trim();
        if (keyword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Tìm kiếm", "Vui lòng nhập tên dụng cụ cần tìm.");
            return;
        }

        List<Equipment> result = equipDAO.searchEquipmentByName(keyword);
        ObservableList<Equipment> observableList = FXCollections.observableArrayList(result);
        equipmentTableId.setItems(observableList);
    }
}