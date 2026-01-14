import java.io.File; //Acceso y manipulación de archivos del sistema.
import java.util.ArrayList; //Lista redimensionable para la administración temporal de datos.
import java.util.Collections; //Funciones auxiliares para manipulación de estructuras de datos.

import javafx.application.Application; //Punto central de inicialización y cierre de la aplicación JavaFX.
import javafx.beans.binding.Bindings; //Permite sincronizar valores de forma automática.
import javafx.geometry.Insets; //Define márgenes y espaciados (padding).
import javafx.geometry.Pos; //Define posición relativa dentro de contenedores.
import javafx.scene.Scene; //Contenedor principal de la interfaz gráfica.
import javafx.scene.control.Button; //Elemento de interacción básica.
import javafx.scene.control.Label; //Etiqueta para mostrar texto no editable.
import javafx.scene.control.ListCell; //Define la representación visual de un elemento de lista.
import javafx.scene.control.ListView; //Lista interactiva con soporte de selección.
import javafx.scene.control.Slider; //Elemento para control (selector gráfico de rango).
import javafx.scene.effect.DropShadow; //Efecto visual de sombra paralela.
import javafx.scene.image.Image; //Representación de una imagen en memoria.
import javafx.scene.image.ImageView; //Visualizador gráfico de Image.
import javafx.scene.layout.BorderPane; //Distribución en zonas principales (arriba, abajo, centro, izquierda, derecha).
import javafx.scene.layout.HBox; //Contenedor de disposición horizontal.
import javafx.scene.layout.Priority; //Define crecimiento relativo.
import javafx.scene.layout.Region; //Elemento base para controles visuales.
import javafx.scene.layout.VBox; //Contenedor de disposición vertical.
import javafx.scene.media.Media; //Recurso multimedia (archivo de audio).
import javafx.scene.media.MediaPlayer; //Controlador de reproducción multimedia (play/pause).
import javafx.scene.paint.Color; //Definición cromática de la interfaz.
import javafx.scene.paint.ImagePattern; //Permite usar una imagen como relleno de figuras.
import javafx.scene.shape.Rectangle; //Figura geométrica rectangular.
import javafx.stage.DirectoryChooser; //Acceso visual al sistema de archivos.
import javafx.stage.Stage; //Ventana principal de la aplicación.
import javafx.util.Duration; //Representación de tiempo (segundos/milisegundos).

import modelo.*; //Acceso a la capa lógica del sistema.


public class Main extends Application { //Main hereda de la clase "Application" de JavaFX sus métodos y comportamiento (de lo contrario JavaFX no reconocería la clase como una aplicación válida).
    //Etiquetas (labels) informativas para mostrar datos del track y tiempos de reproducción.
    private Label lblNombreTrack = new Label("No especificado");
    private Label lblNombreArtista = new Label("No especificado");
    private Label lblTiempoActual = new Label("0:00");
    private Label lblTiempoRestante = new Label("-0:00"); 
    
    //Controles deslizantes (sliders) para la interacción con el progreso y el volumen.
    private Slider sldReproduccion = new Slider();
    private Slider sldVolumen = new Slider(0, 100, 50); //Configuración inicial (min, max, valor actual).

    //Contenedores visuales para la carátula y la lista de canciones.
    private Rectangle rectPortada = new Rectangle(300, 300); //Configuración inicial (anch, alt).
    private ListView<Track> listaReproduccionVisual = new ListView <>();

    //Controles de interacción directa del usuario (controladores).
    private Button btnReproducirPausar, btnAnterior, btnSiguiente; 
    private Button btnModoAleatorio, btnModoRepetir;
    private Button btnDesplazarArriba, btnDesplazarAbajo, btnImportarCarpeta;

    //Recursos gráficos cargados en memoria para iconografía de la interfaz.
    private Image imgReproducir, imgPausar, imgSiguiente, imgAnterior;
    private Image imgModoAleatorioOff, imgModoAleatorioOn;
    private Image imgModoRepetirOff, imgModoRepetirOn, imgimgModoRepetirUno;
    private Image imgVolumen, imgImportarCarpeta, imgDesplazarArriba, imgDesplazarAbajo;

