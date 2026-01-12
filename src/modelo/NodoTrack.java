//Elemento constitutivo de la Lista Doblemente Enlazada.
package modelo;

public class NodoTrack {
    private Track track; 
    private NodoTrack siguiente;
    private NodoTrack anterior;

    public NodoTrack(Track track) {
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

    public void setSiguiente(NodoTrack siguiente) { 
        this.siguiente = siguiente;
    }

    public NodoTrack getAnterior() { 
        return anterior;
    }
    public void setAnterior(NodoTrack anterior) { 
        this.anterior = anterior;
    }

    public void setTrack(Track track) {
        this.track = track;
    }
}