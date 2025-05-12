package com.proyecto.Controlador;

import com.proyecto.Modelo.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.*;

/**
 * Controlador para la vista principal del juego
 * Implementa el patrón Observador para actualizar la vista cuando cambia el
 * modelo
 * 
 * @author Tu Nombre
 */
public class JuegoController implements Observador {

    @FXML
    private GridPane tableroGrid;

    @FXML
    private Label turnoActualLabel;

    @FXML
    private Label nombreJugadorLabel;

    @FXML
    private ProgressBar saludJugadorBar;

    @FXML
    private Label saludJugadorLabel;

    @FXML
    private Label fuerzaJugadorLabel;

    @FXML
    private Label defensaJugadorLabel;

    @FXML
    private Label velocidadJugadorLabel;

    @FXML
    private VBox enemigosContainer;

    @FXML
    private ListView<String> ordenTurnosListView;

    @FXML
    private Label mensajeLabel;

    private Mazmorra mazmorra;
    private String nombreJugador;
    private List<Personaje> ordenTurnos;
    private int turnoActual = 0;
    private boolean juegoTerminado = false;

    // Mapeo de personajes a sus representaciones visuales
    private Map<Personaje, StackPane> personajesVisuales = new HashMap<>();

    /**
     * Inicializa el juego con el protagonista creado
     * 
     * @param protagonista El personaje protagonista
     * @param nombre       Nombre del protagonista
     */
    public void iniciarJuego(Protagonista protagonista, String nombre) {
        this.nombreJugador = nombre;

        mazmorra = new Mazmorra();

        ordenTurnos = new ArrayList<>();

        mazmorra.agregarObservador(this);

        try {
            mazmorra.cargarEscenario("src/main/resources/com/proyecto/mapa.txt");
            mazmorra.setJugador(protagonista);
            configurarInterfaz();

            mazmorra.cargarEnemigos("src/main/resources/com/proyecto/enemigos.csv");

            for (enemigo e : mazmorra.getEnemigos()) {
                e.setMazmorra(mazmorra);
            }

            determinarOrdenTurnos();

            // Asegurarnos de que los controles de teclado se configuren cuando la escena
            // esté disponible
            if (tableroGrid.getScene() != null) {
                configurarControlesTeclado();
            } else {
                // Si la escena aún no está disponible, usamos un listener para detectar cuando
                // lo esté
                tableroGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        configurarControlesTeclado();
                    }
                });
            }

            actualizarEstadoJuego();

            comenzarTurnoActual();

        } catch (IOException e) {
            mostrarMensaje("Error al cargar el juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura la interfaz de usuario
     */
    private void configurarInterfaz() {
        // Configurar tablero
        crearTablero();

        // Mostrar información del jugador
        actualizarInfoJugador();

        // Mostrar información de enemigos
        actualizarInfoEnemigos();
    }

    /**
     * Crea el tablero visual basado en el escenario de la mazmorra
     */
    private void crearTablero() {
        // Clear existing components
        tableroGrid.getChildren().clear();
        tableroGrid.getColumnConstraints().clear();
        tableroGrid.getRowConstraints().clear();

        int ancho = mazmorra.getAncho();
        int alto = mazmorra.getAlto();
        int tamCelda = 40; // Cell size in pixels

        // Create column and row constraints
        for (int i = 0; i < ancho; i++) {
            ColumnConstraints colConstraint = new ColumnConstraints(tamCelda);
            tableroGrid.getColumnConstraints().add(colConstraint);
        }

        for (int i = 0; i < alto; i++) {
            RowConstraints rowConstraint = new RowConstraints(tamCelda);
            tableroGrid.getRowConstraints().add(rowConstraint);
        }

        // Populate the grid
        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                StackPane celdaVisual = new StackPane();
                Celda celda = mazmorra.getEscenario()[y][x];

                Rectangle fondo = new Rectangle(tamCelda, tamCelda);

                // Load the images using the class loader
                Image imagenPared = new Image(getClass().getResourceAsStream("/com/proyecto/Imagenes/Pared.png"));
                ImagePattern patronImagenPared = new ImagePattern(imagenPared);

                Image imagenSuelo = new Image(getClass().getResourceAsStream("/com/proyecto/Imagenes/Suelo.png"));
                ImagePattern patronImagenSuelo = new ImagePattern(imagenSuelo);

                // Set the appropriate image pattern based on cell type
                if (celda.esPared()) {
                    fondo.setFill(patronImagenPared);
                } else {
                    fondo.setFill(patronImagenSuelo);
                }

                celdaVisual.getChildren().add(fondo);

                // If the cell is occupied by a character, add a colored rectangle
                if (celda.estaOcupada()) {
                    Personaje ocupante = celda.getOcupante();
                    Rectangle personajeRect = new Rectangle(tamCelda * 0.8, tamCelda * 0.8);

                    // Color based on character type (player or enemy)
                    if (ocupante instanceof Protagonista) {
                        personajeRect.setFill(Color.BLUE);
                    } else {
                        personajeRect.setFill(Color.RED);
                    }

                    celdaVisual.getChildren().add(personajeRect);
                    personajesVisuales.put(ocupante, celdaVisual);
                }

                tableroGrid.add(celdaVisual, x, y);
            }
        }
    }