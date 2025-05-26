package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static java.lang.System.load;

public class EmployeeController {

	@FXML private Button btnLamTrong;
	@FXML private Button btnSua;
	@FXML private Button btnThem;
	@FXML private Button btnTimKiem;
	@FXML private Button btnXoa;

	@FXML private TableColumn<Employee, String> colMaNV;
	@FXML private TableColumn<Employee, String> colTenNV;
	@FXML private TableColumn<Employee, LocalDate> colNgaySinh;
	@FXML private TableColumn<Employee, String> colSDT;
	@FXML private TableColumn<Employee, String> colEmail;
	@FXML private TableColumn<Employee, String> colChucVu;
	@FXML private TableColumn<Employee, BigDecimal> colLuong;
	@FXML private TableColumn<Employee, Integer> colStt;

	@FXML private TableView<Employee> tableNhanVien;

	@FXML private ComboBox<String> txtChucVu;
	@FXML private TextField txtEmail;
	@FXML private TextField txtLuong;
	@FXML private TextField txtMaNV;
	@FXML private DatePicker txtNgaySinh;
	@FXML private TextField txtSDT;
	@FXML private TextField txtTenNV;
	@FXML private TextField txtTimKiem;

	private final EmployeeDAO dao = new EmployeeDAO();
	private ObservableList<Employee> dsNhanVien;

