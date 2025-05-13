module com.proyecto {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.proyecto.Controlador to javafx.fxml;
    exports com.proyecto;
}
