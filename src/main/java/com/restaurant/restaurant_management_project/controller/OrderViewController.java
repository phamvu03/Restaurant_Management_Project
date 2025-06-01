/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver; // Ví dụ sử dụng PopOver từ ControlsFX

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.geometry.Insets;

public class OrderViewController {

    @FXML
    private FlowPane menuItemsPane;

    @FXML
    private VBox orderItemsVBox;

    @FXML
    private Label lblCartItemCount;

    @FXML
    private Label lblCartTotal;

    @FXML
    private Button btnThanhToan;

    @FXML
    private TextField txtGhiChu;

    // Định dạng số tiền
    private final DecimalFormat currencyFormat = new DecimalFormat("#,### đ");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // Dữ liệu mẫu cho các món ăn (nên tải từ database hoặc file)
    private List<RMenuItem> allMenuItems;

    // Danh sách các món trong giỏ hàng
    private Map<RMenuItem, OrderItem> currentOrder = new HashMap<>();

    private Timeline longPressTimeline;
    private RMenuItem currentLongPressItem;
    private Node longPressNode;

    // Thời gian giữ để coi là long press (miligiây)
    private static final int LONG_PRESS_DURATION = 700;


    @FXML
    public void initialize() {
        loadMenuItems(); // Tải danh sách món ăn
//        renderMenuItems(); // Hiển thị món ăn lên giao diện
        updateCartSummary(); // Cập nhật thông tin giỏ hàng ban đầu
    }

    MenuItemDaoImpl dao = new MenuItemDaoImpl();
    
    // Lớp để lưu trữ thông tin món trong giỏ hàng
    private static class OrderItem {
        RMenuItem menuItem;
        int quantity;
        String addedTime;
        

        public OrderItem(RMenuItem menuItem, int quantity, String addedTime) {
            this.menuItem = menuItem;
            this.quantity = quantity;
            this.addedTime = addedTime;
        }
    }

    private void loadMenuItems() {
        
        allMenuItems = new ArrayList<>();
        // Thêm các món ăn vào đây (ví dụ)
        allMenuItems = dao.getAll();
    }

//    private void renderMenuItems() {
//        menuItemsPane.getChildren().clear();
//        for (RMenuItem item : allMenuItems) {
//            VBox menuItemBox = createMenuItemNode(item);
//            menuItemsPane.getChildren().add(menuItemBox);
//        }
//    }

//    private VBox createMenuItemNode(RMenuItem itemData) {
//        ImageView imageView = new ImageView();
//        try {
//            // Quan trọng: Đường dẫn ảnh phải đúng và ảnh phải nằm trong resources
//            // Ví dụ nếu ảnh nằm trong src/main/resources/images/ten_anh.jpg
//            // thì đường dẫn sẽ là "/images/ten_anh.jpg"
//            Image image = new Image(getClass().getResourceAsStream(itemData.imagePath));
//            imageView.setImage(image);
//        } catch (Exception e) {
//            // System.err.println("Không thể tải ảnh: " + itemData.imagePath + " - " + e.getMessage());
//            // Có thể đặt ảnh placeholder ở đây
//            imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png"))); // Cần có ảnh placeholder
//        }
//        imageView.setFitHeight(80.0);
//        imageView.setFitWidth(100.0);
//        imageView.setPreserveRatio(true);
//
//        Label nameLabel = new Label(itemData.name);
//        nameLabel.setStyle("-fx-text-alignment: center; -fx-wrap-text: true;");
//
//
//        Label priceLabel = new Label(currencyFormat.format(itemData.price));
//        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
//
//        VBox menuItemBox = new VBox(5, imageView, nameLabel, priceLabel);
//        menuItemBox.setAlignment(Pos.CENTER);
//        menuItemBox.setPrefHeight(160.0); // Tăng chiều cao để chữ không bị cắt
//        menuItemBox.setPrefWidth(120.0);
//        menuItemBox.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5;");
//        menuItemBox.setUserData(itemData); // Lưu trữ dữ liệu món ăn
//
//        // Xử lý sự kiện click
//        menuItemBox.setOnMouseClicked(this::handleMenuItemClicked);
//
//        // Xử lý sự kiện nhấn giữ (long press)
//        menuItemBox.setOnMousePressed(event -> handleMenuItemMousePressed(event, itemData, menuItemBox));
//        menuItemBox.setOnMouseReleased(this::handleMenuItemMouseReleased);
//
//        return menuItemBox;
//    }

    @FXML
    private void handleMenuItemClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            Node sourceNode = (Node) event.getSource();
            // Đảm bảo rằng timeline long press đã dừng nếu nó đang chạy (tránh trường hợp click nhanh sau khi bắt đầu long press)
            if (longPressTimeline != null) {
                longPressTimeline.stop();
            }

