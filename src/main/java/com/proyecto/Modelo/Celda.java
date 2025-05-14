package com.proyecto.Modelo;

/**
 * Representa una celda en la mazmorra del juego, que puede ser suelo, pared o
 * contener un personaje.
 * 
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 * @version 1.0
 */
public class Celda {
    private tipoCelda tipo;
    private Personaje ocupante; // Null si no hay personaje

    /**
     * Constructor de la celda.
     * 
     * @param tipo Tipo de celda (pared, suelo, etc.).
     */
    public Celda(tipoCelda tipo) {
        this.tipo = tipo;
        this.ocupante = null;
    }

    /**
     * Obtiene el tipo de celda.
     * 
     * @return Tipo de celda.
     */
    public tipoCelda getTipo() {
        return this.tipo;
    }

    /**
     * Establece el tipo de celda.
     * 
     * @param tipo Nuevo tipo de celda.
     */
    public void setTipo(tipoCelda tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el personaje que ocupa la celda.
     * 
     * @return Personaje en la celda o null si está vacía.
     */
    public Personaje getOcupante() {
        return this.ocupante;
    }

    /**
     * Establece el personaje que ocupa la celda.
     * 
     * @param ocupante Personaje a colocar en la celda.
     */
    public void setOcupante(Personaje ocupante) {
        this.ocupante = ocupante;
    }

    /**
     * Verifica si la celda es una pared.
     * 
     * @return true si es pared, false en caso contrario.
     */
    public boolean esPared() {
        return tipo == tipoCelda.pared;
    }

    /**
     * Verifica si la celda es suelo.
     * 
     * @return true si es suelo, false en caso contrario.
     */
    public boolean esSuelo() {
        return tipo == tipoCelda.suelo;
    }

    /**
     * Verifica si la celda es una trampa.
     * 
     * @return true si es suelo, false en caso contrario.
     */
    public boolean esTrampa() {
        return tipo == tipoCelda.trampa;
    }

    /**
     * Verifica si la celda está ocupada por un personaje.
     * 
     * @return true si hay un personaje, false si está vacía.
     */
    public boolean estaOcupada() {
        return ocupante != null;
    }
}