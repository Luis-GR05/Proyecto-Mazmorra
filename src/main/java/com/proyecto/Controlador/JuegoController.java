package com.proyecto.Controlador;

import com.proyecto.Modelo.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.*;

/**
 * Controlador para la vista principal del juego
 * Implementa el patrón Observador para actualizar la vista cuando cambia el
 * modelo
 * 
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
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
        int tamCelda = 60; // Cell size in pixels

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

                // Crear Imagenes
                Image imagenPared = new Image(getClass().getResourceAsStream("/com/proyecto/Imagenes/Pared.png"));
                ImagePattern patronImagenPared = new ImagePattern(imagenPared);

                Image imagenSuelo = new Image(getClass().getResourceAsStream("/com/proyecto/Imagenes/Suelo.png"));
                ImagePattern patronImagenSuelo = new ImagePattern(imagenSuelo);

                // Poner imagenes dependiendo del tipo
                if (celda.esPared()) {
                    fondo.setFill(patronImagenPared);
                } else {
                    fondo.setFill(patronImagenSuelo);
                }

                celdaVisual.getChildren().add(fondo);

                // Si la celda está ocupada por un personaje, ponemos una imagen
                if (celda.estaOcupada()) {
                    Personaje ocupante = celda.getOcupante();
                    Rectangle personajeRect = new Rectangle(tamCelda * 0.8, tamCelda * 0.8);

                    Image prota = new Image(getClass().getResourceAsStream("/com/proyecto/Imagenes/Charizard.png"));
                    ImagePattern patronImagenProta = new ImagePattern(prota);

                    if (ocupante instanceof Protagonista) {
                        personajeRect.setFill(patronImagenProta);
                    } else if (ocupante instanceof enemigo) {
                        // Asignar imágenes según el tipo de enemigo
                        enemigo enemigo = (enemigo) ocupante;

                        switch (enemigo.getTipo()) {
                            case AGUA:
                                Image eneAgua = new Image(
                                        getClass().getResourceAsStream("/com/proyecto/Imagenes/CastformAgua.png"));
                                personajeRect.setFill(new ImagePattern(eneAgua));
                                break;
                            case SOL:
                                Image eneSol = new Image(
                                        getClass().getResourceAsStream("/com/proyecto/Imagenes/CastformSol.png"));
                                personajeRect.setFill(new ImagePattern(eneSol));
                                break;
                            case NIEVE:
                                Image eneNieve = new Image(
                                        getClass().getResourceAsStream("/com/proyecto/Imagenes/CastformNieve.png"));
                                personajeRect.setFill(new ImagePattern(eneNieve));
                                break;
                        }
                    }
                    celdaVisual.getChildren().add(personajeRect);
                    personajesVisuales.put(ocupante, celdaVisual);
                }

                tableroGrid.add(celdaVisual, x, y);
            }
        }
    }

    /**
     * Actualiza la información del jugador en la interfaz
     */
    private void actualizarInfoJugador() {
        Protagonista jugador = mazmorra.getJugador();

        nombreJugadorLabel.setText("Nombre: " + nombreJugador);

        int saludMax = jugador.getSaludMax();
        int saludActual = Math.max(0, jugador.getSalud());
        ProgressBar saludJugadorBar = new ProgressBar();
        saludJugadorBar.setPrefWidth(200);


        saludJugadorBar.setProgress((double) saludActual / saludMax);
        // Cambiar color de la barra según la salud
        if (saludActual < saludMax * 0.3) {
            saludJugadorBar.setStyle("-fx-accent: red;");
        } else if (saludActual < saludMax * 0.6) {
            saludJugadorBar.setStyle("-fx-accent: orange;");
        } else {
            saludJugadorBar.setStyle("-fx-accent: green;");
        }

        saludJugadorBar.setProgress((double) saludActual / saludMax);
        saludJugadorLabel.setText("Salud: " + saludActual + "/" + saludMax);
        fuerzaJugadorLabel.setText("Fuerza: " + jugador.getFuerza());
        defensaJugadorLabel.setText("Defensa: " + jugador.getDefensa());
        velocidadJugadorLabel.setText("Velocidad: " + jugador.getVelocidad());
    }

    /**
     * Actualiza la información de los enemigos en la interfaz
     */
    private void actualizarInfoEnemigos() {
        enemigosContainer.getChildren().clear();

        for (int i = 0; i < mazmorra.getEnemigos().size(); i++) {
            enemigo enemigo = mazmorra.getEnemigos().get(i);

            // Solo mostrar enemigos vivos
            if (enemigo.getSalud() > 0) {

                VBox enemigoInfo = new VBox(5);
                Label nombreLabel = new Label("Enemigo " + (i + 1));
                nombreLabel.setStyle("-fx-font-weight: bold;");

                ProgressBar saludBar = new ProgressBar();
                saludBar.setPrefWidth(200);

                int saludMax = enemigo.getSaludMax();
                int saludActual = Math.max(0, enemigo.getSalud());

                saludBar.setProgress((double) saludActual / saludMax);

                // Cambiar color de la barra según la salud
                if (saludActual < saludMax * 0.3) {
                    saludBar.setStyle("-fx-accent: red;");
                } else if (saludActual < saludMax * 0.6) {
                    saludBar.setStyle("-fx-accent: orange;");
                } else {
                    saludBar.setStyle("-fx-accent: green;");
                }

                Label saludLabel = new Label("Salud: " + saludActual + "/" + saludMax);
                Label fuerzaLabel = new Label("Fuerza: " + enemigo.getFuerza());
                Label defensaLabel = new Label("Defensa: " + enemigo.getDefensa());
                Label velocidadLabel = new Label("Velocidad: " + enemigo.getVelocidad());
                Label percepcionLabel = new Label("Percepción: " + enemigo.getPercepcion());

                enemigoInfo.getChildren().addAll(nombreLabel, saludBar, saludLabel,
                        fuerzaLabel, defensaLabel, velocidadLabel, percepcionLabel);

                // Añadir separador si no es el último enemigo
                if (i < mazmorra.getEnemigos().size() - 1) {
                    enemigoInfo.getChildren().add(new Separator());
                }

                enemigosContainer.getChildren().add(enemigoInfo);
            }
        }
    }

    /**
     * Determina el orden de turnos basado en la velocidad de los personajes
     */
    private void determinarOrdenTurnos() {
        ordenTurnos = new ArrayList<>();

        // Añadir protagonista
        ordenTurnos.add(mazmorra.getJugador());

        // Añadir enemigos
        ordenTurnos.addAll(mazmorra.getEnemigos());

        // Ordenar por velocidad (mayor a menor)
        Collections.sort(ordenTurnos, (p1, p2) -> Integer.compare(p2.getVelocidad(), p1.getVelocidad()));

        // Actualizar la vista de orden de turnos
        actualizarVistaOrdenTurnos();
    }

    /**
     * Actualiza la vista del orden de turnos
     */
    private void actualizarVistaOrdenTurnos() {

        if (ordenTurnos != null) {

            ObservableList<String> items = FXCollections.observableArrayList();

            for (int i = 0; i < ordenTurnos.size(); i++) {
                Personaje p = ordenTurnos.get(i);
                String nombre;

                if (p instanceof Protagonista) {
                    nombre = nombreJugador;
                } else {
                    int index = mazmorra.getEnemigos().indexOf(p);
                    nombre = "Enemigo " + (index + 1);
                }

                String item = nombre + " (Velocidad: " + p.getVelocidad() + ")";
                if (i == turnoActual) {
                    item += " ← Turno actual";
                }

                items.add(item);
            }

            ordenTurnosListView.setItems(items);
            ordenTurnosListView.getSelectionModel().select(turnoActual);

            if (!ordenTurnos.isEmpty()) {
                if (ordenTurnos.get(turnoActual) instanceof Protagonista) {
                    turnoActualLabel.setText(nombreJugador);
                } else {
                    int index = mazmorra.getEnemigos().indexOf(ordenTurnos.get(turnoActual));
                    turnoActualLabel.setText("Enemigo " + (index + 1));
                }
            }
        }
    }

    /**
     * Configura los controles de teclado para el movimiento del jugador
     */
    private void configurarControlesTeclado() {
        // Ahora podemos estar seguros de que la escena no es null
        tableroGrid.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!juegoTerminado) {

                if (ordenTurnos.get(turnoActual) instanceof Protagonista) {
                    Protagonista protagonista = (Protagonista) ordenTurnos.get(turnoActual);

                    if (protagonista.estaTurnoEnEspera()) {
                        int dx = 0, dy = 0;

                        switch (event.getCode()) {
                            case UP:
                            case W:
                                dy = -1;
                                break;
                            case DOWN:
                            case S:
                                dy = 1;
                                break;
                            case LEFT:
                            case A:
                                dx = -1;
                                break;
                            case RIGHT:
                            case D:
                                dx = 1;
                                break;
                            default:
                                return;
                        }

                        // Intentar mover al jugador
                        if (moverJugador(dx, dy)) {
                            // Marcar el turno como completado
                            protagonista.completarTurno();

                            // Avanzar al siguiente turno
                            siguienteTurno();
                        }
                    }
                }

                event.consume();
            }
        });
    }

    /**
     * Intenta mover al jugador en la dirección especificada
     * 
     * @param dx Desplazamiento en X
     * @param dy Desplazamiento en Y
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    private boolean moverJugador(int dx, int dy) {
        Protagonista jugador = mazmorra.getJugador();
        int nuevaX = jugador.getPosX() + dx;
        int nuevaY = jugador.getPosY() + dy;
        boolean valido = true;

        // Verificar si la nueva posición está dentro de los límites
        if (nuevaX < 0 || nuevaX >= mazmorra.getAncho() ||
                nuevaY < 0 || nuevaY >= mazmorra.getAlto()) {
            mostrarMensaje("¡No puedes salir del mapa!");
            valido = false;
        }

        Celda nuevaCelda = mazmorra.getEscenario()[nuevaY][nuevaX];

        // Verificar si es una pared
        if (nuevaCelda.esPared()) {
            mostrarMensaje("¡No puedes atravesar paredes!");
            valido = false;
        }

        // Verificar si está ocupada
        if (nuevaCelda.estaOcupada()) {
            // Atacar al ocupante
            Personaje defensor = nuevaCelda.getOcupante();
            atacar(jugador, defensor);
            valido = true;
        } else {

            // Mover al jugador
            mazmorra.moverPersonaje(jugador, nuevaX, nuevaY);
            mostrarMensaje("Te has movido.");
        }
        return valido;
    }

    /**
     * Realiza el ataque de un personaje a otro
     * 
     * @param atacante Personaje atacante
     * @param defensor Personaje defensor
     */
    private void atacar(Personaje atacante, Personaje defensor) {
        mazmorra.procesarAtaque(atacante, defensor);

        String mensaje;
        if (atacante instanceof Protagonista) {
            mensaje = "Has atacado a un enemigo!";

            if (defensor.getSalud() <= 0) {
                mensaje += " ¡Lo has derrotado!";
                if (mazmorra.getEnemigos().isEmpty()) {
                    finalizarJuego(true);
                }
            }
        } else {
            mensaje = "Un enemigo te ha atacado!";

            if (defensor.getSalud() <= 0) {
                mensaje += " ¡Has sido derrotado!";
                finalizarJuego(false);
            }
        }

        mostrarMensaje(mensaje);
    }

    /**
     * Inicia el turno del personaje actual
     */
    private void comenzarTurnoActual() {
        if (!juegoTerminado) {

            Personaje personajeActual = ordenTurnos.get(turnoActual);

            if (personajeActual instanceof Protagonista) {
                // Iniciar turno del jugador (esperar entrada del usuario)
                personajeActual.jugar();
                mostrarMensaje("Tu turno. Usa las teclas de dirección para moverte.");
            } else if (personajeActual instanceof enemigo) {
                // Retrasar la ejecución del turno del enemigo para dar tiempo al jugador a ver
                // lo que ocurre
                Platform.runLater(() -> {
                    mostrarMensaje("Turno del enemigo...");

                    // Ejecutar el turno del enemigo
                    personajeActual.jugar();

                    // Continuar con el siguiente turno
                    siguienteTurno();
                });
            }
        }
    }

    /**
     * Avanza al siguiente turno
     */
    private void siguienteTurno() {
        if (!juegoTerminado) {

            turnoActual = (turnoActual + 1) % ordenTurnos.size();
            actualizarVistaOrdenTurnos();

            // Iniciar el nuevo turno
            comenzarTurnoActual();
        }
    }

    /**
     * Actualiza el tablero visual
     */
    private void actualizarTablero() {
        // Recrear el tablero para reflejar los cambios
        crearTablero();
    }

    /**
     * Actualiza todo el estado del juego
     */
    private void actualizarEstadoJuego() {
        actualizarTablero();
        actualizarInfoJugador();
        actualizarInfoEnemigos();
        actualizarVistaOrdenTurnos();
    }

    /**
     * Finaliza el juego
     * 
     * @param victoria true si el jugador ganó, false si perdió
     */
    private void finalizarJuego(boolean victoria) {
        juegoTerminado = true;

        // Crear una alerta con estilo de juego
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Fin del Juego");

        // Crear un diseño personalizado
        VBox dialogPaneContent = new VBox(20);
        dialogPaneContent.setAlignment(Pos.CENTER);
        dialogPaneContent.setPadding(new Insets(20));

        // Estilo de fondo similar al juego
        dialogPaneContent.setStyle("-fx-background-color: #2b2b2b; -fx-border-color: #5d5d5d; -fx-border-width: 3px;");

        // Icono de victoria/derrota
        ImageView icono = new ImageView();
        String imagenPath = victoria ? "/com/proyecto/Imagenes/CharizardAtaque.png"
                : "/com/proyecto/Imagenes/CastformSol.png";
        Image imagen = new Image(getClass().getResourceAsStream(imagenPath));
        icono.setImage(imagen);
        icono.setFitWidth(80);
        icono.setFitHeight(80);

        // Título principal
        Label titulo = new Label(victoria ? "¡VICTORIA!" : "¡DERROTA!");
        titulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " +
                (victoria ? "#4CAF50;" : "#F44336;"));

        // Mensaje descriptivo
        Label mensaje = new Label(victoria ? "Has conquistado la mazmorra" : "Los enemigos te han derrotado");
        mensaje.setStyle("-fx-font-size: 18px; -fx-text-fill: #e0e0e0;");

        // Puntuación o estadísticas
        Label detalles = new Label(
                victoria ? "¡Enhorabuena, " + nombreJugador + "!" : "¡Inténtalo de nuevo, " + nombreJugador + "!");
        detalles.setStyle("-fx-font-size: 16px; -fx-text-fill: #aaaaaa;");

        // Botón personalizado
        ButtonType botonAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(botonAceptar);

        // Añadir todos los elementos
        dialogPaneContent.getChildren().addAll(icono, titulo, mensaje, detalles);
        alert.getDialogPane().setContent(dialogPaneContent);

        // Estilo del diálogo
        alert.getDialogPane().setStyle("-fx-background-color: #2b2b2b;");
        alert.getDialogPane().setMinSize(400, 300);

        // Centrar en la pantalla
        alert.initOwner(tableroGrid.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);

        // Mostrar la alerta
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje en la interfaz
     * 
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarMensaje(String mensaje) {
        mensajeLabel.setText(mensaje);
    }

    /**
     * Método de la interfaz Observador que se llama cuando el modelo cambia
     */
    @Override
    public void actualizar() {
        // Ejecutar en el hilo de la interfaz gráfica
        Platform.runLater(() -> {
            actualizarEstadoJuego();

            // Verificar si el juego ha terminado
            if (mazmorra.juegoTerminado() && !juegoTerminado) {
                finalizarJuego(mazmorra.jugadorGano());
            }
        });
    }
}