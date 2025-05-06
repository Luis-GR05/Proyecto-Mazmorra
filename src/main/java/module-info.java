module com.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.proyecto to javafx.fxml;
    exports com.proyecto;
}