            // Kiểm tra xem userData có phải là RMenuItem không
            if (sourceNode.getUserData() instanceof RMenuItem) {
                RMenuItem selectedItem = (RMenuItem) sourceNode.getUserData();
                addItemToOrder(selectedItem, 1); // Thêm 1 sản phẩm khi click
            } else if (sourceNode.getParent().getUserData() instanceof RMenuItem){ // Đôi khi sự kiện click là của Label/ImageView bên trong VBox
                 RMenuItem selectedItem = (RMenuItem) sourceNode.getParent().getUserData();
                 addItemToOrder(selectedItem, 1);
            }
        }
    }


    @FXML
    private void handleMenuItemMousePressed(MouseEvent event, RMenuItem itemData, Node node) {
        if (event.getButton() == MouseButton.PRIMARY) {
            currentLongPressItem = itemData;
            longPressNode = node; // Lưu lại node để hiển thị PopOver gần đó
            if (longPressTimeline == null) {
                longPressTimeline = new Timeline(new KeyFrame(Duration.millis(LONG_PRESS_DURATION), ae -> showQuantityPopup(currentLongPressItem, longPressNode)));
            }
            longPressTimeline.setCycleCount(1); // Chạy 1 lần
            longPressTimeline.playFromStart();
        }
    }

    @FXML
    private void handleMenuItemMouseReleased(MouseEvent event) {
        if (longPressTimeline != null) {
            longPressTimeline.stop();
        }
    }

    private void showQuantityPopup(RMenuItem itemData, Node ownerNode) {
        // Sử dụng TextInputDialog của JavaFX hoặc PopOver của ControlsFX
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Nhập số lượng");
//        dialog.setHeaderText("Nhập số lượng cho món: " + itemData.name);
        dialog.setContentText("Số lượng:");

        // Để PopOver hiển thị gần item (nếu dùng ControlsFX)
        // PopOver popOver = new PopOver();
        // VBox content = new VBox();
        // TextField quantityField = new TextField("1");
        // Button okButton = new Button("OK");
        // okButton.setOnAction(e -> {
        //     try {
        //         int quantity = Integer.parseInt(quantityField.getText());
        //         if (quantity > 0) {
        //             addItemToOrder(itemData, quantity);
        //         }
        //         popOver.hide();
        //     } catch (NumberFormatException ex) {
        //         // Xử lý lỗi nhập không phải số
        //     }
        // });
        // content.getChildren().addAll(new Label("Số lượng cho " + itemData.name + ":"), quantityField, okButton);
        // popOver.setContentNode(content);
        // popOver.show(ownerNode);


        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantityStr -> {
            try {
                int quantity = Integer.parseInt(quantityStr);
                if (quantity > 0) {
                    // Nếu món đã có, cập nhật số lượng, nếu chưa thì thêm mới với số lượng này
                     OrderItem existingItem = currentOrder.get(itemData);
                    if (existingItem != null) {
                        existingItem.quantity = quantity; // Cập nhật trực tiếp số lượng
                    } else {
                         addItemToOrder(itemData, quantity); // Thêm mới với số lượng nhập
                    }
                    renderOrderItems();
                    updateCartSummary();
                }
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu người dùng nhập không phải là số
                Alert alert = new Alert(Alert.AlertType.ERROR, "Vui lòng nhập một số hợp lệ.");
                alert.showAndWait();
            }
        });
    }


    private void addItemToOrder(RMenuItem item, int quantityToAdd) {
        if (currentOrder.containsKey(item)) {
            OrderItem orderItem = currentOrder.get(item);
            orderItem.quantity += quantityToAdd; // Tăng số lượng nếu đã có
        } else {
            // Lần đầu thêm món này
            String currentTime = timeFormat.format(new Date());
            currentOrder.put(item, new OrderItem(item, quantityToAdd, currentTime));
        }
        renderOrderItems();
        updateCartSummary();
    }

    private void renderOrderItems() {
        orderItemsVBox.getChildren().clear();
        for (OrderItem orderItem : currentOrder.values()) {
            if (orderItem.quantity <= 0) continue; // Không hiển thị nếu số lượng là 0 hoặc âm

            HBox itemBox = new HBox(10);
            itemBox.setAlignment(Pos.CENTER_LEFT);
            itemBox.setPadding(new Insets(5));

            Label quantityLabel = new Label(String.valueOf(orderItem.quantity));
            quantityLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 20px;");

//            Label nameLabel = new Label(orderItem.menuItem.name);
//            HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS); // Để tên món ăn chiếm phần lớn không gian

//            Label priceLabel = new Label(currencyFormat.format(orderItem.menuItem.price * orderItem.quantity));

            Label timeLabel = new Label(orderItem.addedTime);
            timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

            // Nút giảm số lượng
            Button btnDecrease = new Button("-");
            btnDecrease.setOnAction(e -> {
                orderItem.quantity--;
                if (orderItem.quantity <= 0) {
                    currentOrder.remove(orderItem.menuItem);
                }
                renderOrderItems();
                updateCartSummary();
            });

            // Nút tăng số lượng
            Button btnIncrease = new Button("+");
            btnIncrease.setOnAction(e -> {
                orderItem.quantity++;
                renderOrderItems();
                updateCartSummary();
            });

//            itemBox.getChildren().addAll(quantityLabel, nameLabel, btnDecrease, btnIncrease, priceLabel, timeLabel);
            orderItemsVBox.getChildren().add(itemBox);
        }
    }

    private void updateCartSummary() {
        int totalItems = 0;
        double totalPrice = 0;
        for (OrderItem item : currentOrder.values()) {
            totalItems += item.quantity;
//            totalPrice += item.menuItem.price * item.quantity;
        }
        lblCartItemCount.setText("(" + totalItems + ")");
        lblCartTotal.setText(currencyFormat.format(totalPrice));
        btnThanhToan.setText("Thanh toán " + currencyFormat.format(totalPrice));
    }

    @FXML
    private void clearCart() {
        currentOrder.clear();
        renderOrderItems();
        updateCartSummary();
    }

    @FXML
    private void addDiscount() {
        // Xử lý logic thêm chiết khấu
        System.out.println("Thêm chiết khấu");
    }

    @FXML
    private void addServiceFee() {
        // Xử lý logic thêm phí dịch vụ
        System.out.println("Thêm phí dịch vụ");
    }

    @FXML
    private void handleThanhToan() {
        // Xử lý logic thanh toán
        System.out.println("Tiến hành thanh toán với tổng tiền: " + lblCartTotal.getText());
        System.out.println("Ghi chú: " + txtGhiChu.getText());
        // Có thể hiển thị dialog xác nhận hoặc xử lý nghiệp vụ thanh toán ở đây
    }
}
