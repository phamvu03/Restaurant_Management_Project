package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.GridCell;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ItemView extends GridCell<RMenuItem> {

    // Các biến FXML
    @FXML private ImageView itemImage;
    @FXML private Text itemName;
    @FXML private Text itemCate;
    @FXML private Text itemPrice;
    @FXML private Text itemStatus;
    @FXML private VBox itemContainer; // This is the root node defined in FXML
    // --- State for the cell ---
    private boolean fxmlLoaded = false;
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private static final Image PLACEHOLDER_IMAGE = new Image(ItemView.class.getResource("/com/app/nguyenhungthanh_2022601002/image/em.png").toExternalForm());
    private final Consumer<RMenuItem> clickConsumer;
    private CompletableFuture<?> currentImageLoadTask;
    private static final int MAX_CACHE_SIZE = 100;
    private static final Map<String, Image> imageCache = new LinkedHashMap<String, Image>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    public ItemView(Consumer<RMenuItem> clickConsumer){
        getStyleClass().add("item-cell");
        this.clickConsumer = clickConsumer;
        setOnMouseClicked((MouseEvent event) -> {
            if (!isEmpty() && getItem() != null && this.clickConsumer != null) {
                this.clickConsumer.accept(getItem());
            }
        });

    }

    @Override
    protected void updateItem(RMenuItem item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
            return;
        }

        if (!fxmlLoaded) {
            loadFXML();
        }

        // Update UI with menu item data
        setText(null);
        setGraphic(itemContainer);

        // Cập nhật text trước để UI phản hồi nhanh hơn
        itemName.setText(item.getItemName());
        itemPrice.setText(String.format("%,.0f", item.getItemPrice()));
        itemCate.setText(item.getItemCategory());
        itemStatus.setText(item.isItemStatus() ? "Còn hàng" : "Hết hàng");
        itemStatus.getStyleClass().setAll(item.isItemStatus() ? "status-available" : "status-unavailable");

        // Hủy task load ảnh cũ nếu có
        if (currentImageLoadTask != null) {
            currentImageLoadTask.cancel(true);
        }

        // Kiểm tra cache trước
        String cacheKey = item.getItemId() + "_" + item.getItemName();
        Image cachedImage = imageCache.get(cacheKey);
        if (cachedImage != null) {
            itemImage.setImage(cachedImage);
            return;
        }

        // Load ảnh bất đồng bộ với kích thước tối ưu
        currentImageLoadTask = CompletableFuture.runAsync(() -> {
            try {
                byte[] imageData = item.getItemImage();
                if (imageData != null) {
                    // Tạo ảnh với kích thước tối ưu
                    Image image = new Image(new ByteArrayInputStream(imageData));

                    // Lưu vào cache
                    synchronized (imageCache) {
                        imageCache.put(cacheKey, image);
                    }

                    Platform.runLater(() -> {
                        if (!isEmpty()) {
                            itemImage.setImage(image);
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        if (!isEmpty()) {
                            itemImage.setImage(PLACEHOLDER_IMAGE);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    if (!isEmpty()) {
                        itemImage.setImage(PLACEHOLDER_IMAGE);
                    }
                });
            }
        });
    }
    private void loadFXML() {
        if (fxmlLoaded) {
            return; // Đã tải cho instance này rồi
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            URL fxmlUrl = getClass().getResource("/com/app/nguyenhungthanh_2022601002/fxml/item-view.fxml");

            if (fxmlUrl == null) {
                System.err.println("CRITICAL ERROR: Cannot find FXML file: /com/app/nguyenhungthanh_2022601002/fxml/item-view.fxml");
                return;
            }

            loader.setLocation(fxmlUrl);
            loader.setController(this);
            loader.load();
            // Cấu hình UI
            itemContainer.prefWidthProperty().bind(getGridView().cellWidthProperty());
            itemContainer.prefHeightProperty().bind(getGridView().cellHeightProperty());
            itemImage.setFitWidth(getGridView().getCellWidth() - 10);
            itemName.wrappingWidthProperty().bind(getGridView().cellWidthProperty().subtract(10));
            fxmlLoaded = true;
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Failed to load FXML for ItemView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

