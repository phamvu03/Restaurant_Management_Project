module com.restaurant.restaurant_management_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires annotations;
    requires java.base;

    exports com.restaurant.restaurant_management_project;
    exports com.restaurant.restaurant_management_project.controller to javafx.fxml;
    opens com.restaurant.restaurant_management_project.controller to  javafx.fxml;
    exports com.restaurant.restaurant_management_project.model to javafx.fxml;
    opens com.restaurant.restaurant_management_project.model to javafx.fxml;
}
