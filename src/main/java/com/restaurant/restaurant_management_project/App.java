package com.restaurant.restaurant_management_project;

import com.restaurant.restaurant_management_project.database.ConnectionPool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/login-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);

        stage.setTitle("Restaurant Management");
        stage.setScene(scene);
        
        stage.setOnCloseRequest(e -> {
            try {
                System.out.println("Closing all connection!! XD");
                ConnectionPool.getInstance().shutdown();
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}