package com.techpark.controller;

import com.techpark.model.*;
import com.techpark.service.ParkDataService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperatorController {

    @FXML private Label lblOperadorNombre;
    @FXML private Label lblOperadorZona;
    @FXML private ComboBox<Atraccion> selectorAtraccion;
    @FXML private Label lblEstadoActual;
    @FXML private Label lblContadorVisitantes;
    @FXML private Label lblColaVirtual;
    @FXML private ListView<String> listaActividades;

    private final ParkDataService dataService;
    private Operador operadorActual;
    private Atraccion atraccionSeleccionada;

    public OperatorController(ParkDataService dataService) {
        this.dataService = dataService;
    }

    public void setOperadorInicial(Operador operador) {
        this.operadorActual = operador;
        if (operador != null) {
            lblOperadorNombre.setText("🔧 " + operador.getNombre());
            lblOperadorZona.setText("📍 Zona: " + 
                (operador.getZona() != null ? operador.getZona().getNombre() : "Sin asignar"));
        }
    }

    @FXML
    public void initialize() {
        // Cargar solo las atracciones de la zona del operador
        if (operadorActual != null && operadorActual.getZona() != null) {
            for (Atraccion a : operadorActual.getZona().getAtracciones().aLista()) {
                selectorAtraccion.getItems().add(a);
            }
        }

        // Configurar cómo se muestran las atracciones
        selectorAtraccion.setCellFactory(lv -> new ListCell<>() {
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
                    setText(estadoEmoji + " " + a.getNombre());
                }
            }
        });
        selectorAtraccion.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Atraccion a, boolean empty) {
                super.updateItem(a, empty);
                setText(empty || a == null ? null : a.getNombre());
            }
        });

        // Listener para mostrar info al seleccionar atracción
        selectorAtraccion.setOnAction(e -> actualizarInfoAtraccion());
    }

    private void actualizarInfoAtraccion() {
        atraccionSeleccionada = selectorAtraccion.getValue();
        if (atraccionSeleccionada == null) {
            lblEstadoActual.setText("Estado actual: —");
            lblContadorVisitantes.setText("Visitantes acumulados: —");
            lblColaVirtual.setText("Personas en cola: —");
            return;
        }

        lblEstadoActual.setText("Estado actual: " + atraccionSeleccionada.getEstado());
        lblContadorVisitantes.setText("Visitantes acumulados: " + atraccionSeleccionada.getContadorVisitantes() + " / 500");
        
        int enCola = atraccionSeleccionada.getCola() != null ? atraccionSeleccionada.getCola().tamano() : 0;
        lblColaVirtual.setText("Personas en cola: " + enCola);
    }

    @FXML
    public void cambiarEstado() {
        if (atraccionSeleccionada == null) {
            mostrarAlerta("Selecciona una atracción primero.");
            return;
        }

        // Diálogo para elegir nuevo estado
        ChoiceDialog<EstadoAtraccion> dialog = new ChoiceDialog<>(
            atraccionSeleccionada.getEstado(),
            EstadoAtraccion.values()
        );
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Cambiar estado de: " + atraccionSeleccionada.getNombre());
        dialog.setContentText("Nuevo estado:");

        dialog.showAndWait().ifPresent(nuevoEstado -> {
            MotivosCierre motivo = null;
            if (nuevoEstado == EstadoAtraccion.CERRADA) {
                // Preguntar motivo
                ChoiceDialog<MotivosCierre> motivoDialog = new ChoiceDialog<>(
                    MotivosCierre.OTRO, MotivosCierre.values()
                );
                motivoDialog.setTitle("Motivo de Cierre");
                motivoDialog.setHeaderText("¿Por qué se cierra " + atraccionSeleccionada.getNombre() + "?");
                motivoDialog.setContentText("Motivo:");
                motivo = motivoDialog.showAndWait().orElse(MotivosCierre.OTRO);
            }

            operadorActual.gestionarEstadoAtraccion(atraccionSeleccionada, nuevoEstado, motivo);
            registrarActividad("Cambió estado de " + atraccionSeleccionada.getNombre() + 
                             " a " + nuevoEstado);
            actualizarInfoAtraccion();
        });
    }

    @FXML
    public void registrarRevision() {
        if (atraccionSeleccionada == null) {
            mostrarAlerta("Selecciona una atracción primero.");
            return;
        }

        operadorActual.registrarRevisionTecnica(atraccionSeleccionada);
        registrarActividad("Registró revisión técnica de " + atraccionSeleccionada.getNombre() + 
                         " → Contador reiniciado a 0");
        actualizarInfoAtraccion();
        
        mostrarAlerta("✅ Revisión técnica registrada.\n" +
                     "Atracción reactivada y contador reiniciado.");
    }

    @FXML
    public void procesarSiguiente() {
        if (atraccionSeleccionada == null) {
            mostrarAlerta("Selecciona una atracción primero.");
            return;
        }

        Visitante siguiente = operadorActual.procesarSiguienteEnFila(atraccionSeleccionada);
        if (siguiente != null) {
            registrarActividad("Procesó a " + siguiente.getNombre() + 
                             " en " + atraccionSeleccionada.getNombre());
            actualizarInfoAtraccion();
            mostrarAlerta("✅ Visitante procesado: " + siguiente.getNombre());
        } else {
            mostrarAlerta("No hay visitantes en la cola o la atracción no está disponible.");
        }
    }

    private void registrarActividad(String actividad) {
        String hora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        listaActividades.getItems().add(0, "[" + hora + "] " + actividad);
        
        // Mantener solo últimas 50 actividades
        if (listaActividades.getItems().size() > 50) {
            listaActividades.getItems().remove(50, listaActividades.getItems().size());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tech-Park UQ - Operador");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}