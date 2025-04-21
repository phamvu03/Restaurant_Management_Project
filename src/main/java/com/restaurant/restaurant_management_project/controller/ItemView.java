package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.model.MenuItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.GridCell;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

public class ItemView extends GridCell<MenuItem> {
    // Các biến FXML
    @FXML private ImageView itemImage;
    @FXML private Text itemName;
    @FXML private Text itemCata;
    @FXML private Text itemPrice;
    @FXML private Text itemStatus;
    @FXML private VBox itemContainer; // This is the root node defined in FXML
    // --- State for the cell ---
    private boolean fxmlLoaded = false; // Flag to track if FXML is loaded for this cell instance
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    // Placeholder image if loading fails or path is invalid
    private static final Image PLACEHOLDER_IMAGE = new Image(ItemView.class.getResource("/com/app/restaurantmanagement/image/lock.svg").toExternalForm());

    @Override
    protected void updateItem(MenuItem menuItem, boolean b) {
        super.updateItem(menuItem, b);
        if(menuItem == null||b)
        {
            setItem(null);
            setGraphic(null);
        }
        else
        {
            if(!fxmlLoaded)
            {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    URL fxmlUrl = getClass().getResource("/com/app/restaurantmanagement/fxml/item-view.fxml");

                    if (fxmlUrl == null) {
                        System.err.println("CRITICAL ERROR: Cannot find FXML file: /com/app/restaurantmanagement/fxml/item-view.fxml");
                        setText("Error: View FXML not found");
                        setGraphic(null); // Ensure graphic is null if FXML is missing
                        return; // Stop processing
                    }
                    loader.setLocation(fxmlUrl);
                    loader.setController(this);
                    loader.load();
                    setText(null);
                    itemContainer.prefWidthProperty().bind(getGridView().cellWidthProperty());
                    itemImage.setFitWidth(getGridView().getCellWidth());
                    setGraphic(itemContainer);
                } catch (IOException e) {
                    System.err.println("CRITICAL ERROR: Failed to load FXML for ItemView: " + e.getMessage());
                    e.printStackTrace();
                    setText("Error loading view"); // Show error in cell
                    setGraphic(null); // Ensure graphic is null on error
                    return; // Stop processing
                }
                if (itemName != null) { // Check added for safety
                    itemName.setText(menuItem.getName()); // Line 76 (in original code)
                } else {
                    System.err.println("WARN: itemName field is null after FXML load attempt.");
                }

                if (itemCata != null) itemCata.setText(menuItem.getCategory());
                if (itemPrice != null) itemPrice.setText(currencyFormatter.format(menuItem.getPrice()));
                if (itemStatus != null) itemStatus.setText(menuItem.isStatus() ? "Available" : "Unavailable");
                // Load the image
                if (menuItem.getImage() != null)
                    itemImage.setImage(menuItem.getImage());
                else
                    itemImage.setImage(PLACEHOLDER_IMAGE);
            }
        }
    }
}
