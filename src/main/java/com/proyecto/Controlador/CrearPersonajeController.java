package com.proyecto.Controlador;

import com.proyecto.Modelo.Protagonista;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para la vista de creación del personaje protagonista
 * 
 * @author Luis Gordillo
 */
public class CrearPersonajeController {

    @FXML
    private TextField nombreField;

    @FXML
    private Slider saludSlider;

    @FXML
    private Slider fuerzaSlider;

    @FXML
    private Slider defensaSlider;

    @FXML
    private Slider velocidadSlider;

    @FXML
    private Label saludLabel;

    @FXML
    private Label fuerzaLabel;

    @FXML
    private Label defensaLabel;

    @FXML
    private Label velocidadLabel;

    @FXML
    private Label puntosRestantesLabel;

    @FXML
    private Button crearButton;

    @FXML
    private Label mensajeError;

    private int puntosTotales = 200; // Total de puntos para distribuir
    private int puntosRestantes = 100; // Puntos disponibles después de la configuración inicial

    /**
     * Inicializa el controlador
     */
    @FXML
    public void initialize() {
        // Actualizar etiquetas con valores iniciales
        actualizarEtiquetas();

        // Añadir listeners a los sliders para actualizar etiquetas y calcular puntos
        // restantes
        ChangeListener<Number> sliderChangeListener = (ObservableValue<? extends Number> observable,
                Number oldValue, Number newValue) -> {
            actualizarEtiquetas();
            calcularPuntosRestantes();
        };

        saludSlider.valueProperty().addListener(sliderChangeListener);
        fuerzaSlider.valueProperty().addListener(sliderChangeListener);
        defensaSlider.valueProperty().addListener(sliderChangeListener);
        velocidadSlider.valueProperty().addListener(sliderChangeListener);
    }

    /**
     * Actualiza las etiquetas con los valores actuales de los sliders
     */
    private void actualizarEtiquetas() {
        saludLabel.setText(String.valueOf((int) saludSlider.getValue()));
        fuerzaLabel.setText(String.valueOf((int) fuerzaSlider.getValue()));
        defensaLabel.setText(String.valueOf((int) defensaSlider.getValue()));
        velocidadLabel.setText(String.valueOf((int) velocidadSlider.getValue()));
    }

    /**
     * Calcula los puntos restantes basados en los valores de los sliders
     */
    private void calcularPuntosRestantes() {
        int puntosUsados = (int) (saludSlider.getValue() + fuerzaSlider.getValue() +
                defensaSlider.getValue() + velocidadSlider.getValue());
        puntosRestantes = puntosTotales - puntosUsados;
        puntosRestantesLabel.setText(String.valueOf(puntosRestantes));

        // Deshabilitar el botón si no hay puntos restantes o nombre está vacío
        crearButton.setDisable(puntosRestantes < 0 || nombreField.getText().trim().isEmpty());

        if (puntosRestantes < 0) {
            mensajeError.setText("¡Has excedido el número de puntos disponibles!");
        } else {
            mensajeError.setText("");
        }
    }

    /**
     * Maneja el evento de crear personaje
     * 
     * @param event Evento de acción
     */
    @FXML
    private void crearPersonaje(ActionEvent event) {
        if (nombreField.getText().trim().isEmpty()) {
            mensajeError.setText("Por favor, introduce un nombre para tu personaje.");

        } else {

            if (puntosRestantes < 0) {
                mensajeError.setText("No puedes crear el personaje con más puntos de los disponibles.");
            } else {

                String nombre = nombreField.getText().trim();
                int salud = (int) saludSlider.getValue();
                int fuerza = (int) fuerzaSlider.getValue();
                int defensa = (int) defensaSlider.getValue();
                int velocidad = (int) velocidadSlider.getValue();

                // Crear el personaje protagonista
                Protagonista protagonista = new Protagonista(salud, fuerza, defensa, velocidad);

                try {
                    // Cargar la vista del juego
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/proyecto/JuegoView.fxml"));
                    Parent root = loader.load();

                    // Obtener controlador y pasar el protagonista
                    JuegoController juegoController = loader.getController();
                    juegoController.iniciarJuego(protagonista, nombre);

                    // Cambiar a la escena del juego
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) crearButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("Juego de Mazmorras - " + nombre);
                    stage.setResizable(false);
                    stage.show();

                } catch (IOException e) {
                    mensajeError.setText("Error al cargar el juego: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}