package com.proyecto.Modelo;

/**
 * Interfaz para implementar el patrón Observador.
 * Las clases que implementen esta interfaz recibirán notificaciones de cambios
 * en el modelo.
 * 
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 * @version 1.0
 */
public interface Observador {
    /**
     * Método llamado cuando el sujeto observado notifica un cambio.
     */
    void actualizar();
}