    // Conexión con las estructuras de datos del paquete 'modelo'.
    private ListaReproduccion listaReproduccion = new ListaReproduccion(); //Lista doblemente enlazada (estructura principal).
    private PilaHistorialReproduccion historialReproduccion = new PilaHistorialReproduccion(); //Pila LIFO.  
    private ColaReproduccionAleatoria ReproduccionAleatoria = new ColaReproduccionAleatoria(); //Cola FIFO.

    private MediaPlayer reproductor; //Motor multimedia responsable de la reproducción de audio.

    //Variables de estado que controlan el comportamiento del reproductor.
    private boolean reproduccionActiva = false; //Reproducción activa
    private boolean reproduccionAleatoria = false; //Reprodcción aleatoria
    private int repetirActivo = 0; // Estados de repetición: 0 = no activo, 1 = activo, 2 = activo para 1. 


    @Override //Indicar redefinición del punto de entrada gráfico de JavaFX (start = método heredado).
    public void start(Stage ventanaPrincipal) { //Stage; representa la ventana del sistema operativo.
        RecursosVisuales(); //Precarga de activos visuales requeridos por la UI.

        ListaPrueba(); //Inicializar contenido de prueba para verificación funcional.
        
        BorderPane interfazPrincipal = new BorderPane(); //Estructura base de la interfaz gráfica.
        //Inicializar los paneles laterales que componen la interfaz principal.
        VBox panelLateralIzquierdo = inicializarPanelLateralIzquierdo();
        VBox panelLateralDerecho = inicializarPanelLateralDerecho(ventanaPrincipal);

        //Región izquierda para controles y el centro para la lista de reproducción.
        interfazPrincipal.setLeft(panelLateralIzquierdo);
        interfazPrincipal.setCenter(panelLateralDerecho);

        configurarInteractividadBotonesPrincipal(); //Vincular la lógica a los botones creados.

        Scene vistaPrincipal = new Scene(interfazPrincipal, 1000, 720); //Escena principal con dimensiones iniciales definidas (ancho, alto).
        //Personalización visual de componentes nativos sin archivos CSS externos.
        String cssInterfaz = "data:text/css;base64," + java.util.Base64.getEncoder().encodeToString(
            (
                ".slider .track { " +
                "   -fx-background-color: #444; " +
                "   -fx-pref-height: 4px; " +
                "   -fx-background-radius: 2px; " +
                "} " +
                ".slider .thumb { " +
                "   -fx-background-color: #e0e0e0; " +
                "   -fx-pref-width: 14px; " +
                "   -fx-pref-height: 14px; " +
                "   -fx-background-radius: 50%; " +
                "   -fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 1); " +
                "} " +
                ".slider:hover .thumb { -fx-background-color: white; } " +

                ".list-view { -fx-background-color: transparent; } " +
                ".list-cell { " +
                "   -fx-background-color: transparent; " +
                "   -fx-padding: 0; " +
                "   -fx-border-width: 0; " +
                "} " +
                ".list-cell:filled:selected, .list-cell:filled:selected:focused { " +
                "   -fx-background-color: #101010; " +
                "   -fx-background-radius: 12px; " +
                "} " +
                ".list-cell:filled:hover { " +
                "   -fx-background-color: #151515; " +
                "   -fx-background-radius: 12px; " +
                "} " +

                ".list-view .scroll-bar:vertical { " +
                "   -fx-background-color: transparent; " +
                "   -fx-pref-width: 9; " +
                "   -fx-padding: 0; " +
                "} " +
                ".list-view .scroll-bar:vertical .track { " +
                "   -fx-background-color: transparent; " +
                "   -fx-border-color: transparent; " +
                "} " +
                ".list-view .scroll-bar:vertical .thumb { " +
                "   -fx-background-color: #666; " +
                "   -fx-background-radius: 10px; " +
                "} " +
                ".list-view .scroll-bar:vertical:hover .thumb { -fx-background-color: #999; } " +
                ".list-view .scroll-bar .increment-button, " +
                ".list-view .scroll-bar .decrement-button { -fx-pref-height: 0; -fx-padding: 0; } " +
                ".list-view .scroll-bar .increment-arrow, " +
                ".list-view .scroll-bar .decrement-arrow { -fx-padding: 0; -fx-shape: ' '; } "
            ).getBytes()
        );
        vistaPrincipal.getStylesheets().add(cssInterfaz); //Asociar los estilos visuales a la vista principal.

        ventanaPrincipal.setTitle("Reproductor"); //Definir el título de la ventana principal (setTitle).
        ventanaPrincipal.setScene(vistaPrincipal); //Inseertar la escena dentro del contenedor principal.
        ventanaPrincipal.show(); //Hacer visible la ventana al usuario (show).

        sincronizar(false); //Sincronizar el estado visual inicial antes de la interacción del usuario.
    }


