package com.restaurant.restaurant_management_project;

import com.restaurant.restaurant_management_project.dao.EquipmentDAO;
import com.restaurant.restaurant_management_project.model.Equipment;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.util.List;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/equipmentView.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        stage.setTitle("Restaurant Management");
        stage.setScene(scene);
        stage.show();
    }

    public void connectionTest(){
        List<Equipment> equipmentList;
//        Equipment newE = new Equipment("DC002", "A", "Loai 1", 100, "Tot", Date.valueOf("2025-4-30"));
        EquipmentDAO equipDAO = new EquipmentDAO();
//        equipDAO.addEquipment(newE);
        equipmentList = equipDAO.GetAllEquipment();
        for(Equipment e : equipmentList){
            e.inThongTin();
        }
    }
    
    public static void main(String[] args) {
        launch();
    }

}