package com.restaurant.restaurant_management_project.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.io.IOException;

public class SideBar {
    @FXML
    private Button dasBtn;
    @FXML
    private FontIcon lockIcon;
    @FXML
    private Label name;
    @FXML
    private VBox sideBar;
    @FXML
    private ImageView logo;
    private boolean isLocked = false;
    private boolean isExpanded = false;
    private double expandedWidth = 270;
    private double collapsedWidth = 70;
    private Timeline expandAnimation;
    private Timeline collapseAnimation;
    Scene scene;
    @FXML
    public void initialize() {
        Platform.runLater(this::bindSize);
        sideBar.getStyleClass().addAll("collapsed");
        setupHoverListeners();
        setupLockFunctionality();
        setupAnimations();
    }
    public void bindSize()
    {
        scene = sideBar.getScene();
        sideBar.prefHeightProperty().bind(
                scene.heightProperty()
        );
        scene.widthProperty().addListener((obs,old,newVal) ->{
            expandedWidth = newVal.doubleValue()*0.2;
            setupHoverListeners();
            setupLockFunctionality();
            setupAnimations();
        });
    }
    private void setupHoverListeners() {
        sideBar.setOnMouseEntered(event -> {
            if (!isExpanded && !isLocked) {
                expandSidebar();
            }
        });
        sideBar.setOnMouseExited(event -> {
            if (isExpanded && !isLocked) {
                collapseSidebar();
            }
        });}
    private void setupLockFunctionality() {
        lockIcon.setOnMouseClicked(event -> {
            isLocked = !isLocked;
            if (isLocked) {
                lockIcon.setIconLiteral("fas-lock");
                if (!isExpanded) {
                    expandSidebar();
                }
            } else {
                lockIcon.setIconLiteral("fas-lock-open");
            }
        });
    }

    private void setupAnimations() {
        // Create collapse animation
         collapseAnimation = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(sideBar.prefWidthProperty(), collapsedWidth),
                        new KeyValue(sideBar.minWidthProperty(), collapsedWidth)
                )
        );

        // Create expand animation
         expandAnimation = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(sideBar.prefWidthProperty(), expandedWidth),
                        new KeyValue(sideBar.minWidthProperty(), expandedWidth)
                )
        );

        // Add finished handlers to animations
        collapseAnimation.setOnFinished(e -> handleCollapsedState());
        expandAnimation.setOnFinished(e -> handleExpandedState());
    }

    private void collapseSidebar() {
        if (isExpanded && !isLocked) {
            isExpanded = false;
            collapseAnimation.play();
        }
    }

    private void expandSidebar() {
        if (!isExpanded) {
            isExpanded = true;
            expandAnimation.play();
        }
    }

    private void handleCollapsedState() {

        sideBar.getStyleClass().addAll("collapsed");
    }

    private void handleExpandedState() {
        sideBar.getStyleClass().removeAll("collapsed");
    }
    @FXML
    private void navigate(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        HBox root = (HBox) scene.getRoot();
        Object source = event.getSource();
        Button clickedButton = (Button) source;
        String buttonId = clickedButton.getId();
        switch (buttonId){
            case ("dasBtn"):
                loader.setLocation(getClass().getResource("/fxml/dashboard.fxml"));
                root.getChildren().remove(1);
                root.getChildren().add(loader.load());
                break;
            case ("menuBtn"):
                loader.setLocation(getClass().getResource("/fxml/manage-menuitem-view.fxml"));
                root.getChildren().remove(1);
                root.getChildren().add(loader.load());
                break;
        }
    }
}