    private void RecursosVisuales() { //Inicializar y almacenar recursos necesarios para la interfaz gráfica.
        imgReproducir = establecerVisual("recursos_visuales/reproducir.png");
        imgPausar = establecerVisual("recursos_visuales/pausar.png");
        imgSiguiente = establecerVisual("recursos_visuales/siguiente.png");
        imgAnterior = establecerVisual("recursos_visuales/anterior.png");
        imgModoAleatorioOff = establecerVisual("recursos_visuales/modo_aleatorio_off.png");
        imgModoAleatorioOn = establecerVisual("recursos_visuales/modo_aleatorio_on.png");
        imgModoRepetirOff = establecerVisual("recursos_visuales/modo_repetir_off.png");
        imgModoRepetirOn = establecerVisual("recursos_visuales/modo_repetir_on.png");
        imgimgModoRepetirUno = establecerVisual("recursos_visuales/modo_repetir_uno.png");
        imgVolumen = establecerVisual("recursos_visuales/volumen.png");
        imgImportarCarpeta = establecerVisual("recursos_visuales/importar_carpeta.png");
        imgDesplazarArriba = establecerVisual("recursos_visuales/desplazar_arriba.png");
        imgDesplazarAbajo = establecerVisual("recursos_visuales/desplazar_abajo.png");
    }

