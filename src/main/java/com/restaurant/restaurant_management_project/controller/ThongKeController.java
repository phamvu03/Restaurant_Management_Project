package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.DatBanDAO;
import com.restaurant.restaurant_management_project.model.ThongKe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class ThongKeController {
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;
    @FXML private TableView<ThongKe> thongKeTable;
    @FXML private BarChart<String, Number> thongKeChart;

    @FXML private TableColumn<ThongKe, LocalDate> colNgay;
    @FXML private TableColumn<ThongKe, Integer> colSoLuot;
    @FXML private TableColumn<ThongKe, Integer> colTongKhach;

    private DatBanDAO datBanDAO = new DatBanDAO();
    private ObservableList<ThongKe> thongKeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Thiết lập ngày mặc định (7 ngày gần nhất)
        toDate.setValue(LocalDate.now());
        fromDate.setValue(LocalDate.now().minusDays(7));

        // Thiết lập bảng
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngay"));
        colSoLuot.setCellValueFactory(new PropertyValueFactory<>("soLuotDat"));
        colTongKhach.setCellValueFactory(new PropertyValueFactory<>("tongKhach"));
        thongKeTable.setItems(thongKeList);

        // Load dữ liệu ban đầu
        xemThongKe();
    }

    @FXML
    private void xemThongKe() {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();

        if (from != null && to != null) {
            thongKeList.setAll(datBanDAO.thongKeDatBan(from, to));
            updateChart();
        }
    }

    private void updateChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượt đặt bàn");
        for (ThongKe tk : thongKeList) {
            series.getData().add(new XYChart.Data<>(tk.getNgay().toString(), tk.getSoLuotDat()));
        }

        thongKeChart.getData().clear();
        thongKeChart.getData().add(series);
    }
}