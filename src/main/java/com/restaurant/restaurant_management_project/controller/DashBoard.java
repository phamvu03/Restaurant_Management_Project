package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.ReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.controlsfx.control.GridView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

public class DashBoard {

    public Label revenue;
    public Label todayRevenueCompare;
    public Label totalOrder;
    public Label todayOrderCompare;
    public PieChart usedTable;
    public Label usedTableText;
    public Label orderedTable;
    public LineChart<String, BigDecimal> benefitChart;
    public BarChart popularTimeChart;
    public PieChart revenueByCate;
    public PieChart tableOrderState;
    public GridView popularItemList;

    private final ReportDAO reportDAO = new ReportDAO();
    //data
    private BigDecimal todayRevenueData;
    private BigDecimal yesRevenueData;
    private int todayOrderNum;
    private int yesOrderNum;
    private int tableInUse;
    private int totalTable;
    private ObservableList<PieChart.Data> usedTableDataList;
    private ObservableList<XYChart.Series<String, Long>> barChartSeriesList;
    public void initialize() {
        usedTableDataList = FXCollections.observableArrayList();
        barChartSeriesList = FXCollections.observableArrayList();
        Task<Void> loadDataTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                todayRevenueData = reportDAO.getBenefitByDate(LocalDate.now());
                yesRevenueData = reportDAO.getBenefitByDate(LocalDate.now().minusDays(1));

                todayOrderNum = reportDAO.getOderByDate(LocalDate.now());
                yesOrderNum = reportDAO.getOderByDate(LocalDate.now().minusDays(1));

                totalTable = reportDAO.geTotalTable();
                tableInUse = reportDAO.getTableInUse(LocalDateTime.now());

                usedTableDataList.addAll(new PieChart.Data("Đang sử dụng",(double) tableInUse/totalTable));


                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateUI();
            }

            @Override
            protected void failed() {
                super.failed();
                Throwable ex = getException();
                System.err.println("Lỗi tải dữ liệu cho Dashboard: " + ex.getMessage());
                ex.printStackTrace();
                revenue.setText("Lỗi tải dữ liệu");
            }
        };
        Thread thread = new Thread(loadDataTask);
        thread.start();
    }

    private void updateUI() {
        //daonh thu
        revenue.setText(formatMn(todayRevenueData));
        if (todayRevenueData.compareTo(yesRevenueData)>=0)
        {
            todayRevenueCompare.setText("Tăng "+ compareValue(todayRevenueData,yesRevenueData)+"% so với hôm qua");
            todayRevenueCompare.getStyleClass().add("");
        }
        else
        {
            todayRevenueCompare.setText("Giảm "+ compareValue(todayRevenueData,yesRevenueData)+"%");
            todayRevenueCompare.getStyleClass().add("");
        }
        //so don hang
        totalOrder.setText(todayOrderNum+"");
        if (todayOrderNum>=yesOrderNum)
        {
            todayOrderCompare.setText("Tăng "+compareValue(todayOrderNum,yesOrderNum)+"% so với hôm qua");
            todayOrderCompare.getStyleClass().add("");
        }else {
            todayOrderCompare.setText("Giảm "+compareValue(todayOrderNum,yesOrderNum)+"% so với hôm qua");
            todayOrderCompare.getStyleClass().add("");
        }

        usedTable.setData(usedTableDataList);
        usedTableText.setText(tableInUse+"/"+totalTable);


    }
    public static String formatMn(BigDecimal soTien) {
        if (soTien == null) {
            return "0 triệu VND";
        }

        // Chuyển đổi sang đơn vị triệu
        BigDecimal soTienTrieu = soTien.divide(new BigDecimal(1000000), 2, RoundingMode.HALF_UP);

        // Sử dụng NumberFormat cho locale Việt Nam
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);

        return formatter.format(soTienTrieu) + " triệu VND";
    }
    private BigDecimal compareValue(BigDecimal a,BigDecimal b)
    {
        // Tính chênh lệch
        BigDecimal chenhLech = a.subtract(b).abs();

        // Tính phần trăm tăng/giảm
        if (b.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal phanTram = chenhLech.multiply(new BigDecimal(100))
                    .divide(b, 2, RoundingMode.HALF_UP);
            return phanTram;
        } else {
            return new BigDecimal(100);
        }
    }
    private double compareValue(int a,int b)
    {
        // Tính chênh lệch
        int chenhLech = Math.abs(a-b);

        // Tính phần trăm tăng/giảm
        if (b > 0) {
            double phanTram = (double) chenhLech/b *100;
            return phanTram;
        } else {
            return 100;
        }
    }
}

