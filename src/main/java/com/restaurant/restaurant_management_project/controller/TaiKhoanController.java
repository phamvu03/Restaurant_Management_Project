package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.AccountDAO;
import com.restaurant.restaurant_management_project.model.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.List;

public class TaiKhoanController {

	@FXML
	private TableView<Account> tableAccount;
	@FXML
	private TableColumn<Account, String> colUsername;
	@FXML
	private TableColumn<Account, String> colPassword;
	@FXML
	private TableColumn<Account, String> colRole;
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
	private ComboBox<String> role;
	@FXML
	private TextField txtEmployeeId;

	private AccountDAO dao = new AccountDAO();
	private ObservableList<Account> dsAccount;

	@FXML
	public void initialize() {
		colUsername.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getTenTK()));
		colPassword.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getMatKhau()));
		colRole.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));
		colEmployeeId.setCellValueFactory(cell ->new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaNV()));
		colStt.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
				tableAccount.getItems().indexOf(cell.getValue()) + 1).asObject());

		role.setItems(FXCollections.observableArrayList("admin", "user"));

		loadData();
		tableAccount.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
			if (selected != null) {
				hienThiChiTiet(selected);
			}
		});
	}

	private void loadData() {
            List<Account> list = dao.getAllAccounts();
            dsAccount = FXCollections.observableArrayList(list);
            tableAccount.setItems(dsAccount);
	}

	private void hienThiChiTiet(Account tk) {
		txtUsername.setText(tk.getTenTK());
		txtPassword.setText(tk.getMatKhau());
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

	private Account docTuForm() {
	    return new Account(
	        txtUsername.getText(),
	        txtPassword.getText(),
	        txtEmployeeId.getText()
	    );
	}

	@FXML
	private void themAccount() {
            if (!validateForm())
                return;

            try {
                Account tk = docTuForm();
                if (dao.addAccount(tk)) {
                    dsAccount.add(tk);
                    showInfo("Đã thêm tài khoản mới.");
                    lamTrongForm();
                }
            } catch (Exception e) {
                showError("Lỗi thêm: " + e.getMessage());
            }
	}

	@FXML
	private void suaAccount() {
		if (!validateForm())
			return;
                Account tk = docTuForm();
                if (dao.updateAccount(tk)) {
                    showInfo("Đã cập nhật.");
                    loadData(); // tải lại danh sách
                    lamTrongForm(); // xóa form
                }
	}

	@FXML
	private void xoaAccount() {
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
	private void timkiemAccount() {
		String tukhoa = txtUsername.getText();
		try {
			List<Account> list = dao.timKiemAccount(tukhoa);
			if (list.isEmpty()) {
	            showInfo("Không tìm thấy nhân viên nào với từ khóa: " + tukhoa);
	        }
			dsAccount = FXCollections.observableArrayList(list);
			tableAccount.setItems(dsAccount);
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
