package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BenefitReportController {

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private BarChart<String, Number> revenueBarChart;
    @FXML private LineChart<String, Number> revenueLineChart;
    @FXML private PieChart revenuePieChart;
    @FXML private TabPane tabPane;
    @FXML private TableView<RevenueData> revenueTable;
    @FXML private TableColumn<RevenueData, String> periodColumn;
    @FXML private TableColumn<RevenueData, BigDecimal> revenueColumn;
    @FXML private Button exportButton;

    private final ReportDAO reportDAO = new ReportDAO();
    private ObservableList<RevenueData> tableData = FXCollections.observableArrayList();

    public void initialize() {
        // Thiết lập giá trị mặc định
        fromDatePicker.setValue(LocalDate.now().minusDays(7));
        toDatePicker.setValue(LocalDate.now());

        // Thiết lập combobox năm (năm hiện tại và 5 năm trước)
        List<Integer> years = new ArrayList<>();
        int currentYear = Year.now().getValue();
        for (int i = 0; i < 6; i++) {
            years.add(currentYear - i);
        }
        yearComboBox.setItems(FXCollections.observableArrayList(years));
        yearComboBox.setValue(currentYear);

        // Thiết lập combobox tháng
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add("Tháng " + i);
        }
        monthComboBox.setItems(FXCollections.observableArrayList(months));
        monthComboBox.setValue("Tháng " + LocalDate.now().getMonthValue());

        // Thiết lập loại báo cáo (đã bỏ "Doanh thu theo thứ")
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
                "Doanh thu theo ngày",
                "Doanh thu theo tuần",
                "Doanh thu theo tháng"
        ));
        reportTypeComboBox.setValue("Doanh thu theo ngày");

        // Thiết lập bảng dữ liệu
        setupTable();

        // Khởi tạo biểu đồ với trục và nhãn
        initializeCharts();

        // Xử lý sự kiện thay đổi loại báo cáo
        reportTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updateView());

        // Tải dữ liệu ban đầu
        updateView();
    }

    private void initializeCharts() {
        // Đảm bảo biểu đồ được hiển thị đúng
        revenueBarChart.setTitle("Biểu đồ doanh thu");
        CategoryAxis barXAxis = (CategoryAxis) revenueBarChart.getXAxis();
        barXAxis.setLabel("Thời gian");
        NumberAxis barYAxis = (NumberAxis) revenueBarChart.getYAxis();
        barYAxis.setLabel("Doanh thu (VNĐ)");
        barYAxis.setAutoRanging(true);
        barYAxis.setMinorTickVisible(false);

        // Cấu hình cho biểu đồ đường
        revenueLineChart.setTitle("Biểu đồ doanh thu theo tuần");
        CategoryAxis lineXAxis = (CategoryAxis) revenueLineChart.getXAxis();
        lineXAxis.setLabel("Thời gian");
        NumberAxis lineYAxis = (NumberAxis) revenueLineChart.getYAxis();
        lineYAxis.setLabel("Doanh thu (VNĐ)");
        lineYAxis.setAutoRanging(true);

        // Cấu hình cho biểu đồ tròn
        revenuePieChart.setTitle("Biểu đồ tỷ trọng doanh thu");

        // Đảm bảo hiển thị đúng theo loại báo cáo
        revenueBarChart.setVisible(true);
        revenueLineChart.setVisible(false);
        revenuePieChart.setVisible(false);

        // Thêm padding cho biểu đồ
        revenueBarChart.setPadding(new Insets(10, 10, 10, 10));
        revenueLineChart.setPadding(new Insets(10, 10, 10, 10));
        revenuePieChart.setPadding(new Insets(10, 10, 10, 10));
    }

    private void setupTable() {
        // Thiết lập TableView nếu chưa có columns
        if (revenueTable.getColumns().isEmpty() && periodColumn != null && revenueColumn != null) {
            revenueTable.getColumns().addAll(periodColumn, revenueColumn);
        }

        periodColumn.setCellValueFactory(cellData -> cellData.getValue().periodProperty());
        revenueColumn.setCellValueFactory(cellData -> cellData.getValue().revenueProperty());

        // Định dạng cột doanh thu thành tiền tệ
        revenueColumn.setCellFactory(column -> new TableCell<RevenueData, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(formatMoney(item));
                }
            }
        });

        revenueTable.setItems(tableData);
    }

    private void updateView() {
        String reportType = reportTypeComboBox.getValue();

        // Ẩn/hiện các điều khiển dựa trên loại báo cáo
        if ("Doanh thu theo ngày".equals(reportType)) {
            fromDatePicker.setVisible(true);
            toDatePicker.setVisible(true);
            yearComboBox.setVisible(false);
            monthComboBox.setVisible(false);

            revenueBarChart.setVisible(true);
            revenueLineChart.setVisible(false);
            revenuePieChart.setVisible(false);
        } else if ("Doanh thu theo tuần".equals(reportType)) {
            fromDatePicker.setVisible(false);
            toDatePicker.setVisible(false);
            yearComboBox.setVisible(true);
            monthComboBox.setVisible(true);

            revenueBarChart.setVisible(false);
            revenueLineChart.setVisible(true);
            revenuePieChart.setVisible(false);
        } else if ("Doanh thu theo tháng".equals(reportType)) {
            fromDatePicker.setVisible(false);
            toDatePicker.setVisible(false);
            yearComboBox.setVisible(true);
            monthComboBox.setVisible(false);

            revenueBarChart.setVisible(true);
            revenueLineChart.setVisible(false);
            revenuePieChart.setVisible(false);
        }

        // Tải lại dữ liệu
        loadData();
    }

    @FXML
    private void loadData() {
        String reportType = reportTypeComboBox.getValue();

        try {
            tableData.clear();

            switch (reportType) {
                case "Doanh thu theo ngày":
                    loadDailyRevenueData();
                    break;
                case "Doanh thu theo tuần":
                    loadWeeklyRevenueData();
                    break;
                case "Doanh thu theo tháng":
                    loadMonthlyRevenueData();
                    break;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu: " + e.getMessage());
        }
    }

    private void loadDailyRevenueData() throws SQLException {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        // Kiểm tra thời gian hợp lệ
        if (fromDate.isAfter(toDate)) {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Ngày bắt đầu không thể sau ngày kết thúc");
            return;
        }

        // Sử dụng API doanh thu theo ngày
        Map<String, BigDecimal> data = reportDAO.getDoanhThuTheoNgay(fromDate, toDate);

        // Hiển thị dữ liệu trên bảng và biểu đồ
        updateTable(data);
        updateBarChart(data);
    }

    private void loadWeeklyRevenueData() throws SQLException {
        int year = yearComboBox.getValue();
        int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;

        Map<String, BigDecimal> data = reportDAO.getDoanhThuTheoTuanTrongThang(month, year);

        // Hiển thị dữ liệu trên bảng và biểu đồ
        updateTable(data);
        updateLineChart(data);
    }

    private void loadMonthlyRevenueData() throws SQLException {
        int year = yearComboBox.getValue();

        Map<String, BigDecimal> data = reportDAO.getDoanhThuTheoThang(year);

        // Hiển thị dữ liệu trên bảng và biểu đồ
        updateTable(data);
        updateBarChart(data);
    }

    private void updateTable(Map<String, BigDecimal> data) {
        tableData.clear();

        for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
            tableData.add(new RevenueData(entry.getKey(), entry.getValue() != null ? entry.getValue() : BigDecimal.ZERO));
        }
    }

    private void updateBarChart(Map<String, BigDecimal> data) {
        revenueBarChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                double value = entry.getValue().doubleValue();
                series.getData().add(new XYChart.Data<>(entry.getKey(), value));
            }
        }

        revenueBarChart.getData().add(series);

        // Thêm tooltip cho các cột
        for (XYChart.Data<String, Number> item : series.getData()) {
            Tooltip tooltip = new Tooltip(String.format("%,.0f VND", (Double) item.getYValue()));
            Tooltip.install(item.getNode(), tooltip);
        }
    }

    private void updateLineChart(Map<String, BigDecimal> data) {
        revenueLineChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (Map.Entry<String, BigDecimal> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }

        revenueLineChart.getData().add(series);
    }

    @FXML
    private void exportToExcel() {
        String reportType = reportTypeComboBox.getValue();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo Excel");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("BaoCaoDoanhThu_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx");

        File file = fileChooser.showSaveDialog(revenueTable.getScene().getWindow());

        if (file != null) {
            try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
                // Tạo sheet cho báo cáo
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Báo cáo doanh thu");

                // Tạo header style
                org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                // Tạo tiêu đề báo cáo
                org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
                org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("BÁO CÁO DOANH THU - " + reportType.toUpperCase());
                titleCell.setCellStyle(headerStyle);

                // Tạo thông tin thời gian báo cáo
                org.apache.poi.ss.usermodel.Row infoRow = sheet.createRow(1);
                org.apache.poi.ss.usermodel.Cell infoCell = infoRow.createCell(0);

                switch (reportType) {
                    case "Doanh thu theo ngày":
                        infoCell.setCellValue("Từ ngày " + fromDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                                " đến ngày " + toDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        break;
                    case "Doanh thu theo tuần":
                        infoCell.setCellValue("Tháng " + (monthComboBox.getSelectionModel().getSelectedIndex() + 1) +
                                " năm " + yearComboBox.getValue());
                        break;
                    case "Doanh thu theo tháng":
                        infoCell.setCellValue("Năm " + yearComboBox.getValue());
                        break;
                }

                // Tạo header cho bảng dữ liệu
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(3);
                org.apache.poi.ss.usermodel.Cell periodHeaderCell = headerRow.createCell(0);
                periodHeaderCell.setCellValue("Kỳ báo cáo");
                periodHeaderCell.setCellStyle(headerStyle);

                org.apache.poi.ss.usermodel.Cell revenueHeaderCell = headerRow.createCell(1);
                revenueHeaderCell.setCellValue("Doanh thu");
                revenueHeaderCell.setCellStyle(headerStyle);

                // Tạo dữ liệu cho bảng
                int rowNum = 4;
                BigDecimal total = BigDecimal.ZERO;

                for (RevenueData data : tableData) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(data.getPeriod());
                    row.createCell(1).setCellValue(data.getRevenue().doubleValue());

                    total = total.add(data.getRevenue());
                }

                // Tạo dòng tổng doanh thu
                org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowNum + 1);
                org.apache.poi.ss.usermodel.Cell totalLabelCell = totalRow.createCell(0);
                totalLabelCell.setCellValue("TỔNG CỘNG");
                totalLabelCell.setCellStyle(headerStyle);

                org.apache.poi.ss.usermodel.Cell totalValueCell = totalRow.createCell(1);
                totalValueCell.setCellValue(total.doubleValue());

                // Tự điều chỉnh độ rộng cột
                for (int i = 0; i < 2; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Lưu workbook vào file
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất báo cáo thành công!");

            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xuất báo cáo: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String formatMoney(BigDecimal amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    // Class chứa dữ liệu doanh thu cho TableView
    public static class RevenueData {
        private final String period;
        private final BigDecimal revenue;

        public RevenueData(String period, BigDecimal revenue) {
            this.period = period;
            this.revenue = revenue;
        }

        public String getPeriod() {
            return period;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public javafx.beans.property.StringProperty periodProperty() {
            return new javafx.beans.property.SimpleStringProperty(period);
        }

        public javafx.beans.property.ObjectProperty<BigDecimal> revenueProperty() {
            return new javafx.beans.property.SimpleObjectProperty<>(revenue);
        }
    }
}