package com.proyecto.Controlador;

import java.io.IOException;
import javafx.fxml.FXML;

public class JuegoController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
