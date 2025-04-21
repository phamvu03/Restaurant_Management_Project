package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.util.ItemFillterData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.controlsfx.control.RangeSlider;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemFilterPopup {
    @FXML
    public MenuItem all;
    @FXML
    public MenuItem available;
    @FXML
    public MenuItem unavailable;
    @FXML
    private MenuButton statusMenuButton;
    @FXML
    private RangeSlider priceRangeSlider;
    @FXML
    private VBox categoryVBox;

    private Popup ownerPopup;
    private Consumer<ItemFillterData> dataConsumer;

    // Danh sách phân loại có thể có
    private final List<String> CATEGORY_OPTIONS = List.of("an kem", "Gia dụng", "Thời trang", "Sách", "Thể thao", "Mẹ & Bé");

    private Boolean currentStatus = null;
    private final List<CheckBox> categoryCheckBoxes = new ArrayList<>(); // Giữ tham chiếu đến các checkbox

    // Định dạng số tiền tệ
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        setupCategoryCheckBoxes();
        setupRangeSliderListeners();
        // Đặt giá trị ban đầu cho MenuButton text
        statusMenuButton.setText("Tất cả");
    }
    @FXML
    private void selectStatus(ActionEvent event)
    {
        MenuItem clickedMenu = (MenuItem) event.getSource();
        switch (clickedMenu.getId())
        {
            case "all":
                currentStatus = null;
                statusMenuButton.setText("Tất cả");
                notifyDataChanged();
                break;
            case "available":
                currentStatus = true;
                statusMenuButton.setText("Còn hàng");
                notifyDataChanged();
                break;
            case "unavailable":
                currentStatus = false;
                statusMenuButton.setText("Hết hàng");
                notifyDataChanged();
                break;
        }
    }
    /** Tạo và thêm các CheckBox phân loại vào VBox */
    private void setupCategoryCheckBoxes() {
        categoryVBox.getChildren().clear();
        categoryCheckBoxes.clear();
        for (String category : CATEGORY_OPTIONS) {
            CheckBox cb = new CheckBox(category);
            cb.setSelected(true);
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> notifyDataChanged()); // Lắng nghe thay đổi
            categoryCheckBoxes.add(cb);
            categoryVBox.getChildren().add(cb);
        }
    }

    /** Thiết lập listeners cho RangeSlider */
    private void setupRangeSliderListeners() {
        // Cập nhật khi người dùng *thả chuột* ra khỏi slider (ít cập nhật hơn là khi kéo)
        priceRangeSlider.lowValueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) { // Chỉ cập nhật khi isChanging == false (đã thả chuột)
                notifyDataChanged();
            }
//            updateSliderTooltip(); // Cập nhật tooltip ngay cả khi đang kéo
        });
        priceRangeSlider.highValueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                notifyDataChanged();
            }
//            updateSliderTooltip();
        });
        // Cập nhật tooltip ban đầu
//        Platform.runLater(this::updateSliderTooltip); // Đảm bảo slider đã được vẽ
    }

    /**
     * Phương thức này được gọi từ MainViewController để truyền các tham chiếu cần thiết.
     */
    public void initializeData(Popup ownerPopup, Consumer<ItemFillterData> dataConsumer) {
        this.ownerPopup = ownerPopup;
        this.dataConsumer = dataConsumer;
        // Gửi trạng thái ban đầu khi popup được hiển thị
        notifyDataChanged();
    }
    /** Thu thập dữ liệu hiện tại từ các control và gửi về MainViewController */
    private void notifyDataChanged() {
        if (dataConsumer == null) {
            System.err.println("Dữ liệu không có ");
            return;
        }

        // Lấy giá trị từ RangeSlider
        BigDecimal minPrice = BigDecimal.valueOf(priceRangeSlider.getLowValue());
        BigDecimal maxPrice = BigDecimal.valueOf(priceRangeSlider.getHighValue());

        // Lấy danh sách các phân loại được chọn
        List<String> selectedCategories = categoryCheckBoxes.stream()
                .filter(CheckBox::isSelected)
                .map(CheckBox::getText)
                .collect(Collectors.toList());
        // Tạo đối tượng FilterData
        ItemFillterData currentFilterData = new ItemFillterData(currentStatus, minPrice, maxPrice, selectedCategories);
        // Gửi dữ liệu
        dataConsumer.accept(currentFilterData);
        //System.out.println("Popup Data Changed: " + currentFilterData); // Để debug
    }
}
