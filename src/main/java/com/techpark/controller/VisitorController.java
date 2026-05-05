package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VisitorController {

    @FXML private ComboBox<Visitante> selectorVisitante;
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

    @FXML
    public void initialize() {
        // Cargar visitantes en el selector
        selectorVisitante.getItems().addAll(dataService.getVisitantes());
        selectorVisitante.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Visitante v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getNombre());
            }
        });
        selectorVisitante.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Visitante v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : v.getNombre());
            }
        });

        // Cargar atracciones en selectores
        selectorAtraccion.getItems().addAll(dataService.getAtracciones());
        selectorFavorito.getItems().addAll(dataService.getAtracciones());

        for (ComboBox<Atraccion> cb : new ComboBox[]{selectorAtraccion, selectorFavorito}) {
            cb.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Atraccion a, boolean empty) {
                    super.updateItem(a, empty);
                    setText(empty || a == null ? null : a.getNombre());
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
    }

    @FXML
    public void cargarVisitante() {
        visitanteActual = selectorVisitante.getValue();
        if (visitanteActual == null) return;

        lblNombre.setText("Nombre: " + visitanteActual.getNombre());
        lblEdad.setText("Edad: " + visitanteActual.getEdad() + " años");
        lblEstatura.setText("Estatura: " + visitanteActual.getEstatura() + " m");
        lblSaldo.setText("Saldo: $" + String.format("%.0f", visitanteActual.getSaldoVirtual()));
        lblTicket.setText("Ticket: " + (visitanteActual.getTicket() != null
                ? visitanteActual.getTicket().getTipo() : "Sin ticket"));

        actualizarFavoritos();
    }

    @FXML
    public void unirseAFila() {
        if (visitanteActual == null) {
            mostrarAlerta("Selecciona un visitante primero.");
            return;
        }
        Atraccion atraccion = selectorAtraccion.getValue();
        if (atraccion == null) {
            mostrarAlerta("Selecciona una atracción.");
            return;
        }

        boolean resultado = visitanteActual.unirseAFila(atraccion);
        if (resultado) {
            int posicion = visitanteActual.consultarPosicionEnFila(atraccion);
            lblPosicionFila.setText("Posición en fila: " + posicion);
            lblTiempoEspera.setText("Tiempo estimado: " + atraccion.getTiempoEsperaEstimado() + " min");
        } else {
            lblPosicionFila.setText("No se pudo unir a la fila.");
            lblTiempoEspera.setText(atraccion.estaDisponible()
                    ? "Verifica requisitos de acceso."
                    : "Atracción no disponible: " + atraccion.getEstado());
        }
    }

    @FXML
    public void agregarFavorito() {
        if (visitanteActual == null) {
            mostrarAlerta("Selecciona un visitante primero.");
            return;
        }
        Atraccion atraccion = selectorFavorito.getValue();
        if (atraccion == null) return;
        visitanteActual.registrarFavorito(atraccion);
        actualizarFavoritos();
    }

    @FXML
    public void quitarFavorito() {
        if (visitanteActual == null) return;
        Atraccion atraccion = selectorFavorito.getValue();
        if (atraccion == null) return;
        visitanteActual.eliminarFavorito(atraccion);
        actualizarFavoritos();
    }

    private void actualizarFavoritos() {
        listaFavoritos.getItems().clear();
        if (visitanteActual == null) return;
        for (Atraccion a : visitanteActual.consultarFavoritos().aLista()) {
            listaFavoritos.getItems().add(a.getNombre() + " — " + a.getEstado());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}