    private VBox inicializarPanelLateralIzquierdo() { //Inicializar la sección izquierda de la interfaz gráfica.
        VBox panelVertical = new VBox(); //Panel base con disposición vertical de elementos (organiza los componentes de forma secuencial).
        panelVertical.setPrefWidth(500); 
        panelVertical.setPadding(new Insets(50)); 
        panelVertical.setAlignment(Pos.CENTER); 
        panelVertical.setStyle("-fx-background-color: #101010;"); 

        double maxWidth = 400; //Valor de referencia para mantener consistencia visual.

        //Configuración visual del área de carátula del track.
        rectPortada.setWidth(maxWidth); 
        rectPortada.setHeight(maxWidth);
        rectPortada.setArcWidth(25); //Borde redondeado
        rectPortada.setArcHeight(25);
        rectPortada.setFill(Color.rgb(40, 40, 40)); //Color base utilizado cuando no existe imagen asociada.
        
        DropShadow sombraPortada = new DropShadow(); //Efecto de sombra para generar sensación de profundidad.
        sombraPortada.setColor(Color.rgb(0, 0, 0, 0.6));
        sombraPortada.setRadius(40);
        sombraPortada.setOffsetY(20);
        rectPortada.setEffect(sombraPortada);

        //Contenedor de información textual del track.
        VBox informacion = new VBox(5);
        informacion.setAlignment(Pos.CENTER_LEFT);
        informacion.setPadding(new Insets(25, 0, 15, 0));
        informacion.setMaxWidth(maxWidth); 

        lblNombreTrack.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        lblNombreArtista.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 20px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        informacion.getChildren().addAll(lblNombreTrack, lblNombreArtista);

        //Panel de control del progreso de reproducción.
        VBox panelParcialReproduccion = new VBox(8);
        panelParcialReproduccion.setMaxWidth(maxWidth); 
        sldReproduccion.setStyle("-fx-control-inner-background: #444; -fx-accent: #b0b0b0; -fx-cursor: hand;");
        
        HBox referenciaTiempo = new HBox();
        referenciaTiempo.setPadding(new Insets(0, 5, 0, 5));
        String estiloReferenciaTiempo = "-fx-text-fill: #999; -fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';";
        lblTiempoActual.setStyle(estiloReferenciaTiempo);
        lblTiempoRestante.setStyle(estiloReferenciaTiempo);
        
        Region espaciadoReferenciaTiempo = new Region();
        HBox.setHgrow(espaciadoReferenciaTiempo, Priority.ALWAYS);
        referenciaTiempo.getChildren().addAll(lblTiempoActual, espaciadoReferenciaTiempo, lblTiempoRestante);
        panelParcialReproduccion.getChildren().addAll(sldReproduccion, referenciaTiempo);

        HBox panelParcialControles = new HBox();
        panelParcialControles.setAlignment(Pos.CENTER);
        panelParcialControles.setSpacing(25); 
        panelParcialControles.setPadding(new Insets(20, 0, 20, 0));
        panelParcialControles.setMaxWidth(maxWidth);
        panelParcialControles.setTranslateX(0); 

        btnModoAleatorio = establecerVisualBoton(imgModoAleatorioOff, 16, 40);
        btnAnterior = establecerVisualBoton(imgAnterior, 20, 40);
        btnReproducirPausar = establecerVisualBoton(imgReproducir, 35, 70);       
        btnSiguiente = establecerVisualBoton(imgSiguiente, 20, 40);
        btnModoRepetir = establecerVisualBoton(imgModoRepetirOff, 16, 40);

        panelParcialControles.getChildren().addAll(btnModoAleatorio, btnAnterior, btnReproducirPausar, btnSiguiente, btnModoRepetir);

        HBox panelParcialVolumen = new HBox(15);
        panelParcialVolumen.setAlignment(Pos.CENTER); 
        panelParcialVolumen.setMaxWidth(maxWidth);
        panelParcialVolumen.setPadding(new Insets(22, 30, 0, 0)); 
        
        ImageView recursoVolumen = new ImageView(imgVolumen);
        recursoVolumen.setFitHeight(14); 
        recursoVolumen.setPreserveRatio(true);
        recursoVolumen.setOpacity(0.7);

        sldVolumen.setPrefWidth(200); 
        sldVolumen.setStyle("-fx-control-inner-background: #333; -fx-accent: #888; -fx-cursor: hand;");

        panelParcialVolumen.getChildren().addAll(recursoVolumen, sldVolumen);

        panelVertical.getChildren().addAll(rectPortada, informacion, panelParcialReproduccion, panelParcialControles, panelParcialVolumen);
        
        return panelVertical;
    }

