package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.MenuItemDao;
import com.restaurant.restaurant_management_project.dao.MenuItemDaoImpl;
import com.restaurant.restaurant_management_project.dao.ReportDAO;
import com.restaurant.restaurant_management_project.model.RMenuItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class ItemListCell extends ListCell<RMenuItem> {

    @FXML
    private Label num;
    @FXML
    private ImageView img;
    @FXML
    private Label name;
    @FXML
    private Label price;
    @FXML
    private Label sales;
    @FXML
    private HBox container;
    @FXML
    private Label category;
    @FXML
    private Label salesPercentage;
    @FXML
    private ImageView trendIndicator;

    private final static MenuItemDaoImpl dao = new MenuItemDaoImpl();

    private boolean fxmlLoaded = false;
    private static final Image PLACEHOLDER_IMAGE = new Image(ItemGridCell.class.getResource("/image/em.png").toExternalForm());

    @Override
    protected void updateItem(RMenuItem rMenuItem, boolean b) {
        super.updateItem(rMenuItem, b);

        if (b || rMenuItem == null) {
            setGraphic(null);
            return;
        }

        if (!fxmlLoaded) {
            loadFXML();
        }

        // Update UI with menu item data
        setText(null);
        setGraphic(container);
        num.setText("#" + (getIndex() + 1));
        name.setText(rMenuItem.getItemName());
        price.setText("$" + rMenuItem.getItemPrice());
        category.setText(rMenuItem.getItemCategory() != null ? rMenuItem.getItemCategory() : "Main Course");

        //image
        if (rMenuItem.getItemImage() != null) {
            Image image = new Image(new ByteArrayInputStream(rMenuItem.getItemImage()));
            img.setImage(image);
        } else {
            img.setImage(PLACEHOLDER_IMAGE);
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        int salesCount = dao.getFoodSalesQuantity(rMenuItem.getId(), Date.valueOf(startOfWeek), Date.valueOf(today));
        sales.setText(String.valueOf(salesCount));

    }

    private void loadFXML() {
        if (fxmlLoaded) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            URL fxmlUrl = getClass().getResource("/fxml/itemview2.fxml");

            if (fxmlUrl == null) {
                System.err.println("CRITICAL ERROR: Cannot find FXML file: /fxml/itemview2.fxml");
                return;
            }
            loader.setLocation(fxmlUrl);
            loader.setController(this);
            loader.load();

            // Apply the CSS
            container.getStylesheets().add(getClass().getResource("/css/itemlistcell.css").toExternalForm());
            fxmlLoaded = true;
        } catch (IOException e) {
            System.err.println("CRITICAL ERROR: Failed to load FXML for ItemView: " + e.getMessage());
            e.printStackTrace();
        }
    }
}