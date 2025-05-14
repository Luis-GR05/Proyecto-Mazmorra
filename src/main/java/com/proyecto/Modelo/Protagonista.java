package com.proyecto.Modelo;

/**
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 */

public class Protagonista extends Personaje {
    private boolean turnoEnEspera;
    private int saludMax;

    public Protagonista(int salud, int fuerza, int defensa, int velocidad) {
        super(salud, fuerza, defensa, velocidad);
        this.turnoEnEspera = true;
        this.saludMax=salud;
    }

    public int getSaludMax(){
        return this.saludMax;
    }

    /**
     * Indica si el protagonista ha completado su turno o está esperando entrada del
     * usuario
     * 
     * @return true si el jugador aún no ha realizado su acción, false si ya
     *         completó su turno
     */
    public boolean estaTurnoEnEspera() {
        return turnoEnEspera;
    }

    /**
     * Marca el turno como completado
     */
    public void completarTurno() {
        this.turnoEnEspera = false;
    }

    /**
     * Prepara al personaje para recibir un nuevo turno
     */
    public void prepararNuevoTurno() {
        this.turnoEnEspera = true;
    }

    /**
     * Implementación del método jugar para el protagonista.
     * En el caso del protagonista, este método solo marca que está esperando
     * la entrada del usuario, ya que las acciones se manejan a través de eventos de
     * teclado.
     */
    @Override
    public void jugar() {
        // El protagonista espera la entrada del usuario a través de eventos de teclado
        // en el JuegoController, así que aquí solo marcamos que está listo para jugar
        prepararNuevoTurno();
    }
}
