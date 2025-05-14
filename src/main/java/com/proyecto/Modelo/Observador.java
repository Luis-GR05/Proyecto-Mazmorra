package com.proyecto.Modelo;

/**
 * Interfaz para implementar el patrón Observador
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 */
public interface Observador {
    /**
     * Método que se llama cuando el modelo observado cambia
     */
    void actualizar();
}