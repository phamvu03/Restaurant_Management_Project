package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import com.restaurant.restaurant_management_project.util.ItemFillterData;
import com.restaurant.restaurant_management_project.util.enums.SortOrder;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.GridView;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ManageMenuItemView {
    @FXML
    private HBox searchContainer;
    @FXML
    private Button clearButton;
    @FXML
    private ListView<String> suggestName;
    //Các biến FXML
    @FXML
    private AnchorPane content;
    @FXML
    private TextField searchField;
    @FXML
    private Button filterBtn;
    @FXML
    private Button nameSort;
    @FXML
    private Button priceSort;
    @FXML
    private GridView<RMenuItem> itemList;
    private Popup filterPopup;
    private Stage itemInfoPopup;
    ItemInformationView popupController ;
    private MenuItemDaoImpl menuItemDao;
    //Danh sách món ăn
    private ObservableList<RMenuItem> menuItemObservableList ;
    private ObservableList<String> suggestionNames = FXCollections.observableArrayList();
    FilteredList<RMenuItem> filteredMenuItems;
    //Các biến thuộc tính danh sách
    private final int SPACING = 15;
    private final int THREE_COLUMN_WIDTH = 1000;
    private final int TWO__COLUMN_WIDTH = 660;
    //Trạng thái nút bấm
    private Map<String,SortOrder> sortOrderMap= new HashMap<>();
    private static final int ITEMS_PER_PAGE = 12;
    private int currentPage = 0;
    private int totalItems = 0;

    public void initialize() {
        menuItemDao = new MenuItemDaoImpl();
        System.out.println("Loaded");
        //Lấy dữ liệu
        loadData();

        // Đợi scene được khởi tạo trước khi bind size
        content.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                bindSize();
            }
        });

        suggestName.setOnMouseClicked(event -> {
            handleSuggestionSelection();
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSuggestions(newValue);
        });

        // Thêm scroll listener để load thêm dữ liệu khi cuộn
        itemList.setOnScroll(event -> {
            if (event.getDeltaY() < 0) { // Cuộn xuống
                if (currentPage * ITEMS_PER_PAGE < totalItems) {
                    loadMoreData();
                }
            }
        });
    }

    public void loadData() {
        try {
            menuItemObservableList = FXCollections.observableArrayList();
            totalItems = menuItemDao.getTotalCount();


            filteredMenuItems = new FilteredList<>(menuItemObservableList);
            itemList.setItems(filteredMenuItems);
            initializeGrid();
            Platform.runLater(() -> {
                loadMoreData();
            });

            suggestName.setItems(suggestionNames);
            suggestName.setVisible(false);
            sortOrderMap.put(nameSort.getId(),SortOrder.NONE);
            sortOrderMap.put(priceSort.getId(),SortOrder.NONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSuggestionSelection() {
        String selectedName = suggestName.getSelectionModel().getSelectedItem();
        if (selectedName != null && !selectedName.isEmpty()) {
            searchField.setText(selectedName); // Điền tên gợi ý vào TextField
            suggestName.setVisible(false); // Ẩn danh sách gợi ý
            suggestionNames.clear(); // Xóa gợi ý cũ

            // Tìm và hiển thị chi tiết của món được chọn
            Predicate<RMenuItem> nameFilterPredicate = item -> item.getItemName().equals(selectedName);
            filteredMenuItems.setPredicate(nameFilterPredicate);
            javafx.application.Platform.runLater(() -> suggestName.getSelectionModel().clearSelection());
        }
    }
    @FXML
    private void clearSearchText()
    {
        searchField.clear();
    }
    private void updateSuggestions(String inputText) {
        String searchText = inputText.trim().toLowerCase();

        if (searchText.isEmpty()) {
            clearButton.setVisible(false);
            searchContainer.getStyleClass().removeAll("typing-search-container");
            suggestionNames.clear();
            suggestName.setVisible(false);
            Predicate<RMenuItem> nameFilterPredicate = item ->true;
            filteredMenuItems.setPredicate(nameFilterPredicate);
        } else {
            List<String> filteredNames = menuItemObservableList.stream()
                    .map(RMenuItem::getItemName)
                    .filter(name -> name.toLowerCase().contains(searchText))
                    .limit(20)
                    .collect(Collectors.toList());
            clearButton.setVisible(true);
            suggestionNames.setAll(filteredNames);

            if ( filteredNames.isEmpty())
            {
                searchContainer.getStyleClass().removeAll("typing-search-container");
            }else {
                searchContainer.getStyleClass().add("typing-search-container");
            }
            suggestName.setVisible(!filteredNames.isEmpty());
        }
    }
    @FXML
    private void searchBtn()
    {
        performFullSearch(searchField.getText());
    }
    private void performFullSearch(String inputText) {
        Predicate<RMenuItem> nameFilterPredicate;
        String searchText = inputText.trim().toLowerCase();
        suggestName.setVisible(false);
        suggestionNames.clear();
        if (inputText.isEmpty())
        {
            nameFilterPredicate = item -> true;
            filteredMenuItems.setPredicate(nameFilterPredicate);
        }
        else
        {
            nameFilterPredicate = item -> item.getItemName().equals(inputText);
        }
        filteredMenuItems.setPredicate(nameFilterPredicate);
    }
    public void bindSize()
    {
        Scene scene = content.getScene();
        content.prefWidthProperty().bind(
                scene.widthProperty().multiply(0.1)
        );
//        scene.widthProperty().addListener((obs,old,newVa)->{
//            System.out.println(scene.getWidth());
//            System.out.println(content.getWidth());
//        });

        // Cấu hình kích thước ô và khoảng cách của gridview
        itemList.setCellHeight(350);
        itemList.setHorizontalCellSpacing(SPACING);
        itemList.setVerticalCellSpacing(15);
        itemList.widthProperty().addListener((obs, oldWidth, newWidth) -> adjustGridViewColumns(newWidth.doubleValue()));
        adjustGridViewColumns(itemList.getWidth());
        //Cấu hình vị trí của suggestion search
        suggestName.prefWidthProperty().bind(
                searchContainer.widthProperty()
        );

        final double CELL_HEIGHT = 35.0;
        suggestName.setFixedCellSize(CELL_HEIGHT);
        NumberBinding suggestHeight = Bindings.size(suggestionNames).multiply(CELL_HEIGHT).add(20
        );
        suggestName.maxHeightProperty().bind(suggestHeight);
        suggestName.prefHeightProperty().bind(suggestHeight);
        searchContainer.boundsInParentProperty().addListener(((observableValue, bounds, t1) ->
        {
            if(suggestName.isVisible())
            {
                Point2D posOfSearchContainer = searchContainer.localToScene(0,0);
                double containerHeight = searchContainer.getHeight();
                // Tính toán vị trí cho suggestName (ngay bên dưới searchContainer)
                double suggestNameTop = posOfSearchContainer.getY() + containerHeight;
                double suggestNameLeft = posOfSearchContainer.getX();
                AnchorPane.setTopAnchor(suggestName,suggestNameTop);
                AnchorPane.setLeftAnchor(suggestName,suggestNameLeft);
            }
        }));


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
            cellWidth = (width - (numColumns + 1) * SPACING - 90) / numColumns;
        else cellWidth = 280;
        itemList.setCellWidth(cellWidth);
    }
    @FXML
    private void showFilterPopup()
    {
        if(filterPopup == null)
        {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item-fillter-popup.fxml"));
                Parent popupContent = loader.load();
                ItemFilterPopup popupController = loader.getController();

                filterPopup = new Popup();
                filterPopup.getContent().add(popupContent);
                filterPopup.setAutoHide(true);
                // Consumer nhận FilterData và cập nhật Text
                popupController.initializeData(filterPopup, this::updateFilterResult); // Truyền phương thức tham chiếu

            } catch (IOException e) {
                System.err.println("Không thể tải PopupView.fxml: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        if (!filterPopup.isShowing()) {
            Window ownerWindow = filterBtn.getScene().getWindow();
            double popupX = filterBtn.localToScreen(filterBtn.getBoundsInLocal()).getMinX() - 100;
            // Hiển thị popup ngay dưới nút, căn trái với nút
            double popupY = filterBtn.localToScreen(filterBtn.getBoundsInLocal()).getMaxY() + 5;
            filterPopup.show(ownerWindow, popupX, popupY);
        } else {
            filterPopup.hide();
        }
    }
    private void updateFilterResult(ItemFillterData data) {
        if (data == null) {
            System.err.println("Dữ liệu không hợp lệ ");
            return;
        }
        System.err.println(data.status());
        // Tạo Predicate dựa trên trạng thái
        Predicate<RMenuItem> statusFilterPredicate;
        if(data.status() == null)
        {
            statusFilterPredicate = item -> true;

        } else if (data.status().equals(Boolean.FALSE)) {
            statusFilterPredicate = item -> !item.isItemStatus();
        }else {
            statusFilterPredicate = RMenuItem::isItemStatus;
        }
        // Tạo Predicate dựa trên giá
        Predicate<RMenuItem> priceFilterPredicate = item -> item.getItemPrice().compareTo(data.minPrice())>0 && item.getItemPrice().compareTo(data.maxPrice())<0;

        //Tạo Predicate dựa trên phân loại
        Set<String> catas = new HashSet<>(data.selectedCategories());
        Predicate<RMenuItem> cataFilterPredicate = item -> catas.contains(item.getItemCategory());
        // Áp dụng bộ lọc (predicate) cho FilteredList
        Predicate<RMenuItem> combinedPredicate = statusFilterPredicate
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
                    menuItemObservableList.sort(Comparator.comparing(RMenuItem::getItemPrice));
                } else {
                    menuItemObservableList.sort(Comparator.comparing(RMenuItem::getItemPrice).reversed());
                }
                break;
            case "nameSort":
                if ((sortOrderMap.get(buttonId) == SortOrder.ASCENDING)) {
                    menuItemObservableList.sort(Comparator.comparing(RMenuItem::getItemName));
                } else {
                    menuItemObservableList.sort(Comparator.comparing(RMenuItem::getItemName).reversed());
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
    //XEM THONG tin
    private void showInfo(RMenuItem item)
    {
        System.out.println(item.getItemName());
        showInformationPopup();
        popupController.setData(item);
        popupController.manageInfo();
        popupController.setDataConsumer(this::delete);
        popupController.setUpadateDataConsumer(r->{
            menuItemDao.update(r);
        });
    }
    //chuc năng xóa
    private void delete(RMenuItem menuItem)
    {
        menuItemDao.delete(menuItem);
        totalItems--;
        menuItemObservableList.remove(menuItem);
        itemInfoPopup = null;
    }
    //Chức năng thêm
    @FXML
    private void handleAddButton()
    {
        showInformationPopup();
        popupController.setOnCancel(()->{
            itemInfoPopup = null;
        });
        popupController.setUpAdd();
        // popupController.setDataConsumer(menuItemDao::add);
        popupController.setDataConsumer(rMenuItem -> {
            menuItemObservableList.add(rMenuItem);
            menuItemDao.add(rMenuItem);
            itemInfoPopup = null;
        });
    }
    private void showInformationPopup()
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/item-information-view.fxml"));
            Parent popupContent = loader.load();
            popupController = loader.getController();

            itemInfoPopup = new Stage();
            Window ownerWindow = itemList.getScene().getWindow();
            itemInfoPopup.initOwner(ownerWindow);
            itemInfoPopup.initModality(Modality.WINDOW_MODAL);

            itemInfoPopup.setScene(new Scene(popupContent));

            popupController.setMenuItemObservableList(menuItemObservableList);
            // popupStage.show();
            itemInfoPopup.show();
            popupController.setOwnerPopup(itemInfoPopup);
            popupController.setMenuItemObservableList(menuItemObservableList);
        } catch (IOException e) {
            System.err.println("Không thể tải PopupView.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void backToHome(ActionEvent event) {
        Scene thisScene = content.getScene();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-screen-view.fxml"));
        try {
            thisScene.setRoot(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMoreData() {
        try {
            if (currentPage * ITEMS_PER_PAGE >= totalItems) {
                return;
            }

            List<RMenuItem> newItems = menuItemDao.getAll(currentPage * ITEMS_PER_PAGE, ITEMS_PER_PAGE);
            menuItemObservableList.addAll(newItems);
            currentPage++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeGrid() {
        itemList.setCellFactory(v -> {
            return new ItemView(menuItem -> {
                Platform.runLater(() -> showInfo(menuItem));
            });
        });

        itemList.setHorizontalCellSpacing(SPACING);
        itemList.setVerticalCellSpacing(SPACING);
    }
}