    private VBox inicializarPanelLateralDerecho(Stage ventanaPrincipal) {
        VBox panelVertical = new VBox();
        panelVertical.setStyle("-fx-background-color: #000000;"); 
        panelVertical.setPadding(new Insets(30));
        panelVertical.setSpacing(15);

        HBox panelSuperior = new HBox(15);
        panelSuperior.setAlignment(Pos.CENTER_LEFT);
        Label lblTextoSuperior = new Label("Seguir reproduciendo");
        lblTextoSuperior.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        Region espacioPanelSuperior = new Region();
        HBox.setHgrow(espacioPanelSuperior, Priority.ALWAYS);

        btnImportarCarpeta = establecerVisualBoton(imgImportarCarpeta, 18, 40);
        btnImportarCarpeta.setStyle("-fx-background-color: #101010; -fx-cursor: hand; -fx-padding: 8; -fx-background-radius: 10;"); 
        btnImportarCarpeta.setOnAction(accion -> importarCarpeta(ventanaPrincipal));

        panelSuperior.getChildren().addAll(lblTextoSuperior, espacioPanelSuperior, btnImportarCarpeta);

        listaReproduccionVisual.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #000000;");
        VBox.setVgrow(listaReproduccionVisual, Priority.ALWAYS);
        
        listaReproduccionVisual.setCellFactory(parametro -> new ListCell<Track>() {
            @Override
            protected void updateItem(Track trackEnFila, boolean estaVacio) {
                super.updateItem(trackEnFila, estaVacio);
                setText(null);
                setGraphic(null);
                setStyle("-fx-background-color: transparent;"); 

                if (estaVacio || trackEnFila == null) {

                } else {
                    HBox panelFilaVisual = new HBox(15);
                    panelFilaVisual.setAlignment(Pos.CENTER_LEFT);
                    
                    panelFilaVisual.styleProperty().bind(
                        Bindings.when(selectedProperty()).then("-fx-background-color: #101010; -fx-background-radius: 12px; -fx-padding: 8 20 8 20;").otherwise("-fx-background-color: transparent; -fx-padding: 8 20 8 20;")
                    );

                    Label lblNumeroTrack = new Label(String.valueOf(getIndex() + 1));
                    lblNumeroTrack.textFillProperty().bind(
                        Bindings.when(selectedProperty()).then(Color.WHITE).otherwise(Color.web("#666"))
                    );
                    lblNumeroTrack.setStyle("-fx-font-size: 14px; -fx-min-width: 25px; -fx-font-weight: bold;");

                    VBox informacion = new VBox(4);
                    Label lblNombreTrack = new Label(trackEnFila.getNombreTrack());
                    lblNombreTrack.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
                    Label lblNombreArtista = new Label(trackEnFila.getNombreArtista());
                    lblNombreArtista.setStyle("-fx-text-fill: #999; -fx-font-size: 13px; -fx-font-weight: bold;");
                    
                    informacion.getChildren().addAll(lblNombreTrack, lblNombreArtista);

                    Region espacioFilaVisual = new Region();
                    HBox.setHgrow(espacioFilaVisual, Priority.ALWAYS);
                    Label lblReferenciaOpciones = new Label("•••"); 
                    lblReferenciaOpciones.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-cursor: hand;");

                    panelFilaVisual.getChildren().addAll(lblNumeroTrack, informacion, espacioFilaVisual, lblReferenciaOpciones);
                    
                    setGraphic(panelFilaVisual);
                }
            }
        });

        HBox panelInferior = new HBox(5);
        panelInferior.setMaxWidth(Double.MAX_VALUE); 
        panelInferior.setAlignment(Pos.CENTER_RIGHT);
        panelInferior.setPadding(new Insets(0, 0, 0, 0));

        btnDesplazarArriba = establecerVisualBoton(imgDesplazarArriba, 22, 45);
        btnDesplazarAbajo = establecerVisualBoton(imgDesplazarAbajo, 22, 45);
        Label lblReferenciaOrden = new Label("Reordenar:");
        lblReferenciaOrden.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        panelInferior.getChildren().addAll(lblReferenciaOrden, btnDesplazarArriba, btnDesplazarAbajo);
        panelVertical.getChildren().addAll(panelSuperior, listaReproduccionVisual, panelInferior);

        return panelVertical;
    }


