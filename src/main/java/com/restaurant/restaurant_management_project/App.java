package com.restaurant.restaurant_management_project;

import com.restaurant.restaurant_management_project.dao.EquipmentDAO;
import com.restaurant.restaurant_management_project.database.ConnectionPool;
import com.restaurant.restaurant_management_project.model.Equipment;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/login-view.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

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

    public void connectionTest(){
        List<Equipment> equipmentList;
//        Equipment newE = new Equipment("DC002", "A", "Loai 1", 100, "Tot", Date.valueOf("2025-4-30"));
        EquipmentDAO equipDAO = new EquipmentDAO();
//        equipDAO.addEquipment(newE);
        equipmentList = equipDAO.getAllEquipment();
        for(Equipment e : equipmentList){
            e.inThongTin();
        }
    }
    
    public static void main(String[] args) {
        launch();
    }
}