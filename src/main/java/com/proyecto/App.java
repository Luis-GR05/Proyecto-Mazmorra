package com.proyecto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal que inicia la aplicación del juego de mazmorras
 * @author Tu Nombre
 */
public class App extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar la vista de creación de personaje
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CrearPersonajeView.fxml"));
        Parent root = loader.load();
        
        // Configurar la escena y mostrar
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Juego de Mazmorras - Crear Personaje");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    /**
     * Método principal que lanza la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}