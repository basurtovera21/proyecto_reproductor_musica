package modelo;

import java.util.Stack;

public class PilaHistorialReproduccion {
    private Stack<Track> pilaHistorialReproduccion;

    public PilaHistorialReproduccion() {
        this.pilaHistorialReproduccion = new Stack<>();
    }

    public void incorporar(Track track) {
        pilaHistorialReproduccion.push(track);
    }

    public Track remover() {
        if (pilaHistorialReproduccion.isEmpty()) {
            return null;
        }

        return pilaHistorialReproduccion.pop();
    }

    public boolean estaVacia() {
        return pilaHistorialReproduccion.isEmpty();
    }
}