	@FXML
	public void initialize() {
		colMaNV.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaNV()));
		colTenNV.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTenNV()));
		colNgaySinh.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getNgaySinh()));
		colSDT.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSDT()));
		colEmail.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
		colChucVu.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getChucVu()));
		colLuong.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getLuong()));
		colStt.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(tableNhanVien.getItems().indexOf(cell.getValue()) + 1).asObject());

		txtChucVu.getItems().addAll("Phục vụ", "Thu ngân", "Bếp chính", "Phụ bếp", "Quản lý", "Tổ trưởng ca", "Tiếp tân", "Giao hàng", "Tạp vụ");

		loadData();

		tableNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
			if (selected != null) {
				hienThiChiTiet(selected);
			}
		});
	}


	private void loadData() {
		try {
			List<Employee> list = dao.getAllEmployee();
			dsNhanVien = FXCollections.observableArrayList(list);
			tableNhanVien.setItems(dsNhanVien);
		} catch (Exception e) {
			showSuccessDialog("Lỗi tải dữ liệu: " + e.getMessage());
		}
	}


	private void hienThiChiTiet(Employee nv) {
		txtMaNV.setText(nv.getMaNV());
		txtTenNV.setText(nv.getTenNV());
		txtNgaySinh.setValue(nv.getNgaySinh());
		txtSDT.setText(nv.getSDT());
		txtEmail.setText(nv.getEmail());
		txtChucVu.setValue(nv.getChucVu());
		txtLuong.setText(nv.getLuong().toPlainString());
	}

	private boolean validateForm() {
		if (txtNgaySinh.getValue() == null || txtSDT.getText().isEmpty() || txtEmail.getText().isEmpty()
				|| txtChucVu.getValue() == null || txtLuong.getText().isEmpty()) {
			showSuccessDialog("Vui lòng điền đầy đủ thông tin.");
			return false;
		}

		if (txtNgaySinh.getValue().isAfter(LocalDate.now())) {
			showSuccessDialog("Ngày sinh không thể sau ngày hiện tại.");
			return false;
		}

		try {
			BigDecimal luong = new BigDecimal(txtLuong.getText());
			if (luong.compareTo(BigDecimal.ZERO) < 0) {
				showSuccessDialog("Lương phải lớn hơn hoặc bằng 0.");
				return false;
			}
		} catch (NumberFormatException e) {
			showSuccessDialog("Lương phải là một số hợp lệ.");
			return false;
		}

		if (!txtSDT.getText().matches("0\\d{9}")) {
			showSuccessDialog("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
			return false;
		}

		if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
			showSuccessDialog("Email không hợp lệ.");
			return false;
		}

		return true;
	}

	private Employee docTuForm() {
		return new Employee(
				txtMaNV.getText(),
				txtTenNV.getText(),
				txtNgaySinh.getValue(),
				txtSDT.getText(),
				txtEmail.getText(),
				txtChucVu.getValue(),
				new BigDecimal(txtLuong.getText())
		);
	}

	@FXML
	private void themNhanVien() {
		if (!validateForm()) return;

		try {
			Employee nv = docTuForm();
			if (dao.addEmployee(nv)) {
				dsNhanVien.add(nv);
				showSuccessDialog("Đã thêm nhân viên.");
				lamTrongForm();
			}
		} catch (Exception e) {
			showSuccessDialog("Lỗi thêm: " + e.getMessage());
		}
	}
	@FXML
	private void handleCreateAccount(ActionEvent event) throws IOException {
		// Tạo FXMLLoader để load giao diện Tài Khoản
		Employee selectedEmployee = tableNhanVien.getSelectionModel().getSelectedItem();
		if (selectedEmployee == null) {
			showSuccessDialog("Vui lòng chọn một nhân viên trước khi tạo tài khoản.");
			return;
		}
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TaiKhoan.fxml"));

		// Lấy root hiện tại (HBox chứa sidebar và nội dung)
		HBox root = (HBox) ((Node) event.getSource()).getScene().getRoot();

		// Xóa nội dung cũ (thường là phần ở index 1 bên phải)
		if (root.getChildren().size() > 1) {
			root.getChildren().remove(1);
		}

		// Thêm phần giao diện tài khoản mới được load vào
		root.getChildren().add(loader.load());
		// Lấy controller
		AccountController controller = loader.getController();

		// Truyền mã nhân viên sang
		controller.setMaNhanVien(selectedEmployee.getMaNV());
	}

	@FXML
	private void suaNhanVien() {
		if (!validateForm()) return;
		try {
			Employee nv = docTuForm();
			if (dao.updateEmployee(nv)) {
				showSuccessDialog("Đã cập nhật.");
				loadData();
				lamTrongForm();
			}
		} catch (Exception e) {
			showSuccessDialog("Lỗi cập nhật: " + e.getMessage());
		}
	}

	@FXML
	private void xoaNhanVien() {
		Employee selected = tableNhanVien.getSelectionModel().getSelectedItem();
		if (selected == null) {
			showSuccessDialog("Chưa chọn nhân viên.");
			return;
		}
		try {
			if (dao.deleteEmployee(selected.getMaNV())) {
				showSuccessDialog("Đã xóa.");
				loadData();
				lamTrongForm();
			}
		} catch (Exception e) {
			showSuccessDialog("Lỗi xóa: " + e.getMessage());
		}
	}

	@FXML
	private void timKiemNhanVien() {
		String keyword = txtTimKiem.getText();
		try {
			List<Employee> list = dao.searchNhanVien(keyword);
			if (list.isEmpty()) {
				showSuccessDialog("Không tìm thấy nhân viên nào với từ khóa: " + keyword);
			}
			dsNhanVien = FXCollections.observableArrayList(list);
			tableNhanVien.setItems(dsNhanVien);
		} catch (Exception e) {
			showSuccessDialog("Lỗi tìm kiếm: " + e.getMessage());
		}
	}

	@FXML
	private void lamTrongForm() {
		txtMaNV.clear();
		txtTenNV.clear();
		txtNgaySinh.setValue(null);
		txtSDT.clear();
		txtEmail.clear();
		txtChucVu.setValue(null);
		txtLuong.clear();
		txtTimKiem.clear();
		loadData();
	}
	private void showSuccessDialog(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Thông báo");
		alert.setHeaderText(null);
		alert.setContentText(message);
		// Tùy chỉnh DialogPane
		DialogPane dialogPane = alert.getDialogPane();
		alert.initStyle(StageStyle.UTILITY);
		alert.showAndWait();
	}


	}

