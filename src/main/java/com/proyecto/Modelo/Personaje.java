package com.proyecto.Modelo;

/**
 * @author Luis Gordillo
 * @author Roberto Borrallo
 * @author José María Gutierrez
 */

public abstract class Personaje {
    protected int salud, fuerza, defensa, velocidad, posX, posY;

    public Personaje(int salud, int fuerza, int defensa, int velocidad) {
        this.salud = salud;
        this.fuerza = fuerza;
        this.defensa = defensa;
        this.velocidad = velocidad;
        this.posX = 0;
        this.posY = 0;
    }

    public int getSalud() {
        return this.salud;
    }

    public void setSalud(int salud) {
        this.salud = salud;
    }

    public int getFuerza() {
        return this.fuerza;
    }

    public void setFuerza(int fuerza) {
        this.fuerza = fuerza;
    }

    public int getDefensa() {
        return this.defensa;
    }

    public void setDefensa(int defensa) {
        this.defensa = defensa;
    }

    public int getVelocidad() {
        return this.velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getPosX() {
        return this.posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public abstract void jugar();

    public static void atacar(Personaje atacante, Personaje defensor) {
        int daño = Math.max(0, atacante.getFuerza() - defensor.getDefensa());
        defensor.setSalud(defensor.getSalud() - daño);
    }
}
