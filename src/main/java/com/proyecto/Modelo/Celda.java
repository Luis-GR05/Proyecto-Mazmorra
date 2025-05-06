package com.proyecto.Modelo;

public class Celda {
    private tipoCelda tipo;
    private Personaje ocupante; //Null si no hay personaje

    public Celda(tipoCelda tipo){
        this.tipo=tipo;
        this.ocupante=null;
    }


    public tipoCelda getTipo() {
        return this.tipo;
    }

    public void setTipo(tipoCelda tipo) {
        this.tipo = tipo;
    }

    public Personaje getOcupante() {
        return this.ocupante;
    }

    public void setOcupante(Personaje ocupante) {
        this.ocupante = ocupante;
    }

    public boolean esPared(){
        return tipo == tipoCelda.pared;
    }

    public boolean esSuelo(){
        return tipo == tipoCelda.suelo;
    }

    public boolean estaOcupada(){
        return ocupante != null;
    }

}

