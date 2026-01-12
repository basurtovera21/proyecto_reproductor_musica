package modelo;

import java.util.Queue;
import java.util.LinkedList;

public class ColaReproduccionAleatoria {
    private Queue <Track> colaReproduccionAleatoria;

    public ColaReproduccionAleatoria() {
        this.colaReproduccionAleatoria = new LinkedList <>();
    }

    public void incorporar(Track track) {
        colaReproduccionAleatoria.add(track);
    }

    public Track remover() {
        return colaReproduccionAleatoria.poll();
    }

    public boolean estaVacia() {
        return colaReproduccionAleatoria.isEmpty();
    }
}
