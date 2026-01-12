package modelo;

public class ListaReproduccion {
    private NodoTrack inicio;
    private NodoTrack fin;
    private NodoTrack actual;

    public ListaReproduccion() {
        this.inicio = null;
        this.fin = null;
        this.actual = null;
    }

    public void incorporarFinal(Track track) {
        NodoTrack nuevo = new NodoTrack(track);

        if (inicio == null) {
            inicio = nuevo; 
            fin = nuevo;
            actual = nuevo;
        } else {
            fin.setSiguiente(nuevo);
            nuevo.setAnterior(fin);
            fin = nuevo;
        }
    }

    public NodoTrack getActual() {
        return actual;
    }

    public void setActual(NodoTrack actual) {
        this.actual = actual;
    }
    
    public NodoTrack getInicio() {
        return inicio;
    }

    public void desplazarArriba(NodoTrack nodoActivo) {
        if (nodoActivo == null || nodoActivo == inicio) {
            return;
        }

        NodoTrack nodoAnterior = nodoActivo.getAnterior();
        Track trackAnterior = nodoAnterior.getTrack();
        Track trackActivo = nodoActivo.getTrack();
        nodoAnterior.setTrack(trackActivo); 
        nodoActivo.setTrack(trackAnterior);   

        if (actual == nodoActivo) {
            actual = nodoAnterior; 
        }
    }

    public void desplazarAbajo(NodoTrack nodoActivo) {
        if (nodoActivo == null || nodoActivo == fin) {
            return;
        }

        NodoTrack nodoSiguiente = nodoActivo.getSiguiente();
        Track trackSiguiente = nodoSiguiente.getTrack();
        Track trackActivo = nodoActivo.getTrack();
        nodoSiguiente.setTrack(trackActivo);
        nodoActivo.setTrack(trackSiguiente);

        if (actual == nodoActivo) {
            actual = nodoSiguiente;
        }
    }
}