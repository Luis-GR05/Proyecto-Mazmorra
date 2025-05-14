package com.proyecto.Modelo;

/**
 * Clase abstracta que representa a un personaje en el juego, ya sea protagonista o enemigo.
 * Contiene los atributos y comportamientos básicos comunes a todos los personajes.
 * 
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 * @version 1.0
 */
public abstract class Personaje {
    protected int salud;
    protected int fuerza;
    protected int defensa;
    protected int velocidad;
    protected int posX;
    protected int posY;

    /**
     * Constructor principal para crear un personaje.
     * 
     * @param salud     Puntos de salud iniciales del personaje (debe ser positivo).
     * @param fuerza    Puntos de fuerza para calcular daño (debe ser positivo).
     * @param defensa   Puntos de defensa para reducir daño (debe ser positivo).
     * @param velocidad Velocidad de movimiento (debe ser positivo).
     */
    public Personaje(int salud, int fuerza, int defensa, int velocidad) {
        this.salud = salud;
        this.fuerza = fuerza;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.posX = 0;
        this.posY = 0;
    }

    /**
     * Obtiene los puntos de salud actuales del personaje.
     * 
     * @return Salud actual del personaje.
     */
    public int getSalud() {
        return this.salud;
    }

    /**
     * Establece los puntos de salud del personaje.
     * 
     * @param salud Nuevo valor de salud (debe ser positivo).
     */
    public void setSalud(int salud) {
        this.salud = salud;
    }

    /**
     * Obtiene los puntos de fuerza del personaje.
     * 
     * @return Fuerza actual del personaje.
     */
    public int getFuerza() {
        return this.fuerza;
    }

    /**
     * Establece los puntos de fuerza del personaje.
     * 
     * @param fuerza Nuevo valor de fuerza (debe ser positivo).
     */
    public void setFuerza(int fuerza) {
        this.fuerza = fuerza;
    }

    /**
     * Obtiene los puntos de defensa del personaje.
     * 
     * @return Defensa actual del personaje.
     */
    public int getDefensa() {
        return this.defensa;
    }

    /**
     * Establece los puntos de defensa del personaje.
     * 
     * @param defensa Nuevo valor de defensa (debe ser positivo).
     */
    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    /**
     * Obtiene los puntos de velocidad del personaje.
     * 
     * @return Velocidad actual del personaje.
     */
    public int getVelocidad() {
        return this.velocidad;
    }

    /**
     * Establece los puntos de velocidad del personaje.
     * 
     * @param velocidad Nuevo valor de velocidad (debe ser positivo).
     */
    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    /**
     * Obtiene la posición horizontal (columna) del personaje en la mazmorra.
     * 
     * @return Coordenada X actual.
     */
    public int getPosX() {
        return this.posX;
    }

    /**
     * Establece la posición horizontal (columna) del personaje.
     * 
     * @param posX Nueva coordenada X (debe estar dentro de los límites de la mazmorra).
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Obtiene la posición vertical (fila) del personaje en la mazmorra.
     * 
     * @return Coordenada Y actual.
     */
    public int getPosY() {
        return this.posY;
    }

    /**
     * Establece la posición vertical (fila) del personaje.
     * 
     * @param posY Nueva coordenada Y (debe estar dentro de los límites de la mazmorra).
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Método abstracto que define la lógica del turno del personaje.
     * Debe ser implementado por las clases hijas (Protagonista y Enemigo).
     */
    public abstract void jugar();

    /**
     * Realiza un ataque de un personaje a otro, calculando el daño basado en fuerza y defensa.
     * 
     * @param atacante Personaje que realiza el ataque.
     * @param defensor Personaje que recibe el ataque.
     * @throws IllegalArgumentException Si alguno de los personajes es null.
     */
    public static void atacar(Personaje atacante, Personaje defensor) {
        if (atacante == null || defensor == null) {
            throw new IllegalArgumentException("Los personajes no pueden ser null");
        }
        
        int daño = Math.max(0, atacante.getFuerza() - defensor.getDefensa());
        defensor.setSalud(defensor.getSalud() - daño);
    }
}