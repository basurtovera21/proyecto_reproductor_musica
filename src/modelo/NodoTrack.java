//Unidad básica que compone la estructura de la lista doblemente enlazada.
package modelo;

public class NodoTrack {
    private Track track;
    //Referencias que permiten la navegación entre nodos de la lista.
    private NodoTrack siguiente;
    private NodoTrack anterior;

    public NodoTrack(Track track) { //Constructor que inicializa el nodo con un "track" y referencias nulas.
        this.track = track;
        this.siguiente = null;
        this.anterior = null;
    }

    public Track getTrack() { 
        return track;
    }
    
    public NodoTrack getSiguiente() { 
        return siguiente;
    }

    //Setters; permiten actualizar los atributos privados aplicando control sobre las modificaciones.
    public void setSiguiente(NodoTrack siguiente) { 
        this.siguiente = siguiente; //Modificar el puntero para "enlazar" este nodo con otro en la lista.
    }

    public NodoTrack getAnterior() { 
        return anterior;
    }
    public void setAnterior(NodoTrack anterior) { 
        this.anterior = anterior; //Modificar el puntero para "enlazar" este nodo con otro en la lista.
    }

    public void setTrack(Track track) {
        this.track = track; //Reemplazar el contenido del nodo si fuera necesario.
    }
}