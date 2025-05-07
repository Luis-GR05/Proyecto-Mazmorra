package com.proyecto.Modelo;

public class enemigo extends Personaje{
    private int percepcion;

    public enemigo(int salud, int fuerza, int defensa, int velocidad, int percepcion){
        super(salud, fuerza, defensa, velocidad);
        this.percepcion = percepcion;
    }


    public int getPercepcion() {
        return this.percepcion;
    }

    public void setPercepcion(int percepcion) {
        this.percepcion = percepcion;
    }

    @Override
    public void jugar(){
        
    }
}
