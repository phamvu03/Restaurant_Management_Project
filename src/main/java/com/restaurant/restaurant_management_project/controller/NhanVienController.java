package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.EmployeeDAO;
import com.restaurant.restaurant_management_project.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class NhanVienController {

	@FXML
	private Button btnLamTrong;
	@FXML
	private Button btnSua;
	@FXML
	private Button btnThem;
	@FXML
	private Button btnTimKiem;
	@FXML
	private Button btnXemBieuDo;
	@FXML
	private Button btnXoa;

	@FXML
	private TableColumn<Employee, String> colMaNV;
	@FXML
	private TableColumn<Employee, String> colTenNV;
	@FXML
	private TableColumn<Employee, LocalDate> colNgaySinh;
	@FXML
	private TableColumn<Employee, String> colSDT;
	@FXML
	private TableColumn<Employee, String> colEmail;
	@FXML
	private TableColumn<Employee, String> colChucVu;
	@FXML
	private TableColumn<Employee, BigDecimal> colLuong;
	@FXML
	private TableColumn<Employee, Integer> colStt;

	@FXML
	private TableView<Employee> tableEmployee;

	@FXML
	private ComboBox<String> txtChucVu;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtLuong;
	@FXML
	private TextField txtMaNV;
	@FXML
	private DatePicker txtNgaySinh;
	@FXML
	private TextField txtSDT;
	@FXML
	private TextField txtTenNV;
	@FXML
	private TextField txtTimKiem;

	private final EmployeeDAO dao = new EmployeeDAO();
	private ObservableList<Employee> dsEmployee;

	@FXML
	public void initialize() {
		colMaNV.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaNV()));
		colTenNV.setCellValueFactory(
				cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTenNV()));
		colNgaySinh.setCellValueFactory(
				cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getNgaySinh()));
		colSDT.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSdt()));
		colEmail.setCellValueFactory(
				cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
		colChucVu.setCellValueFactory(
				cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getChucVu()));
		colLuong.setCellValueFactory(
				cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getLuong()));
		colStt.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(
				tableEmployee.getItems().indexOf(cell.getValue()) + 1).asObject());

		txtChucVu.getItems().addAll("Phục vụ", "Thu ngân", "Bếp chính", "Phụ bếp", "Quản lý", "Tổ trưởng ca","Tiếp tân", "Giao hàng", "Tạp vụ");

		loadData();

		tableEmployee.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
			if (selected != null) {
				hienThiChiTiet(selected);
			}
		});
	}

	private void loadData() {
            List<Employee> list = dao.getAllEmployee();
            dsEmployee = FXCollections.observableArrayList(list);
            tableEmployee.setItems(dsEmployee);
	}

	private void hienThiChiTiet(Employee nv) {
		txtMaNV.setText(nv.getMaNV());
		txtTenNV.setText(nv.getTenNV());
		txtNgaySinh.setValue(nv.getNgaySinh());
		txtSDT.setText(nv.getSdt());
		txtEmail.setText(nv.getEmail());
		txtChucVu.setValue(nv.getChucVu());
		txtLuong.setText(nv.getLuong().toPlainString());
	}
	private boolean validateForm() {
	    // 1. Bắt buộc nhập
	    if (
	        txtNgaySinh.getValue() == null || txtSDT.getText().isEmpty() ||
	        txtEmail.getText().isEmpty() || txtChucVu.getValue() == null ||
	        txtLuong.getText().isEmpty()) {
	        showError("Vui lòng điền đầy đủ thông tin.");
	        return false;
	    }
	    // 2. Ngày sinh không vượt quá hôm nay
	    if (txtNgaySinh.getValue().isAfter(LocalDate.now())) {
	        showError("Ngày sinh không thể sau ngày hiện tại.");
	        return false;
	    }
	    // 3. Lương phải là số >= 0
	    try {
	        BigDecimal luong = new BigDecimal(txtLuong.getText());
	        if (luong.compareTo(BigDecimal.ZERO) < 0) {
	            showError("Lương phải lớn hơn hoặc bằng 0.");
	            return false;
	        }
	    } catch (NumberFormatException e) {
	        showError("Lương phải là một số hợp lệ.");
	        return false;
	    }
	    // 4. Kiểm tra định dạng SDT (10 số, bắt đầu 0)
	    if (!txtSDT.getText().matches("0\\d{9}")) {
	        showError("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0.");
	        return false;
	    }
	    // 5. Kiểm tra định dạng email cơ bản
	    if (!txtEmail.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
	        showError("Email không hợp lệ.");
	        return false;
	    }
	    return true;
	}


	private Employee docTuForm() {
		return new Employee(txtMaNV.getText(), txtTenNV.getText(), txtNgaySinh.getValue(), txtSDT.getText(),
				txtEmail.getText(), txtChucVu.getValue(), new BigDecimal(txtLuong.getText()));
	}

	@FXML
	private void themEmployee() {
		if (!validateForm()) return;

		try {
			Employee nv = docTuForm();
			if (dao.addEmployee(nv)) {
				dsEmployee.add(nv);
				showInfo("Đã thêm nhân viên.");
				lamTrongForm();
			}
		} catch (Exception e) {
			showError("Lỗi thêm: " + e.getMessage());
		}
	}

	@FXML
	private void suaEmployee() {
	    if (!validateForm()) return;
	    try {
	        Employee nv = docTuForm();
	        if (dao.updateEmployee(nv)) {
	            showInfo("Đã cập nhật.");
	            loadData();      // tải lại danh sách
	            lamTrongForm();  // xóa form
	        }
	    } catch (SQLException e) {
	        showError("Lỗi cập nhật: " + e.getMessage());
	    }
	}

	@FXML
	private void deleteEmployee() {
	    Employee selected = tableEmployee.getSelectionModel().getSelectedItem();
	    if (selected == null) {
	        showError("Chưa chọn nhân viên.");
	        return;
	    }
            if (dao.deleteEmployee(selected.getMaNV())) {
                showInfo("Đã xóa.");
                loadData();      // tải lại danh sách
                lamTrongForm();  // xóa form
            }
	}


	@FXML
	private void timKiemEmployee() {
		String keyword = txtTimKiem.getText();
		try {
			List<Employee> list = dao.searchEmployee(keyword);
			if (list.isEmpty()) {
	            showInfo("Không tìm thấy nhân viên nào với từ khóa: " + keyword);
	        }
			dsEmployee = FXCollections.observableArrayList(list);
			tableEmployee.setItems(dsEmployee);
		} catch (SQLException e) {
			showError("Lỗi tìm kiếm: " + e.getMessage());
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

	@FXML
	private void xemBieuDoLuong() {
	    Stage stage = new Stage();
	    stage.setTitle("Thống kê nhân viên theo chức vụ");

	    // PieChart
	    PieChart pieChart = new PieChart();
	    pieChart.setTitle("Tỉ lệ nhân viên theo chức vụ");

	    try {
	        List<Object[]> thongKe = dao.thongKeEmployeeTheoChucVu();
	        for (Object[] row : thongKe) {
	            String chucVu = (String) row[0];
	            int soLuong = (int) row[1];

	            // Mới (định dạng label cho đẹp):
	            PieChart.Data slice = new PieChart.Data(chucVu + " (" + soLuong + ")", soLuong);
	            pieChart.getData().add(slice);

	        }
	    } catch (SQLException e) {
	        showError("Lỗi biểu đồ: " + e.getMessage());
	        return;
	    }

	    // Bố cục giao diện
	    VBox vbox = new VBox(10, pieChart);
	    vbox.setPadding(new Insets(10));

	    Scene scene = new Scene(vbox, 600, 600);
	    stage.setScene(scene);
	    stage.show();
	}



	private void showError(String msg) {
		new Alert(Alert.AlertType.ERROR, msg).showAndWait();
	}

	private void showInfo(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
	}
}
