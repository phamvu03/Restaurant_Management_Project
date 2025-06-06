package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.dao.ReportDAO;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.GridView;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class DashBoard {

    public Label revenue;
    public Label todayRevenueCompare;
    public Label totalOrder;
    public Label todayOrderCompare;
    public PieChart usedTable;
    public Label usedTableText;
    public Label orderedTable;
    public LineChart<String, Double> benefitChart;
    public BarChart<String,Integer> popularTimeChart;
    public PieChart revenueByCate;
    public ListView<RMenuItem> popularItemList;

    private final MenuItemDaoImpl menuItemDao = new MenuItemDaoImpl();
    private final ReportDAO reportDAO = new ReportDAO();
    public HBox firstRowHBox;
    public HBox secondRowHBox;
    public VBox container;
    //data
    private BigDecimal todayRevenueData;
    private BigDecimal yesRevenueData;
    private int todayOrderNum;
    private int yesOrderNum;
    private int tableInUse;
    private int totalTable;
    private int orderingTable;

    Map<String, BigDecimal> doanhThuTheoTuan;
    Map<String, BigDecimal> doanhThuTheoThang;
    Map<String, BigDecimal> doanhThuTheoNam;
    Map<String,Integer> soLuongTheoDanhMuc;
    Map<Integer,Integer> khachTheoGio;

    private ObservableList<PieChart.Data> usedTableDataList;
    private ObservableList<PieChart.Data> saleByCate;
    private ObservableList<XYChart.Series<String, Double>> benfitChartData;
    private ObservableList<XYChart.Series<String, Integer>> popularTimeData;
    private ObservableList<RMenuItem> populateItemData;

    public void initialize() {
        popularItemList.setFocusTraversable(false);
        container.requestFocus();
        usedTableDataList = FXCollections.observableArrayList();
        benfitChartData = FXCollections.observableArrayList();
        popularTimeData = FXCollections.observableArrayList();
        populateItemData = FXCollections.observableArrayList();
        doanhThuTheoTuan =  new LinkedHashMap<>();
        doanhThuTheoNam = new LinkedHashMap<>();
        doanhThuTheoThang = new LinkedHashMap<>();
        saleByCate = FXCollections.observableArrayList();
        Task<Void> loadDataTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                LocalDate today = LocalDate.now();
                LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDateTime cuoiNgay = LocalDateTime.of(
                        LocalDateTime.now().toLocalDate(),
                        LocalTime.of(0, 0, 0)).plusHours(23).plusMinutes(59).plusSeconds(59);
                todayRevenueData = reportDAO.getBenefitByDate(today);
                yesRevenueData = reportDAO.getBenefitByDate(today.minusDays(1));

                todayOrderNum = reportDAO.getOderByDate(today);
                yesOrderNum = reportDAO.getOderByDate(today.minusDays(1));

                totalTable = reportDAO.geTotalTable();
                tableInUse = reportDAO.getTableInUse(LocalDateTime.now());
                usedTableDataList.addAll(new PieChart.Data("Đang sử dụng",(double) tableInUse/totalTable),
                new PieChart.Data("Trống", (double) (totalTable - tableInUse) /totalTable)
                );

                orderingTable = reportDAO.getOrderedTable(LocalDateTime.now(),cuoiNgay);

                //doanh thu
                doanhThuTheoTuan = reportDAO.getDoanhThuTheoThu(startOfWeek,today);
                doanhThuTheoNam = reportDAO.getDoanhThuTheoThang(today.getYear());
                doanhThuTheoThang = reportDAO.getDoanhThuTheoTuanTrongThang(today.getMonthValue(),today.getYear());

                XYChart.Series<String,Double> data = new XYChart.Series<>();
                for(Map.Entry<String, BigDecimal> entry: doanhThuTheoTuan.entrySet())
                {
                    data.getData().add(new XYChart.Data<>(entry.getKey(),formatToDouble(entry.getValue())));
                }
                benfitChartData.add(data);

                //so luong ban theo danh muc
                soLuongTheoDanhMuc = reportDAO.getSalesByCategory();
                long totalSale = 0;
                for(Map.Entry<String, Integer> entry: soLuongTheoDanhMuc.entrySet())
                {
                    totalSale+= entry.getValue();
                }

                for(Map.Entry<String, Integer> entry: soLuongTheoDanhMuc.entrySet())
                {
                    saleByCate.add(new PieChart.Data(entry.getKey(),(double) entry.getValue()/totalSale));
                }
                //Gio dong khach
                XYChart.Series<String,Integer> data2 = new XYChart.Series<>();
                khachTheoGio = reportDAO.getThongKeKhachTheoGio(today.minusDays(1),today.minusDays(1));
                for(Map.Entry<Integer, Integer> entry: khachTheoGio.entrySet())
                {
                    data2.getData().add(new XYChart.Data<>(entry.getKey()+"",entry.getValue()));
                }
                populateItemData.addAll( menuItemDao.getPopularMenuItems(10, Date.valueOf(startOfWeek),Date.valueOf(today)));

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

        orderedTable.setText(orderingTable+"");

        benefitChart.setData(benfitChartData);

        revenueByCate.setData(saleByCate);

        popularTimeChart.setData(popularTimeData);

        popularItemList.setItems(populateItemData);

        popularItemList.setCellFactory(v->{
            return new ItemListCell();
        });
        adjustListViewHeight();
    }
    public static double formatToDouble(BigDecimal soTien) {
        if (soTien == null) {
            return 0;
        }

        // Chuyển đổi sang đơn vị triệu
        BigDecimal soTienTrieu = soTien.divide(new BigDecimal(1000000), 2, RoundingMode.HALF_UP);

        return soTienTrieu.doubleValue();
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
    private void adjustListViewHeight() {
        // Set a fixed cell height if not already set in FXML
        double cellHeight = popularItemList.getFixedCellSize() > 0 ?
                popularItemList.getFixedCellSize() : 80; // default height

        // Calculate total height: cell height * number of items
        double totalHeight = cellHeight * popularItemList.getItems().size();

        // Add a small buffer for cell spacing and borders (adjust as needed)
        totalHeight += 10;

        // Set preferred height to calculated height
        popularItemList.setPrefHeight(totalHeight);

        // Ensure max height is sufficient
        popularItemList.setMaxHeight(totalHeight);
    }
}

