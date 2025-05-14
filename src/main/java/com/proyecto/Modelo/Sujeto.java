package com.proyecto.Modelo;

/**
 * Interfaz para implementar el patrón Observador (Sujeto)
 * 
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 */
public interface Sujeto {
    /**
     * Añade un observador
     * 
     * @param o Observador a añadir
     */
    void agregarObservador(Observador o);

    /**
     * Elimina un observador
     * 
     * @param o Observador a eliminar
     */
    void eliminarObservador(Observador o);

    /**
     * Notifica a todos los observadores de un cambio
     */
    void notificarObservadores();
}