//3. Estructura: Lista doblemente enlazada; administración del orden de reproducción.
package modelo;

public class ListaReproduccion {
    //Referencias internas que permiten recorrer la lista y controlar la reproducción.
    private NodoTrack inicio;
    private NodoTrack fin;
    private NodoTrack actual;

    public ListaReproduccion() { //Constructor que crea una lista de reproducción vacía.
        this.inicio = null;
        this.fin = null;
        this.actual = null;
    }

    public void incorporarFinal(Track track) { //Inserción al final. Crear un nuevo nodo y reajustar los enlaces.
        NodoTrack nuevo = new NodoTrack(track);

        if (inicio == null) { //Lista vacía; el nuevo nodo es el único elemento (inicio, fin y actual).
            inicio = nuevo; 
            fin = nuevo;
            actual = nuevo;
        } else { //Lista con elementos; se enlaza el nuevo nodo después del actual 'fin' y se actualiza el puntero 'fin'.
            fin.setSiguiente(nuevo); //Referencia del nodo final al nuevo nodo.
            nuevo.setAnterior(fin); //Nuevo nodo establece referencia hacia el anterior nodo final.
            fin = nuevo; //Actualización al nuevo nodo final.
        }
    }

    public NodoTrack getActual() {
        return actual;
    }

    public void setActual(NodoTrack actual) {
        this.actual = actual;
    }
    
    public NodoTrack getInicio() { //Permitir al controlador iniciar recorridos desde el principio de la lista.
        return inicio;
    }

    public void desplazarArriba(NodoTrack nodoActivo) { //Permitir modificar el orden de reproducción desplazando el elemento activo hacia arriba.
        if (nodoActivo == null || nodoActivo == inicio) { //No es posible desplazar si el nodo es el primero de la lista (o si no ha sido seleccionado).
            return;
        }

        NodoTrack nodoAnterior = nodoActivo.getAnterior(); //Identificar nodo anterior (arriba). 
        Track trackAnterior = nodoAnterior.getTrack(); //Variable que contiene track a bajar.
        Track trackActivo = nodoActivo.getTrack(); //Variable que contiene track a subir.
        nodoAnterior.setTrack(trackActivo); //Nodo arriba; track que estaba abajo. 
        nodoActivo.setTrack(trackAnterior); //Nodo abajo; track que estaba arriba.

        if (actual == nodoActivo) { //Track en reproducción; apunta al track desplazado.
            actual = nodoAnterior; 
        }
    }

    public void desplazarAbajo(NodoTrack nodoActivo) { //Permitir modificar el orden de reproducción desplazando el elemento activo hacia abajo.
        if (nodoActivo == null || nodoActivo == fin) { //No es posible desplazar si el nodo es el último de la lista (o si no ha sido seleccionado).
            return;
        }

        NodoTrack nodoSiguiente = nodoActivo.getSiguiente(); //Identificar nodo siguiente (abajo).
        Track trackSiguiente = nodoSiguiente.getTrack(); //Variable que contiene track a subir.
        Track trackActivo = nodoActivo.getTrack(); //Variable que contiene track a bajar.
        nodoSiguiente.setTrack(trackActivo); //Nodo abajo; track que estaba arriba.
        nodoActivo.setTrack(trackSiguiente); //Nodo arriba; track que estaba abajo.

        if (actual == nodoActivo) { //Track en reproducción; apunta al track desplazado.
            actual = nodoSiguiente;
        }
    }
}