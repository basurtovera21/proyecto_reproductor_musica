//5. Estructura: FIFO (First In, First Out); administración de la lista de reproducción aleatoria.
package modelo;

import java.util.Queue; //Importación de la interfaz "Queue" (manejo).
import java.util.LinkedList; //Importación de la clase "LinkedList" (implementación).

public class ColaReproduccionAleatoria {
    private Queue <Track> colaReproduccionAleatoria; //Declaración de la estructura.

    public ColaReproduccionAleatoria() { //Crear e inicializar la cola de reproducción aleatoria.
        this.colaReproduccionAleatoria = new LinkedList <>();
    }

    public void incorporar(Track track) { //Inserta el track (add) dentro de la cola.
        colaReproduccionAleatoria.add(track);
    }

    public Track remover() { //Recupera y elimina (pop) el primer elemento de la cola.
        return colaReproduccionAleatoria.poll();
    }

    public boolean estaVacia() { //Indicar si la cola de reproducción aleatoria se encuentra vacía.
        return colaReproduccionAleatoria.isEmpty();
    }
}