    private void configurarInteractividadBotonesPrincipal() {
        btnReproducirPausar.setOnAction(accion -> {
            if (reproductor == null) {
                return;
            }

            if (reproduccionActiva) {
                reproductor.pause();
                reproduccionActiva = false;

            } else {
                reproductor.play();
                reproduccionActiva = true;
            }

            actualizarReproducirPausar();
        });

        btnModoAleatorio.setOnAction(accion -> {
            reproduccionAleatoria = !reproduccionAleatoria; 
            
            if (reproduccionAleatoria) {
                actualizarVisualBoton(btnModoAleatorio, imgModoAleatorioOn);
                
                btnDesplazarArriba.setDisable(true);
                btnDesplazarAbajo.setDisable(true);

                Track trackActual = null;
                if (listaReproduccion.getActual() != null) {
                    trackActual = listaReproduccion.getActual().getTrack();
                }

                ArrayList<Track> listaReproduccionAleatoria = new ArrayList<>();
                NodoTrack actual = listaReproduccion.getInicio();
                while(actual != null) { 
                    listaReproduccionAleatoria.add(actual.getTrack()); 
                    actual = actual.getSiguiente(); 
                }

                Collections.shuffle(listaReproduccionAleatoria);

                if (trackActual != null) {
                    int posicion = listaReproduccionAleatoria.indexOf(trackActual);
                    if (posicion != -1) {
                        Collections.swap(listaReproduccionAleatoria, 0, posicion);
                    }
                }

                listaReproduccionVisual.getItems().setAll(listaReproduccionAleatoria);

                establecerColaReproduccionAleatoria(listaReproduccionAleatoria);

                if (!ReproduccionAleatoria.estaVacia()) {
                    ReproduccionAleatoria.remover();
                }

                listaReproduccionVisual.getSelectionModel().select(0);
                listaReproduccionVisual.scrollTo(0);

            } else {
                actualizarVisualBoton(btnModoAleatorio, imgModoAleatorioOff);
                btnDesplazarArriba.setDisable(false);
                btnDesplazarAbajo.setDisable(false);
                actualizarListaReproduccionVisual(); 
                
                if (listaReproduccion.getActual() != null) {
                    Track trackActual = listaReproduccion.getActual().getTrack();
                    
                    listaReproduccionVisual.getSelectionModel().select(trackActual);
                    
                    listaReproduccionVisual.scrollTo(trackActual);
                }
            }
        });

        btnModoRepetir.setOnAction(accion -> {
            repetirActivo++;
            if (repetirActivo > 2) {
                repetirActivo = 0;
            }

            switch (repetirActivo) {
                case 0: 
                    actualizarVisualBoton(btnModoRepetir, imgModoRepetirOff); 
                    break;
                case 1: 
                    actualizarVisualBoton(btnModoRepetir, imgModoRepetirOn); 
                    break;
                case 2: 
                    actualizarVisualBoton(btnModoRepetir, imgimgModoRepetirUno); 
                    break;
            }
        });

        btnSiguiente.setOnAction(accion -> cambiarTrack(1));
        btnAnterior.setOnAction(accion -> cambiarTrack(-1));

        configurarInteractividadBoton();
    }

    private void configurarInteractividadBoton() {
        sldReproduccion.setOnMouseReleased(accion -> {
            if (reproductor != null) {
                reproductor.seek(Duration.seconds(sldReproduccion.getValue()));
            }
        });

        sldVolumen.valueProperty().addListener((propiedad, valorAnterior, valorActual) -> {
            if (reproductor != null) {
                reproductor.setVolume(valorActual.doubleValue() / 100.0);
            }
        });

        btnDesplazarArriba.setOnAction(accion -> desplazarTrackEnLista(-1));
        btnDesplazarAbajo.setOnAction(accion -> desplazarTrackEnLista(1));

        listaReproduccionVisual.setOnMouseClicked(accion -> {
            if (accion.getClickCount() == 2) {
                Track trackSeleccionado = listaReproduccionVisual.getSelectionModel().getSelectedItem();
        
                if (trackSeleccionado != null) {
                    NodoTrack seleccionado = buscarNodoTrack(trackSeleccionado);
                    if (seleccionado != null) {
                        listaReproduccion.setActual(seleccionado);
                        sincronizar(true);
                    }
                }
            }
        });
    }


