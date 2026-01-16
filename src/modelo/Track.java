//1. Representación de pista de audio.
package modelo;

public class Track { //Constructor.
    //private; protege la integirdad del dato; no puede ser modificado directamente desde otra clase.
    private String nombreTrack;
    private String nombreArtista;
    private String rutaAudio;
    private String rutaPortada;

    public Track(String nombreTrack, String nombreArtista, String rutaAudio, String rutaPortada) { //public; permite crear instancias (objetos) de esta clase desde el controlador principal.
        this.rutaAudio = rutaAudio;
        
        if (rutaPortada == null) { //Evitar referencias nulas en portada; asignar una cadena vacía.
            this.rutaPortada = ""; 
        } else {
            this.rutaPortada = rutaPortada;
        }

        if (nombreTrack == null || nombreTrack.isEmpty()) { //Si no existe nombre registrado, usar la ruta del archivo como identificador provisional.
            this.nombreTrack = rutaAudio;
        } else {
            this.nombreTrack = nombreTrack;
        }

        if (nombreArtista == null || nombreArtista.isEmpty()) { //Valor por defecto si no existe artista registrado.
            this.nombreArtista = "No especificado";
        } else {
            this.nombreArtista = nombreArtista;
        }
    }

    //Getters; permite leer los atributos privados sin exponerlos a modificaciones.
    public String getNombreTrack() { 
        return nombreTrack;
    }
    public String getNombreArtista() {
        return nombreArtista;
    }
    public String getRutaAudio() {
        return rutaAudio;
    }
    public String getRutaPortada() {
        return rutaPortada;
    }
}