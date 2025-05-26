package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.AccountDAO;
import com.restaurant.restaurant_management_project.model.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.List;

public class AccountController {

	@FXML
	private TableView<Account> tableTaiKhoan;
	@FXML
	private TableColumn<Account, String> colUsername;
	@FXML
	private TableColumn<Account, String> colPassword;
	@FXML
	private TableColumn<Account, String> colEmployeeId;
	@FXML
	private TableColumn<Account, Integer> colStt;

	@FXML
	private TextField txtTimKiem;
	@FXML
	private TextField txtUsername;
	@FXML
	private TextField txtPassword;
	@FXML
	private TextField txtEmployeeId;

	private AccountDAO dao = new AccountDAO();
	private ObservableList<Account> dsTaiKhoan;

	@FXML
	public void initialize() {
		colUsername.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTenTK()));
		colPassword.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMatKhau()));
		colEmployeeId.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaNV()));
		colStt.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
				tableTaiKhoan.getItems().indexOf(cell.getValue()) + 1).asObject());

		loadData();

		tableTaiKhoan.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
			if (selected != null) {
				hienThiChiTiet(selected);
			}
		});
	}

	private void loadData() {
		try {
			List<Account> list = dao.getAllAccounts();
			dsTaiKhoan = FXCollections.observableArrayList(list);
			tableTaiKhoan.setItems(dsTaiKhoan);
		} catch (Exception e) {
			showSuccessDialog("Lỗi tải dữ liệu: " + e.getMessage());
		}
	}

	private void hienThiChiTiet(Account acc) {
		txtUsername.setText(acc.getTenTK());
		txtPassword.setText(acc.getMatKhau());
		txtEmployeeId.setText(acc.getMaNV());
	}

	private boolean validateForm() {
		if (txtEmployeeId.getText().isEmpty() || txtPassword.getText().isEmpty() || txtUsername.getText().isEmpty()) {
			showSuccessDialog("Vui lòng điền đầy đủ thông tin.");
			return false;
		}
		return true;
	}

	private Account docTuForm() {
		return new Account(
				txtEmployeeId.getText(),
				txtUsername.getText(),
				txtPassword.getText()
		);
	}

	@FXML
	private void themTaiKhoan() {
		if (!validateForm()) return;

		try {
			Account acc = docTuForm();
			if (dao.addAccount(acc)) {
				dsTaiKhoan.add(acc);
				showSuccessDialog("Đã thêm tài khoản mới.");
				lamTrongForm();
			}
		} catch (Exception e) {
			showSuccessDialog("Lỗi thêm: " + e.getMessage());
		}
	}
	@FXML
	private void suaTaiKhoan() {
		if (!validateForm()) return;

		try {
			Account acc = docTuForm();
			if (dao.updateAccount(acc)) {
				showSuccessDialog("Đã cập nhật.");
				loadData();
				lamTrongForm();
			}
		} catch (Exception e) {
			showSuccessDialog("Lỗi cập nhật: " + e.getMessage());
		}
	}


	@FXML
	private void xoaTaiKhoan() {
		String maNV = txtEmployeeId.getText();
		String tenTK = txtUsername.getText();

		if (maNV.isEmpty() || tenTK.isEmpty()) {
			showSuccessDialog("Vui lòng chọn tài khoản để xóa.");
			return;
		}

		try {
			if (dao.deleteAccount(maNV, tenTK)) {
				showSuccessDialog("Xóa thành công!");
				loadData();
				lamTrongForm();
			} else {
				showSuccessDialog("Xóa thất bại!");
			}
		} catch (Exception e) {
			showSuccessDialog("Lỗi: " + e.getMessage());
		}
	}

	@FXML
	private void timkiemTaiKhoan() {
		String tuKhoa = txtTimKiem.getText();
		try {
			List<Account> list = dao.timKiemTaiKhoan(tuKhoa);
			if (list.isEmpty()) {
				showSuccessDialog("Không tìm thấy tài khoản nào với từ khóa: " + tuKhoa);
			}
			dsTaiKhoan = FXCollections.observableArrayList(list);
			tableTaiKhoan.setItems(dsTaiKhoan);
		} catch (Exception e) {
			showSuccessDialog("Lỗi khi tìm kiếm tài khoản: " + e.getMessage());
		}
	}


	@FXML
	private void lamTrongForm() {
		txtUsername.clear();
		txtPassword.clear();
		txtEmployeeId.clear();
		txtTimKiem.clear();
		txtEmployeeId.setEditable(true); // hoặc setDisable(true) nếu muốn mờ đi

		loadData();
	}

	public void setMaNhanVien(String maNV) {
		txtEmployeeId.setText(maNV);
		txtEmployeeId.setEditable(false); // hoặc setDisable(true) nếu muốn mờ đi
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