    private void cambiarTrack(int direccion) {
        if (repetirActivo == 2 && direccion == 1 && reproductor != null) { 
            reproductor.seek(Duration.ZERO); 
            return; 
        }
        
        NodoTrack actual = listaReproduccion.getActual();
        if (actual == null) return;
        
        if (direccion == 1) {
            historialReproduccion.incorporar(actual.getTrack());
        }
        
        NodoTrack siguiente = null;
        if (direccion == 1) {
            if (reproduccionAleatoria) {
                if (!ReproduccionAleatoria.estaVacia()) {
                    siguiente = buscarNodoTrack(ReproduccionAleatoria.remover());

                } else if (repetirActivo == 1) {
                    sincornizarColaVisual(); 
                    if (!ReproduccionAleatoria.estaVacia()) {
                        siguiente = buscarNodoTrack(ReproduccionAleatoria.remover());
                    }
                }

            } else {
                siguiente = actual.getSiguiente();
                if (siguiente == null && repetirActivo == 1) {
                    siguiente = listaReproduccion.getInicio();
                }
            }

        } else {
            if (!historialReproduccion.estaVacia()) {
                siguiente = buscarNodoTrack(historialReproduccion.remover());

            } else if (actual.getAnterior() != null) {
                siguiente = actual.getAnterior();
            }
        }

        if (siguiente != null) {
            listaReproduccion.setActual(siguiente);
            sincronizar(true);
            listaReproduccionVisual.getSelectionModel().select(siguiente.getTrack());
        }
    }

    private void sincronizar(boolean reproduccionAutomatica) {
        NodoTrack actual = listaReproduccion.getActual();
        if (actual == null) {
            return;
        }

        Track trackActual = actual.getTrack();
        lblNombreTrack.setText(trackActual.getNombreTrack());
        lblNombreArtista.setText(trackActual.getNombreArtista());

        try {
            if (trackActual.getRutaPortada() != null && !trackActual.getRutaPortada().isEmpty()) {
                File archivoPortada = new File(trackActual.getRutaPortada());
                if (archivoPortada.exists()) {
                    rectPortada.setFill(new ImagePattern(new Image(archivoPortada.toURI().toString())));

                } else { 
                    rectPortada.setFill(Color.rgb(40,40,40));
                }

            } else { 
                rectPortada.setFill(Color.rgb(40,40,40));
            }

        } catch (Exception error) {
            rectPortada.setFill(Color.rgb(40,40,40));
        }

        if (reproductor != null) { 
            reproductor.stop(); 
            reproductor.dispose(); 
        }

        try {
            File archivoTrack = new File(trackActual.getRutaAudio());
            if (archivoTrack.exists()) {
                Media media = new Media(archivoTrack.toURI().toString());
                reproductor = new MediaPlayer(media);
                reproductor.setVolume(sldVolumen.getValue() / 100.0);

                reproductor.setOnReady(() -> {
                    double TiempoTotalTrack = reproductor.getTotalDuration().toSeconds();
                    sldReproduccion.setMax(TiempoTotalTrack);
                });

                reproductor.currentTimeProperty().addListener((propiedad, tiempoAnterior, tiempoActual) -> {
                    if (!sldReproduccion.isValueChanging()) { 
                        double parcial = tiempoActual.toSeconds();
                        double total = reproductor.getTotalDuration().toSeconds();
                        sldReproduccion.setValue(parcial);
                        lblTiempoActual.setText(transformarTiempoReproduccion(parcial));
                        lblTiempoRestante.setText("-" + transformarTiempoReproduccion(total - parcial));
                    }
                });

                reproductor.setOnEndOfMedia(() -> btnSiguiente.fire());

                if (reproduccionAutomatica) { 
                    reproductor.play(); 
                    reproduccionActiva = true; 

                } else { 
                    reproduccionActiva = false; 
                }

                actualizarReproducirPausar();
            }
        } catch (Exception error) { 
            System.out.println("Error al cargar el audio; " + error); 
        }
    }

    private void sincornizarColaVisual() {
        ReproduccionAleatoria = new ColaReproduccionAleatoria();
        for (Track actual : listaReproduccionVisual.getItems()) {
            ReproduccionAleatoria.incorporar(actual);
        }
    }


    private void establecerColaReproduccionAleatoria(ArrayList<Track> listaReproduccionAleatoria) {
        ReproduccionAleatoria = new ColaReproduccionAleatoria(); 

        for (Track trackActual : listaReproduccionAleatoria) {
            ReproduccionAleatoria.incorporar(trackActual);
        }
    }

