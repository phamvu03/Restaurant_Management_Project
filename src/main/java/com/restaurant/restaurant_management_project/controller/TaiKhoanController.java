package com.restaurant.restaurant_management_project.controller;


import com.restaurant.restaurant_management_project.dao.TaiKhoanDAO;
import com.restaurant.restaurant_management_project.model.TaiKhoan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class TaiKhoanController {

	@FXML
	private TableView<TaiKhoan> tableTaiKhoan;
	@FXML
	private TableColumn<TaiKhoan, String> colUsername;
	@FXML
	private TableColumn<TaiKhoan, String> colPassword;
	@FXML
	private TableColumn<TaiKhoan, String> colRole;
	@FXML
	private TableColumn<TaiKhoan, String> colEmployeeId;
	@FXML
	private TableColumn<TaiKhoan, Integer> colStt;

	@FXML
	private TextField txtTimKiem;
	@FXML
	private TextField txtUsername;
	@FXML
	private TextField txtPassword;
	@FXML
	private ComboBox<String> role;
	@FXML
	private TextField txtEmployeeId;

	private TaiKhoanDAO dao = new TaiKhoanDAO();
	private ObservableList<TaiKhoan> dsTaiKhoan;

	@FXML
	public void initialize() {
		colUsername.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
		colPassword.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getPassword()));
		colRole.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));
		colEmployeeId.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaNV()));
		colStt.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
				tableTaiKhoan.getItems().indexOf(cell.getValue()) + 1).asObject());

		role.setItems(FXCollections.observableArrayList("admin", "user"));

		loadData();
		tableTaiKhoan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
			if (selected != null) {
				hienThiChiTiet(selected);
			}
		});
	}

	private void loadData() {
		try {
			List<TaiKhoan> list = dao.getAllAccounts();
			dsTaiKhoan = FXCollections.observableArrayList(list);
			tableTaiKhoan.setItems(dsTaiKhoan);
		} catch (SQLException e) {
			showError("Lỗi tải dữ liệu: " + e.getMessage());
		}
	}

	private void hienThiChiTiet(TaiKhoan tk) {
		txtUsername.setText(tk.getUsername());
		txtPassword.setText(tk.getPassword());
		role.setValue(tk.getRole());
		txtEmployeeId.setText(tk.getMaNV());
	}

	private boolean validateForm() {
		if (txtEmployeeId.getText().isEmpty() || txtPassword.getText().isEmpty() || txtUsername.getText().isEmpty()
				|| role.getValue() == null) {
			showError("Vui lòng điền đầy đủ thông tin.");
			return false;
		}
		return true;

	}

	private TaiKhoan docTuForm() {
	    return new TaiKhoan(
	        txtUsername.getText(),
	        txtPassword.getText(),
	        role.getValue(),
	        txtEmployeeId.getText()
	    );
	}

	@FXML
	private void themTaiKhoan() {
		if (!validateForm())
			return;

		try {
			TaiKhoan tk = docTuForm();
			if (dao.addAccount(tk)) {
				dsTaiKhoan.add(tk);
				showInfo("Đã thêm tài khoản mới.");
				lamTrongForm();
			}
		} catch (Exception e) {
			showError("Lỗi thêm: " + e.getMessage());
		}
	}

	@FXML
	private void suaTaiKhoan() {
		if (!validateForm())
			return;
		try {
			TaiKhoan tk = docTuForm();
			if (dao.updateTaiKhoan(tk)) {
				showInfo("Đã cập nhật.");
				loadData(); // tải lại danh sách
				lamTrongForm(); // xóa form
			}
		} catch (SQLException e) {
			showError("Lỗi cập nhật: " + e.getMessage());
		}
	}

	@FXML
	private void xoaTaiKhoan() {
		String username = txtUsername.getText();
		if (username.isEmpty()) {
			showAlert("Chọn tài khoản để xóa!");
			return;
		}
		try {
			if (dao.deleteAccount(username)) {
				showAlert("Xóa thành công!");
				loadData();
				lamTrongForm();
			} else {
				showAlert("Xóa thất bại!");
			}
		} catch (SQLException e) {
			showAlert("Lỗi: " + e.getMessage());
		}
	}

	@FXML
	private void timkiemTaiKhoan() {
		String tukhoa = txtUsername.getText();
		try {
			List<TaiKhoan> list = dao.timKiemTaiKhoan(tukhoa);
			if (list.isEmpty()) {
	            showInfo("Không tìm thấy nhân viên nào với từ khóa: " + tukhoa);
	        }
			dsTaiKhoan = FXCollections.observableArrayList(list);
			tableTaiKhoan.setItems(dsTaiKhoan);
		} catch (SQLException e) {
			showError("Lỗi tìm kiếm: " + e.getMessage());
		}
	}

	@FXML
	private void lamTrongForm() {
		txtUsername.clear();
		txtPassword.clear();
		txtEmployeeId.clear();
		role.setValue(null);
		txtTimKiem.clear();
		loadData();
	}

	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setContentText(msg);
		alert.showAndWait();
	}

	private void showError(String msg) {
		new Alert(Alert.AlertType.ERROR, msg).showAndWait();
	}

	private void showInfo(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}
}
