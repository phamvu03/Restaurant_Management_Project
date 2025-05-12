module com.restaurant.restaurant_management_project {
    requires javafx.fxml;
    requires java.sql;
    requires annotations;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome5;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires javafx.swing;

    exports com.restaurant.restaurant_management_project;
    exports com.restaurant.restaurant_management_project.controller to javafx.fxml;
    opens com.restaurant.restaurant_management_project.controller to  javafx.fxml;
    opens com.restaurant.restaurant_management_project.model to javafx.base;
}
