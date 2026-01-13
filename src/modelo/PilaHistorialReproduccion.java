//4. Estructura: LIFO (Last In, First Out); administración del historial de reproducción.
package modelo;

import java.util.Stack; //Importación de la estructura "Stack"

public class PilaHistorialReproduccion {
    private Stack<Track> pilaHistorialReproduccion; //Declaración de la estructura.

    public PilaHistorialReproduccion() { //Constructor que crea una pila de historial de reproducción.
        this.pilaHistorialReproduccion = new Stack<>();
    }

    public void incorporar(Track track) { //Insertar un track en la pila, registrándolo como el último elemento del historial (push).
        pilaHistorialReproduccion.push(track);
    }

    public Track remover() { //Extraer el último track agregado (pop), permitiendo la navegación al track anterior.
        if (pilaHistorialReproduccion.isEmpty()) { //Comprobar si la pila está vacía antes de realizar la operación pop.
            return null;
        }

        return pilaHistorialReproduccion.pop();
    }

    public boolean estaVacia() { //Indicar si existen registros previos en el historial de reproducción.
        return pilaHistorialReproduccion.isEmpty();
    }
}