package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VisitorController {

    @FXML private ComboBox<Atraccion> selectorAtraccion;
    @FXML private ComboBox<Atraccion> selectorFavorito;
    @FXML private ListView<String> listaFavoritos;
    @FXML private Label lblNombre;
    @FXML private Label lblEdad;
    @FXML private Label lblEstatura;
    @FXML private Label lblSaldo;
    @FXML private Label lblTicket;
    @FXML private Label lblPosicionFila;
    @FXML private Label lblTiempoEspera;

    private final ParkDataService dataService;
    private Visitante visitanteActual;

    public VisitorController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    public void setVisitanteInicial(Visitante visitante) {
        this.visitanteActual = visitante;
    }

    @FXML
    public void initialize() {
        // Cargar atracciones en selectores
        selectorAtraccion.getItems().addAll(dataService.getAtracciones());
        selectorFavorito.getItems().addAll(dataService.getAtracciones());

        // Configurar cómo se muestran las atracciones
        for (ComboBox<Atraccion> cb : new ComboBox[]{selectorAtraccion, selectorFavorito}) {
            cb.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Atraccion a, boolean empty) {
                    super.updateItem(a, empty);
                    if (empty || a == null) {
                        setText(null);
                    } else {
                        String estadoEmoji = switch (a.getEstado()) {
                            case ACTIVA -> "🟢";
                            case EN_MANTENIMIENTO -> "🟠";
                            case CERRADA -> "🔴";
                        };
                        setText(estadoEmoji + " " + a.getNombre() + " (" + a.getEstado() + ")");
                    }
                }
            });
            cb.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Atraccion a, boolean empty) {
                    super.updateItem(a, empty);
                    setText(empty || a == null ? null : a.getNombre());
                }
            });
        }

        // Si ya hay un visitante asignado, cargar sus datos
        if (visitanteActual != null) {
            cargarDatosVisitante();
        }
    }

    /**
     * Carga los datos del visitante actual en las etiquetas
     */
    private void cargarDatosVisitante() {
        if (visitanteActual == null) {
            limpiarCampos();
            return;
        }

        lblNombre.setText("👤 " + visitanteActual.getNombre());
        lblEdad.setText("🎂 Edad: " + visitanteActual.getEdad() + " años");
        lblEstatura.setText("📏 Estatura: " + visitanteActual.getEstatura() + " m");
        lblSaldo.setText("💰 Saldo: $" + String.format("%.0f", visitanteActual.getSaldoVirtual()));
        
        Ticket ticket = visitanteActual.getTicket();
        if (ticket != null) {
            String tipoTicket = switch (ticket.getTipo()) {
                case FAST_PASS -> "⚡ Fast-Pass";
                case FAMILIAR -> "👨‍👩‍👧‍👦 Familiar";
                case GENERAL -> "🎫 General";
            };
            lblTicket.setText("🎟 Ticket: " + tipoTicket);
        } else {
            lblTicket.setText("🎟 Ticket: Sin ticket");
        }

        actualizarFavoritos();
        lblPosicionFila.setText("Posición en fila: —");
        lblTiempoEspera.setText("Tiempo estimado: —");
    }

    private void limpiarCampos() {
        lblNombre.setText("Nombre: —");
        lblEdad.setText("Edad: —");
        lblEstatura.setText("Estatura: —");
        lblSaldo.setText("Saldo: —");
        lblTicket.setText("Ticket: —");
        lblPosicionFila.setText("Posición en fila: —");
        lblTiempoEspera.setText("Tiempo estimado: —");
        listaFavoritos.getItems().clear();
    }

    @FXML
    public void unirseAFila() {
        if (visitanteActual == null) {
            mostrarAlerta("No hay un visitante logueado.");
            return;
        }
        Atraccion atraccion = selectorAtraccion.getValue();
        if (atraccion == null) {
            mostrarAlerta("Selecciona una atracción primero.");
            return;
        }

        boolean resultado = visitanteActual.unirseAFila(atraccion);
        if (resultado) {
            int posicion = visitanteActual.consultarPosicionEnFila(atraccion);
            lblPosicionFila.setText("📍 Posición en fila: #" + posicion);
            lblTiempoEspera.setText("⏱ Tiempo estimado: " + atraccion.getTiempoEsperaEstimado() + " min");
            
            if (visitanteActual.getTicket() != null && 
                visitanteActual.getTicket().getTipo() == TipoTicket.FAST_PASS) {
                mostrarAlerta("¡Prioridad Fast-Pass activada!\nSerás atendido con prioridad alta.");
            }
        } else {
            if (!atraccion.estaDisponible()) {
                lblPosicionFila.setText("Atracción no disponible");
                lblTiempoEspera.setText("Estado: " + atraccion.getEstado());
            } else if (!atraccion.cumpleRequisitos(visitanteActual)) {
                lblPosicionFila.setText("No cumples los requisitos");
                lblTiempoEspera.setText("Requiere: Edad " + atraccion.getEdadMinima() + 
                                       "+ y Estatura " + atraccion.getAlturaMinima() + "m+");
                mostrarAlerta("No cumples con los requisitos mínimos:\n" +
                            "• Edad mínima: " + atraccion.getEdadMinima() + " años\n" +
                            "• Altura mínima: " + atraccion.getAlturaMinima() + " m");
            } else {
                lblPosicionFila.setText("No se pudo unir a la fila.");
                lblTiempoEspera.setText("Verifica tu saldo o ticket.");
            }
        }
    }

    @FXML
    public void agregarFavorito() {
        if (visitanteActual == null) {
            mostrarAlerta("No hay un visitante logueado.");
            return;
        }
        Atraccion atraccion = selectorFavorito.getValue();
        if (atraccion == null) {
            mostrarAlerta("Selecciona una atracción para agregar a favoritos.");
            return;
        }
        
        boolean agregado = visitanteActual.consultarFavoritos().agregar(atraccion);
        if (agregado) {
            actualizarFavoritos();
        } else {
            mostrarAlerta("Esa atracción ya está en tus favoritos.");
        }
    }

    @FXML
    public void quitarFavorito() {
        if (visitanteActual == null) return;
        Atraccion atraccion = selectorFavorito.getValue();
        if (atraccion == null) {
            mostrarAlerta("Selecciona una atracción para quitar de favoritos.");
            return;
        }
        visitanteActual.eliminarFavorito(atraccion);
        actualizarFavoritos();
    }

    private void actualizarFavoritos() {
        listaFavoritos.getItems().clear();
        if (visitanteActual == null) return;
        
        var favoritos = visitanteActual.consultarFavoritos();
        if (favoritos.estaVacio()) {
            listaFavoritos.getItems().add("⭐ No tienes atracciones favoritas aún");
        } else {
            for (Atraccion a : favoritos.aLista()) {
                String estadoEmoji = switch (a.getEstado()) {
                    case ACTIVA -> "🟢";
                    case EN_MANTENIMIENTO -> "🟠";
                    case CERRADA -> "🔴";
                };
                listaFavoritos.getItems().add(estadoEmoji + " " + a.getNombre() + " — " + a.getEstado());
            }
        }
    }

    public void cargarDatosVisitantePublico() {
        cargarDatosVisitante();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tech-Park UQ");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}