package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.AccountDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginController {
    public Text error;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private ToggleButton showPasswordBtn;
    private final AccountDAO dao = new AccountDAO();
    @FXML
    public void initialize()
    {
        passwordField.textProperty().bindBidirectional(passwordTextField.textProperty());
    }
    @FXML
    public void showPassword()
    {
        if(showPasswordBtn.isSelected())
        {
            passwordTextField.toFront();
        }else
            passwordField.toFront();
    }
    @FXML
    protected void onForgotPasswordClicked() {
        // Tạo alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quên mật khẩu");
        alert.setHeaderText(null); // Bỏ header text để giao diện gọn hơn
        alert.setContentText("Vui lòng liên hệ với người quản lý hệ thống để được cấp lại mật khẩu.");

        // Tùy chỉnh DialogPane
        DialogPane dialogPane = alert.getDialogPane();
        alert.initStyle(StageStyle.UTILITY);
        // Tùy chỉnh nút OK
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Đã hiểu");

        alert.showAndWait();
    }
    @FXML
    private void onLogin()
    {
        String username = userName.getText();
        String password = passwordField.getText();
        if(chekLogin(username,password))
        {
            Scene scene = userName.getScene();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/container.fxml"));
            try {
                scene.setRoot(loader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private boolean chekLogin(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            error.setVisible(true);
            error.setText("Tên tài khoản và mật khẩu không được để trống");
            return false;
        }
        else if (!dao.checkAccount(username,password)) {
            error.setVisible(true);
            error.setText("Tên tài khoản hoặc mật khẩu không chính xác");
            return false;
        }else
        {
            return true;
        }
    }
}
