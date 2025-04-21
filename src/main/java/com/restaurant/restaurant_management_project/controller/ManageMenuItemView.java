package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.MenuItem;
import com.restaurant.restaurant_management_project.util.ItemFillterData;
import com.restaurant.restaurant_management_project.util.compare.ItemNameComparator;
import com.restaurant.restaurant_management_project.util.compare.ItemPriceComparator;
import com.restaurant.restaurant_management_project.util.enums.SortOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.controlsfx.control.GridView;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;

public class ManageMenuItemView {
    //Các biến FXML
    @FXML
    private StackPane content;
    @FXML
    private Button addItemBtn;
    @FXML
    private VBox sideBar;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private Button filterBtn;
    @FXML
    private Button nameSort;
    @FXML
    private Button priceSort;
    @FXML
    private GridView<MenuItem> itemList;
    private Popup popup;
    //Danh sách món ăn
    private ObservableList<MenuItem> menuItemObservableList ;
    FilteredList<MenuItem> filteredMenuItems;
    //Các biến thuộc tính danh sách
    private final int SPACING = 10;
    private final int THREE_COLUMN_WIDTH = 1000;
    private final int TWO__COLUMN_WIDTH = 660;
    //Trạng thái nút bấm
    private Map<String,SortOrder> sortOrderMap= new HashMap<>();
    public void initialize() {
        Platform.runLater(this::bindToWindow);
        loadData();
        sortOrderMap.put(nameSort.getId(),SortOrder.NONE);
        sortOrderMap.put(priceSort.getId(),SortOrder.NONE);
    }
    public void bindToWindow()
    {
        Scene scene = content.getScene();
        content.prefHeightProperty().bind(
                scene.heightProperty()
        );
        content.prefWidthProperty().bind(
                scene.widthProperty().multiply(0.8)
        );
        // Cấu hình kích thước ô và khoảng cách
        itemList.setCellHeight(300);
        itemList.setHorizontalCellSpacing(SPACING);
        itemList.setVerticalCellSpacing(15);
        itemList.widthProperty().addListener((obs, oldWidth, newWidth) -> adjustGridViewColumns(newWidth.doubleValue()));
        adjustGridViewColumns(itemList.getWidth());

    }
    public void loadData()
    {
        menuItemObservableList = FXCollections.observableArrayList();
        System.out.println(getClass().getResource("/com/app/restaurantmanagement/image/em.png"));
        Image image = new Image(getClass().getResource("/com/app/restaurantmanagement/image/em.png").toExternalForm());
        menuItemObservableList.addAll(
                new MenuItem("1","Banh ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,true),
                new MenuItem("1","Con ca",image,new BigDecimal(200000), 0.01F,"cai","an kem",null,false),
                new MenuItem("1","Banh ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,true),
                new MenuItem("1","Chim ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,true),
                new MenuItem("1","Banh ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,false),
                new MenuItem("1","Anh ca",image,new BigDecimal(500000), 0.01F,"cai","an kem",null,true),
                new MenuItem("1","Banh ca",image,new BigDecimal(140000), 0.01F,"cai","an kem",null,false),
                new MenuItem("1","Tan ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,false),
                new MenuItem("1","Banh ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,true),
                new MenuItem("1","Banh ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,false),
                new MenuItem("1","Banh ca",image,new BigDecimal(100000), 0.01F,"cai","an kem",null,false)
        );
        filteredMenuItems = new FilteredList<>(menuItemObservableList);
        itemList.setItems(filteredMenuItems); // Gán danh sách dữ liệu
        itemList.setCellFactory(v->new ItemView());
    }
    private void adjustGridViewColumns(double width) {
        int numColumns; // Số lượng cột
        double cellWidth; // Chiều rộng của mỗi cell

        if (width >= THREE_COLUMN_WIDTH) {
            numColumns = 4; // 4 cột nếu kích thước >= THREE_COLUMN_WIDTH
        } else if (width >= TWO__COLUMN_WIDTH) {
            numColumns = 3; // 3 cột nếu kích thước >= TWO__COLUMN_WIDTH
        }  else {
            numColumns = 2; // 2 cột nếu kích thước nhỏ hơn 660px
        }
        // Tính toán chiều rộng mỗi cell
        if (numColumns>2)
            cellWidth = (width - (numColumns + 1) * SPACING - 50) / numColumns;
        else cellWidth = 280;
        itemList.setCellWidth(cellWidth);
        // Cập nhật chiều rộng và số cột cho GridView
        System.out.println(cellWidth);
    }
    @FXML
    private void showFilterPopup()
    {
        if(popup == null)
        {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/app/restaurantmanagement/fxml/item-fillter-popup.fxml"));
                Parent popupContent = loader.load();
                ItemFilterPopup popupController = loader.getController();

                popup = new Popup();
                popup.getContent().add(popupContent);
                popup.setAutoHide(true);
                // Consumer nhận FilterData và cập nhật Text
                popupController.initializeData(popup, this::updateFilterResult); // Truyền phương thức tham chiếu

            } catch (IOException e) {
                System.err.println("Không thể tải PopupView.fxml: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        if (!popup.isShowing()) {
            Window ownerWindow = filterBtn.getScene().getWindow();
            double popupX = filterBtn.localToScreen(filterBtn.getBoundsInLocal()).getMinX();
            // Hiển thị popup ngay dưới nút, căn trái với nút
            double popupY = filterBtn.localToScreen(filterBtn.getBoundsInLocal()).getMaxY() + 5;
            popup.show(ownerWindow, popupX, popupY);
        } else {
            popup.hide();
        }
    }/**
     * Cập nhật danh sách hiển thị kết quả dựa trên FilterData nhận được.
     * @param data Dữ liệu lọc từ PopupController.
     */
    private void updateFilterResult(ItemFillterData data) {
        if (data == null) {
            System.err.println("Dữ liệu không hợp lệ ");
            return;
        }
        System.err.println(data.status());
        // Tạo Predicate dựa trên trạng thái
        Predicate<MenuItem> statusFilterPredicate;
        if(data.status() == null)
        {
            statusFilterPredicate = item -> true;

        } else if (data.status().equals(Boolean.FALSE)) {
            statusFilterPredicate = item -> !item.isStatus();
        }else {
            statusFilterPredicate = MenuItem::isStatus;
        }
        // Tạo Predicate dựa trên giá
        Predicate<MenuItem> priceFilterPredicate = item -> item.getPrice().compareTo(data.minPrice())>0 && item.getPrice().compareTo(data.maxPrice())<0;

        //Tạo Predicate dựa trên phân loại
        Set<String> catas = new HashSet<>(data.selectedCategories());
        Predicate<MenuItem> cataFilterPredicate = item -> catas.contains(item.getCategory());
        // Áp dụng bộ lọc (predicate) cho FilteredList
        Predicate<MenuItem> combinedPredicate = statusFilterPredicate
                .and(priceFilterPredicate)
                .and(cataFilterPredicate);
        filteredMenuItems.setPredicate(combinedPredicate);
    }

    @FXML
    private void sort(ActionEvent event) {
        Object source = event.getSource();
        Button clickedButton = (Button) source;
        String buttonId = clickedButton.getId();

        if(clickedButton == nameSort)
        {
            if (sortOrderMap.get(priceSort.getId()) != SortOrder.NONE)
            {
                sortOrderMap.put(priceSort.getId(),SortOrder.NONE);
                updateButtonStage(priceSort);
            }
        }else if (clickedButton == priceSort)
        {
            if (sortOrderMap.get(nameSort.getId()) != SortOrder.NONE)
            {
                sortOrderMap.put(nameSort.getId(),SortOrder.NONE);
                updateButtonStage(nameSort);
            }
        }
        switch (sortOrderMap.get(buttonId))
        {
            case NONE:
                sortOrderMap.put(buttonId,SortOrder.DESCENDING);
                updateButtonStage(clickedButton);
                performSort(buttonId);
                break;
            case DESCENDING:
                sortOrderMap.put(buttonId,SortOrder.ASCENDING);
                updateButtonStage(clickedButton);
                performSort(buttonId);
                break;
            case ASCENDING:
                sortOrderMap.put(buttonId,SortOrder.DESCENDING);
                updateButtonStage(clickedButton);
                performSort(buttonId);
                break;
        }
        System.out.println(sortOrderMap.get(clickedButton.getId()));
    }

    private void performSort(String buttonId) {

        switch (buttonId) {
            case "priceSort":
                // Cần có Comparator riêng cho tăng/giảm hoặc Comparator nhận tham số
                if ((sortOrderMap.get(buttonId) == SortOrder.ASCENDING)) {
                    menuItemObservableList.sort(Comparator.comparing(MenuItem::getPrice));
                } else {
                    menuItemObservableList.sort(Comparator.comparing(MenuItem::getPrice).reversed());
                }
                break;
            case "nameSort":
                if ((sortOrderMap.get(buttonId) == SortOrder.ASCENDING)) {
                    menuItemObservableList.sort(Comparator.comparing(MenuItem::getName));
                } else {
                    menuItemObservableList.sort(Comparator.comparing(MenuItem::getName).reversed());
                }
                break;
        }
    }

    private void updateButtonStage(Button button) {
        FontIcon sortIcon ;
        sortIcon = new FontIcon();
        sortIcon.setIconSize(20);
        switch (sortOrderMap.get(button.getId()))
        {
            case NONE:
                sortIcon.setIconLiteral("bi-chevron-expand");
                button.setGraphic(sortIcon);
                break;
            case DESCENDING:
                sortIcon.setIconLiteral("bi-arrow-down");
                button.setGraphic(sortIcon);
                break;
            case ASCENDING:
                sortIcon.setIconLiteral("bi-arrow-up");
                button.setGraphic(sortIcon);
                break;
        }
    }
}