    private void desplazarTrackEnLista(int direccion) {
        Track trackSeleccionado = listaReproduccionVisual.getSelectionModel().getSelectedItem();
        if (trackSeleccionado == null) return;
        NodoTrack seleccionado = buscarNodoTrack(trackSeleccionado);
        if (seleccionado != null) {
            if (direccion == -1) {
                listaReproduccion.desplazarArriba(seleccionado);
            }

            else {
                listaReproduccion.desplazarAbajo(seleccionado);
            }

            actualizarListaReproduccionVisual();
            listaReproduccionVisual.getSelectionModel().select(trackSeleccionado);
        }
    }

    private void importarCarpeta(Stage ventanaPrincipal) {
        DirectoryChooser selectorCarpeta = new DirectoryChooser();
        File carpeta = selectorCarpeta.showDialog(ventanaPrincipal);
        if (carpeta != null) {
            File[] listaArchivo = carpeta.listFiles((carpetaArchivo, nombreArchivo) -> nombreArchivo.toLowerCase().endsWith(".mp3"));
            if (listaArchivo != null) {
                for (File archivo : listaArchivo) {
                    listaReproduccion.incorporarFinal(new Track(archivo.getName().replace(".mp3",""), "No especificado", archivo.getAbsolutePath(), null));
                }
                actualizarListaReproduccionVisual();
            }
        }
    }


    private NodoTrack buscarNodoTrack(Track trackSeleccionado) {
        NodoTrack actual = listaReproduccion.getInicio();
        while (actual != null) { 
            if (actual.getTrack() == trackSeleccionado) {
                return actual; 
            } 
            actual = actual.getSiguiente();
        }
        return null;
    }
    
    private void actualizarListaReproduccionVisual() {
        listaReproduccionVisual.getItems().clear();
        NodoTrack actual = listaReproduccion.getInicio();
        while (actual != null) {
            listaReproduccionVisual.getItems().add(actual.getTrack());
            actual = actual.getSiguiente();
        }
    }

    private void actualizarReproducirPausar() {
        if (reproduccionActiva) {
            actualizarVisualBoton(btnReproducirPausar, imgPausar);
        } else { 
            actualizarVisualBoton(btnReproducirPausar, imgReproducir);
        }
    }

    private Image establecerVisual(String recurso) {
        try {
            File archivo = new File(recurso);
            if(archivo.exists()) {
                return new Image(archivo.toURI().toString());
            } else {
            return null; 
            }

        } catch (Exception error) {
            return null;
        }
    }

    private Button establecerVisualBoton(Image recursoVisual, int alto, int ancho) {
        ImageView recursoBoton = new ImageView(recursoVisual);
        recursoBoton.setFitHeight(alto);
        recursoBoton.setPreserveRatio(true);
        if (recursoVisual == null) { 
            recursoBoton.setFitWidth(alto); recursoBoton.setOpacity(0); 
        }

        Button boton = new Button();
        boton.setGraphic(recursoBoton);
        boton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");
        boton.setAlignment(Pos.CENTER); 

        if (ancho > 0) {
            boton.setMinWidth(ancho);
            boton.setPrefWidth(ancho);
            boton.setMaxWidth(ancho);
        }
        return boton;
    }

    private void actualizarVisualBoton(Button boton, Image actualizarVisual) {
        if (boton.getGraphic() instanceof ImageView) {
            ((ImageView) boton.getGraphic()).setImage(actualizarVisual);
        }
    }

    private String transformarTiempoReproduccion(double segundosTotales) {
        if (Double.isNaN(segundosTotales)) { 
            return "0:00";
        }

        segundosTotales = Math.abs(segundosTotales); 
        int minutos = (int) segundosTotales / 60;
        int segundos = (int) segundosTotales % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    private void ListaPrueba() {
        listaReproduccion.incorporarFinal(new Track("Champagne Supernova (Remastered)", "Oasis", "lista_prueba/Champagne Supernova (Remastered).mp3", "lista_prueba/chs.jpg"));
        listaReproduccion.incorporarFinal(new Track("Stand By Me (Remastered)", "Oasis", "lista_prueba/Stand By Me (Remastered).mp3", "lista_prueba/sbm.jpg"));
        actualizarListaReproduccionVisual();
    }


    public static void main(String[] args) { 
        launch(args); 
    }
}