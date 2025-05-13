package com.proyecto.Modelo;

import java.util.Random;

public class enemigo extends Personaje {
    private int percepcion;
    private Mazmorra mazmorra;
    private int saludMax;

    public enemigo(int salud, int fuerza, int defensa, int velocidad, int percepcion) {
        super(salud, fuerza, defensa, velocidad);
        this.percepcion = percepcion;
        this.saludMax = salud;
    }

    /**
     * Establece la referencia a la mazmorra para poder acceder al estado del juego
     * 
     * @param mazmorra La mazmorra en la que se encuentra el enemigo
     */
    public void setMazmorra(Mazmorra mazmorra) {
        this.mazmorra = mazmorra;
    }

    public int getPercepcion() {
        return this.percepcion;
    }

    public void setPercepcion(int percepcion) {
        this.percepcion = percepcion;
    }

    // Añadir método para obtener salud máxima
    public int getSaludMax() {
        return this.saludMax;
    }

    /**
     * Implementación del método jugar para el enemigo.
     * El enemigo decide automáticamente su movimiento basado en la posición del
     * jugador y su atributo de percepción.
     */
    @Override
    public void jugar() {
        if (mazmorra == null || getSalud() <= 0) {
            System.out.println("Mazmorra no definida o el enemigo ya ha muerto");

        } else {

            Protagonista jugador = mazmorra.getJugador();
            if ((jugador.getSalud() <= 0) || jugador == null) {
                System.out.println("No hacer nada si el jugador ya está muerto");
            } else {
                // Calcular distancia al jugador
                int distX = Math.abs(getPosX() - jugador.getPosX());
                int distY = Math.abs(getPosY() - jugador.getPosY());
                int distancia = distX + distY; // Distancia Manhattan

                int nuevaX = getPosX();
                int nuevaY = getPosY();

                if (distancia == 1) {
                    mazmorra.procesarAtaque(this, jugador);
                }

                // Determinar movimiento basado en la percepción
                if (distancia <= percepcion) {
                    // Moverse hacia el jugador
                    if (distX > distY) {
                        // Moverse en X
                        nuevaX += (getPosX() < jugador.getPosX()) ? 1 : -1;
                    } else {
                        // Moverse en Y
                        nuevaY += (getPosY() < jugador.getPosY()) ? 1 : -1;
                    }
                } else {
                    // Moverse aleatoriamente
                    Random rand = new Random();
                    int direccion = rand.nextInt(4);

                    switch (direccion) {
                        case 0:
                            nuevaY--;
                            break; // Arriba
                        case 1:
                            nuevaY++;
                            break; // Abajo
                        case 2:
                            nuevaX--;
                            break; // Izquierda
                        case 3:
                            nuevaX++;
                            break; // Derecha
                    }
                }

                // Verificar si la nueva posición está dentro de los límites
                if (nuevaX < 0 || nuevaX >= mazmorra.getAncho() ||
                        nuevaY < 0 || nuevaY >= mazmorra.getAlto()) {
                    return; // No hacer nada si se sale de los límites
                }

                Celda nuevaCelda = mazmorra.getEscenario()[nuevaY][nuevaX];

                // Verificar si es una pared
                if (nuevaCelda.esPared()) {
                    System.out.println("El enemigo no puede moverse a una pared");
                }

                // Verificar si está ocupada
                else if (nuevaCelda.estaOcupada()) {
                    Personaje ocupante = nuevaCelda.getOcupante();

                    // Si es el jugador, atacar
                    if (ocupante instanceof Protagonista) {
                        mazmorra.procesarAtaque(this, ocupante);
                    }
                } else {
                    // Mover al enemigo
                    mazmorra.moverPersonaje(this, nuevaX, nuevaY);
                }
            }
        }
    }
}