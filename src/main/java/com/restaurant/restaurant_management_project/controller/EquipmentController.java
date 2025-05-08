/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.restaurant.restaurant_management_project.controller;

import com.restaurant.restaurant_management_project.dao.EquipmentDAO;
import com.restaurant.restaurant_management_project.model.Equipment;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class EquipmentController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private TableView<Equipment> equipmentTableId;
    
     @FXML
    private TableColumn<Equipment, String> colCondition;

    @FXML
    private TableColumn<Equipment, String> colId;

    @FXML
    private TableColumn<Equipment, String> colName;

    @FXML
    private TableColumn<Equipment, Integer> colQuantity;

    @FXML
    private TableColumn<Equipment, Date> colReportDate;

    @FXML
    private TableColumn<Equipment, String>  colType;
    
    @FXML
    private Button addBtn;

    private final EquipmentDAO equipDAO = new EquipmentDAO();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDataFromDatabase();
    }    
    
    private void loadDataFromDatabase(){
        List<Equipment> list = equipDAO.GetAllEquipment();
        ObservableList<Equipment> observableList  = FXCollections.observableArrayList(list);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("MaDungCu"));
        colName.setCellValueFactory(new PropertyValueFactory<>("TenDungCu"));
        colType.setCellValueFactory(new PropertyValueFactory<>("Loai"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("SoLuong"));
        colCondition.setCellValueFactory(new PropertyValueFactory<>("TinhTrang"));
        colReportDate.setCellValueFactory(new PropertyValueFactory<>("NgayThongKe"));
        
        equipmentTableId.setItems(observableList);
    }
    
}
