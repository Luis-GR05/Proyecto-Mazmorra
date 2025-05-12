module com.proyecto {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.proyecto.Controlador to javafx.fxml;
    exports com.proyecto;
}
