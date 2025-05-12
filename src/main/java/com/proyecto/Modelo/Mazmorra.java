package com.proyecto.Modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la mazmorra del juego y mantiene el estado global
 * 
 * @author Tu Nombre
 */
public class Mazmorra implements Sujeto {
    private Celda[][] escenario;
    protected Protagonista protagonista;
    protected List<enemigo> enemigos;
    protected int ancho;
    protected int alto;
    private List<Observador> observadores;

    /**
     * Constructor de la mazmorra
     */
    public Mazmorra() {
        this.enemigos = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public Celda[][] getEscenario() {
        return escenario;
    }

    public void setEscenario(Celda[][] escenario) {
        this.escenario = escenario;
        notificarObservadores();
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
                    case 'J': // Posición inicial del Protagonista
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        if (protagonista != null) {
                            protagonista.setPosX(x);
                            protagonista.setPosY(y);
                            getEscenario()[y][x].setOcupante(protagonista);
                        }
                        break;
                    case 'E': // Posición de enemigo
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        break;
                    default:
                        getEscenario()[y][x] = new Celda(tipoCelda.suelo);
                        break;
                }
            }
        }
        notificarObservadores();
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
            boolean firstLine = true;

            System.out.println("Dimensiones del mapa: " + ancho + "x" + alto); // Debug

            while ((linea = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (linea.toLowerCase().contains("salud") || linea.trim().isEmpty()) {
                        continue;
                    }
                }

                String[] datos = linea.split(",");
                if (datos.length < 7) {
                    System.err.println("Línea CSV inválida (campos insuficientes): " + linea);
                    continue;
                }

                try {
                    int salud = Integer.parseInt(datos[0].trim());
                    int fuerza = Integer.parseInt(datos[1].trim());
                    int defensa = Integer.parseInt(datos[2].trim());
                    int velocidad = Integer.parseInt(datos[3].trim());
                    int percepcion = Integer.parseInt(datos[4].trim());
                    int x = Integer.parseInt(datos[5].trim());
                    int y = Integer.parseInt(datos[6].trim());

                    if (x < 0 || x >= ancho || y < 0 || y >= alto) {
                        System.err.println("Posición de enemigo fuera de límites: x=" + x + ", y=" + y +
                                ". Ajustando a posición válida.");
                        // Ajustar a posición válida
                        x = Math.min(Math.max(0, x), ancho - 1);
                        y = Math.min(Math.max(0, y), alto - 1);
                    }

                    enemigo nuevoEnemigo = new enemigo(salud, fuerza, defensa, velocidad, percepcion);
                    nuevoEnemigo.setPosX(x);
                    nuevoEnemigo.setPosY(y);
                    nuevoEnemigo.setMazmorra(this);

                    // Verificar si la celda ya está ocupada
                    if (getEscenario()[y][x].estaOcupada()) {
                        System.err.println("Celda [" + y + "][" + x + "] ya ocupada, buscando celda libre cercana.");
                        // Buscar una celda libre cercana
                        boolean ubicado = false;
                        for (int dy = -1; dy <= 1 && !ubicado; dy++) {
                            for (int dx = -1; dx <= 1 && !ubicado; dx++) {
                                int nx = x + dx;
                                int ny = y + dy;
                                if (nx >= 0 && nx < ancho && ny >= 0 && ny < alto &&
                                        !getEscenario()[ny][nx].esPared() && !getEscenario()[ny][nx].estaOcupada()) {
                                    nuevoEnemigo.setPosX(nx);
                                    nuevoEnemigo.setPosY(ny);
                                    getEscenario()[ny][nx].setOcupante(nuevoEnemigo);
                                    enemigos.add(nuevoEnemigo);
                                    ubicado = true;
                                }
                            }
                        }
                        if (!ubicado) {
                            System.err.println("No se pudo ubicar al enemigo en una posición cercana.");
                        }
                    } else {
                        getEscenario()[y][x].setOcupante(nuevoEnemigo);
                        enemigos.add(nuevoEnemigo);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al parsear datos del enemigo: " + linea);
                    e.printStackTrace();
                }
            }
        }
        notificarObservadores();
    }

    /**
     * Establece el Protagonista principal en la mazmorra
     * 
     * @param protagonista Instancia del Protagonista
     */
    public void setJugador(Protagonista protagonista) {
        this.protagonista = protagonista;
        // Si el Protagonista ya tiene posición, ocupar esa celda
        if (protagonista.getPosX() >= 0 && protagonista.getPosY() >= 0) {
            getEscenario()[protagonista.getPosY()][protagonista.getPosX()].setOcupante(protagonista);
        }
        notificarObservadores();
    }

    /**
     * Mueve un personaje a una nueva posición
     * 
     * @param personaje Personaje a mover
     * @param nuevaX    Nueva coordenada X
     * @param nuevaY    Nueva coordenada Y
     * @return true si el movimiento fue exitoso, false en caso contrario
     */
    public boolean moverPersonaje(Personaje personaje, int nuevaX, int nuevaY) {
        // Verificar si la nueva posición está dentro de los límites
        if (nuevaX < 0 || nuevaX >= ancho || nuevaY < 0 || nuevaY >= alto) {
            return false;
        }

        // Verificar si la celda destino es una pared
        if (escenario[nuevaY][nuevaX].esPared()) {
            return false;
        }

        // Verificar si la celda destino está ocupada
        if (escenario[nuevaY][nuevaX].estaOcupada()) {
            return false;
        }

        // Actualizar la posición
        escenario[personaje.getPosY()][personaje.getPosX()].setOcupante(null);
        escenario[nuevaY][nuevaX].setOcupante(personaje);
        personaje.setPosX(nuevaX);
        personaje.setPosY(nuevaY);

        notificarObservadores();
        return true;
    }

    /**
     * Procesa un ataque entre personajes
     * 
     * @param atacante Personaje atacante
     * @param defensor Personaje defensor
     */
    public void procesarAtaque(Personaje atacante, Personaje defensor) {
        Personaje.atacar(atacante, defensor);

        // Si el defensor muere, eliminarlo de la celda
        if (defensor.getSalud() <= 0) {
            escenario[defensor.getPosY()][defensor.getPosX()].setOcupante(null);

            // Si es un enemigo, eliminarlo de la lista
            if (defensor instanceof enemigo) {
                enemigos.remove(defensor);
            }
        }

        notificarObservadores();
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public Protagonista getJugador() {
        return protagonista;
    }

    public List<enemigo> getEnemigos() {
        return enemigos;
    }

    public boolean juegoTerminado() {
        return protagonista.getSalud() <= 0 || enemigos.isEmpty();
    }

    public boolean jugadorGano() {
        return protagonista.getSalud() > 0 && enemigos.isEmpty();
    }

    // Implementación de Sujeto
    @Override
    public void agregarObservador(Observador o) {
        observadores.add(o);
    }

    @Override
    public void eliminarObservador(Observador o) {
        observadores.remove(o);
    }

    @Override
    public void notificarObservadores() {
        for (Observador o : observadores) {
            o.actualizar();
        }
    }
}