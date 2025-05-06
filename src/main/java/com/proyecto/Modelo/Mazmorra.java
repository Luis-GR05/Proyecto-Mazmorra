package com.proyecto.Modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mazmorra {
    private Celda[][] escenario;
    protected jugador jugador;
    protected List<enemigo> enemigos;
    protected int ancho;
    protected int alto;

    public Mazmorra() {
        this.enemigos = new ArrayList<>();
    }

    public Celda[][] getEscenario() {
        return escenario;

    }

    public void setEscenario(Celda[][] escenario) {
        this.escenario = escenario;

    }

    /**
     * Carga el escenario desde un archivo de texto
     * 
     * @param rutaArchivo Ruta del archivo que contiene el mapa
     * @throws IOException Si hay error al leer el archivo
     */
    public void cargarEscenario(String rutaArchivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        }

        this.alto = lineas.size();
        this.ancho = lineas.get(0).length();
        this.setEscenario(new Celda[alto][ancho]);

        for (int y = 0; y < alto; y++) {
            String fila = lineas.get(y);
            for (int x = 0; x < ancho; x++) {
                char c = fila.charAt(x);
                switch (c) {
                    case 'P': // Pared
                        getEscenario()[y][x] = new Celda(tipoCelda.pared);
                        break;
                    case 'S': // Suelo
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        break;
                    case 'J': // Posición inicial del jugador
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        if (jugador != null) {
                            jugador.setPosX(x);
                            jugador.setPosY(y);
                            getEscenario()[y][x].setOcupante(jugador);
                        }
                        break;
                    case 'E': // Posición de enemigo
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        // Aquí se debería crear el enemigo según datos del archivo
                        break;
                    default:
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        break;
                }
            }
        }
    }

    /**
     * Carga los enemigos desde un archivo CSV
     * 
     * @param rutaArchivo Ruta del archivo CSV con datos de enemigos
     * @throws IOException Si hay error al leer el archivo
     */
    public void cargarEnemigos(String rutaArchivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                int salud = Integer.parseInt(datos[0]);
                int fuerza = Integer.parseInt(datos[1]);
                int defensa = Integer.parseInt(datos[2]);
                int velocidad = Integer.parseInt(datos[3]);
                int percepcion = Integer.parseInt(datos[4]);
                int x = Integer.parseInt(datos[5]);
                int y = Integer.parseInt(datos[6]);

                enemigo nuevoEnemigo = new enemigo(salud, fuerza, defensa, velocidad, percepcion);
                nuevoEnemigo.setPosX(x);
                nuevoEnemigo.setPosY(y);
                getEscenario()[y][x].setOcupante(nuevoEnemigo);
                enemigos.add(nuevoEnemigo);
            }
        }
    }

    /**
     * Establece el jugador principal en la mazmorra
     * 
     * @param jugador Instancia del jugador
     */
    public void setJugador(jugador jugador) {
        this.jugador = jugador;
        // Si el jugador ya tiene posición, ocupar esa celda
        if (jugador.getPosX() >= 0 && jugador.getPosY() >= 0) {
            getEscenario()[jugador.getPosY()][jugador.getPosX()].setOcupante(jugador);
        }
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public jugador getJugador() {
        return jugador;
    }

    public List<enemigo> getEnemigos() {
        return enemigos;
    }

}